import db from "../config/db.js";

export async function getQuestionsWithOptions(surveyId) {
  // Get all questions for the survey
  const [questions] = await db.execute(
    "SELECT id, question_text, type FROM questions WHERE survey_id = ?",
    [surveyId]
  );

  if (!questions.length) return [];

  const questionIds = questions.map(q => q.id);

  // Get all options in one query
  const [options] = await db.execute(
    `SELECT id, question_id, option_text 
     FROM options 
     WHERE question_id IN (${questionIds.map(() => "?").join(",")})`,
    questionIds
  );

  // Map options into question objects
  const questionMap = {};
  
  questions.forEach(q => {
    questionMap[q.id] = { 
      ...q, 
      options: [] 
    };
  });

  options.forEach(opt => {
    questionMap[opt.question_id].options.push({
      id: opt.id,
      option_text: opt.option_text
    });
  });

  return Object.values(questionMap);
}