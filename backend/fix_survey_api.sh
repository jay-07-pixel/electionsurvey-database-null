#!/bin/bash
# Complete fix for Survey API - Run this on your server
# This fixes all three files: surveyModel.js, surveyController.js, and surveyRoutes.js

echo "=== Fixing Survey API Files ==="
echo ""

cd /root/backend || exit 1

# Backup existing files
echo "1. Backing up existing files..."
mkdir -p backups
cp models/surveyModel.js backups/surveyModel.js.backup.$(date +%Y%m%d_%H%M%S) 2>/dev/null || true
cp controllers/surveyController.js backups/surveyController.js.backup.$(date +%Y%m%d_%H%M%S) 2>/dev/null || true
cp routes/surveyRoutes.js backups/surveyRoutes.js.backup.$(date +%Y%m%d_%H%M%S) 2>/dev/null || true
echo "   ✓ Backups created"

# Fix surveyModel.js
echo ""
echo "2. Fixing models/surveyModel.js..."
cat > models/surveyModel.js << 'EOFMODEL'
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
EOFMODEL
echo "   ✓ surveyModel.js fixed"

# Fix surveyController.js
echo ""
echo "3. Fixing controllers/surveyController.js..."
cat > controllers/surveyController.js << 'EOFCONTROLLER'
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
EOFCONTROLLER
echo "   ✓ surveyController.js fixed"

# Fix surveyRoutes.js
echo ""
echo "4. Fixing routes/surveyRoutes.js..."
cat > routes/surveyRoutes.js << 'EOFROUTES'
import express from "express";
import { getSurveyQuestions } from "../controllers/surveyController.js";

const router = express.Router();

router.get("/surveys/:surveyId/questions", getSurveyQuestions);

export default router;
EOFROUTES
echo "   ✓ surveyRoutes.js fixed"

# Verify exports
echo ""
echo "5. Verifying exports..."
if grep -q "export async function getQuestionsWithOptions" models/surveyModel.js; then
    echo "   ✓ surveyModel.js exports getQuestionsWithOptions"
else
    echo "   ✗ surveyModel.js export NOT found!"
    exit 1
fi

if grep -q "export async function getSurveyQuestions" controllers/surveyController.js; then
    echo "   ✓ surveyController.js exports getSurveyQuestions"
else
    echo "   ✗ surveyController.js export NOT found!"
    exit 1
fi

if grep -q "export default router" routes/surveyRoutes.js; then
    echo "   ✓ surveyRoutes.js exports default router"
else
    echo "   ✗ surveyRoutes.js export NOT found!"
    exit 1
fi

# Restart PM2
echo ""
echo "6. Restarting PM2..."
pm2 restart election-api

echo ""
echo "7. Waiting 3 seconds for server to start..."
sleep 3

# Check logs
echo ""
echo "8. Checking PM2 logs (last 20 lines)..."
pm2 logs election-api --lines 20 --nostream

echo ""
echo "=== Fix Complete! ==="
echo ""
echo "Test the API with:"
echo "  curl http://143.110.252.32:4000/api/surveys/1/questions"
echo ""

