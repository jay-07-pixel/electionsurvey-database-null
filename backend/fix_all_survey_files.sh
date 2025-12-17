#!/bin/bash
# Script to fix all survey-related files on server

echo "=== Fixing Survey Files ==="

cd /root/backend || exit 1

# Fix surveyModel.js
echo "1. Fixing surveyModel.js..."
cat > models/surveyModel.js << 'EOF'
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
EOF

# Fix surveyController.js
echo "2. Fixing surveyController.js..."
cat > controllers/surveyController.js << 'EOF'
import { getQuestionsWithOptions } from "../models/surveyModel.js";

export async function getSurveyQuestions(req, res, next) {
  try {
    const { surveyId } = req.params;

    const questions = await getQuestionsWithOptions(surveyId);

    return res.json({
      success: true,
      data: questions
    });

  } catch (error) {
    next(error);
  }
}
EOF

# Fix surveyRoutes.js
echo "3. Fixing surveyRoutes.js..."
cat > routes/surveyRoutes.js << 'EOF'
import express from "express";
import { getSurveyQuestions } from "../controllers/surveyController.js";

const router = express.Router();

router.get("/surveys/:surveyId/questions", getSurveyQuestions);

export default router;
EOF

echo ""
echo "=== Verifying exports ==="
grep -q "export async function getQuestionsWithOptions" models/surveyModel.js && echo "✓ surveyModel.js export OK" || echo "✗ surveyModel.js export FAILED"
grep -q "export async function getSurveyQuestions" controllers/surveyController.js && echo "✓ surveyController.js export OK" || echo "✗ surveyController.js export FAILED"
grep -q "export default router" routes/surveyRoutes.js && echo "✓ surveyRoutes.js export OK" || echo "✗ surveyRoutes.js export FAILED"

echo ""
echo "=== Restarting PM2 ==="
pm2 restart election-api

echo ""
echo "=== Waiting 3 seconds for startup ==="
sleep 3

echo ""
echo "=== Checking logs ==="
pm2 logs election-api --lines 15 --nostream

echo ""
echo "=== Fix complete! ==="

