import pool from '../config/db.js';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Log file path
const LOG_FILE = path.join(__dirname, '../logs/survey_responses.log');

// Ensure logs directory exists
const logsDir = path.join(__dirname, '../logs');
if (!fs.existsSync(logsDir)) {
  fs.mkdirSync(logsDir, { recursive: true });
}

// Helper function to write logs (with error handling)
function writeLog(message) {
  try {
    const timestamp = new Date().toISOString();
    const logMessage = `[${timestamp}] ${message}\n`;
    console.log(message); // Always log to console
    
    // Try to write to file, but don't fail if it doesn't work
    try {
      if (!fs.existsSync(logsDir)) {
        fs.mkdirSync(logsDir, { recursive: true });
      }
      fs.appendFileSync(LOG_FILE, logMessage, 'utf8');
    } catch (fileError) {
      // If file logging fails, just continue with console logging
      console.error('Failed to write to log file:', fileError.message);
    }
  } catch (error) {
    // If logging completely fails, at least log to console
    console.log(message);
  }
}

export const saveResponses = async (req, res, next) => {
  const { user_id, survey_id, area_id, ward_id, answers } = req.body;

  // Validate required fields
  if (!user_id || !survey_id || !area_id || !ward_id || !Array.isArray(answers)) {
    return res.status(400).json({
      success: false,
      message: "Missing required fields"
    });
  }

  const connection = await pool.getConnection();

  try {
    await connection.beginTransaction();

    writeLog(`=== NEW SURVEY SUBMISSION ===`);
    writeLog(`Saving ${answers.length} answers for user ${user_id}, survey ${survey_id}, area ${area_id}, ward ${ward_id}`);

    for (const ans of answers) {
      const { question_id, answer_text, selected_option_id, selected_option_ids } = ans;

      if (!question_id) {
        writeLog(`WARNING: Skipping answer with missing question_id: ${JSON.stringify(ans)}`);
        continue;
      }

      writeLog(`Processing answer for question ${question_id}:`);
      writeLog(`  - Raw answer object: ${JSON.stringify(ans)}`);
      writeLog(`  - answer_text: ${answer_text !== undefined ? answer_text : 'undefined'} (type: ${typeof answer_text})`);
      writeLog(`  - selected_option_id: ${selected_option_id !== undefined ? selected_option_id : 'undefined'} (type: ${typeof selected_option_id})`);
      writeLog(`  - selected_option_ids: ${selected_option_ids !== undefined ? (Array.isArray(selected_option_ids) ? selected_option_ids.length + ' items' : selected_option_ids) : 'undefined'}`);

      // First, check what column name actually exists in the database
      const [columns] = await connection.execute('SHOW COLUMNS FROM responses');
      const columnNames = columns.map(col => col.Field);
      const optionColumn = columnNames.find(col => 
        col.toLowerCase() === 'selected_option_id' || 
        col.toLowerCase() === 'answered_option_id' ||
        col.toLowerCase() === 'option_id'
      ) || 'selected_option_id'; // fallback
      
      writeLog(`  -> Using column name: ${optionColumn}`);

      // CASE 1: multiple-choice (checkbox) - check this first
      if (Array.isArray(selected_option_ids) && selected_option_ids.length > 0) {
        writeLog(`  -> Multiple choice: ${selected_option_ids.length} options selected`);
        for (const opt of selected_option_ids) {
          await connection.execute(
            `INSERT INTO responses (user_id, survey_id, area_id, ward_id, question_id, ${optionColumn}, answer_text)
             VALUES (?, ?, ?, ?, ?, ?, NULL)`,
            [user_id, survey_id, area_id, ward_id, question_id, opt]
          );
          writeLog(`    ✓ Saved option_id: ${opt}`);
        }
      }
      // CASE 2: single choice (radio button) - check this BEFORE text to prioritize options
      else if (selected_option_id !== undefined && selected_option_id !== null && selected_option_id !== '' && Number(selected_option_id) > 0) {
        writeLog(`  -> Single choice: option_id ${selected_option_id}`);
        await connection.execute(
          `INSERT INTO responses (user_id, survey_id, area_id, ward_id, question_id, ${optionColumn}, answer_text)
           VALUES (?, ?, ?, ?, ?, ?, NULL)`,
          [user_id, survey_id, area_id, ward_id, question_id, Number(selected_option_id)]
        );
        writeLog(`    ✓ Saved option_id: ${selected_option_id}`);
      }
      // CASE 3: text answer - check if answer_text exists and is not empty
      else if (answer_text !== undefined && answer_text !== null && answer_text !== '' && String(answer_text).trim() !== '') {
        writeLog(`  -> Text answer: "${answer_text}"`);
        await connection.execute(
          `INSERT INTO responses (user_id, survey_id, area_id, ward_id, question_id, ${optionColumn}, answer_text)
           VALUES (?, ?, ?, ?, ?, NULL, ?)`,
          [user_id, survey_id, area_id, ward_id, question_id, String(answer_text).trim()]
        );
        writeLog(`    ✓ Saved text answer`);
      }
      // CASE 4: Invalid answer (should not happen)
      else {
        writeLog(`  -> ERROR: Invalid answer format for question ${question_id}`);
        writeLog(`     Full answer object: ${JSON.stringify(ans, null, 2)}`);
        throw new Error(`Invalid answer format for question ${question_id}. Answer must have either answer_text, selected_option_id, or selected_option_ids.`);
      }
    }

    await connection.commit();

    writeLog(`✓ All responses saved successfully!`);
    writeLog(`=== END SUBMISSION ===\n`);

    return res.json({
      success: true,
      message: "Responses saved"
    });

  } catch (error) {
    await connection.rollback();
    next(error);
  } finally {
    connection.release();
  }
};
