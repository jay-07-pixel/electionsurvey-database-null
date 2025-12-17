#!/bin/bash
# Script to fix surveyController.js export issue on server

echo "=== Fixing surveyController.js export ==="

# Navigate to backend directory
cd /root/backend || exit 1

# Backup existing file
if [ -f "controllers/surveyController.js" ]; then
    cp controllers/surveyController.js controllers/surveyController.js.backup
    echo "✓ Backed up existing file"
fi

# Create correct surveyController.js
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

echo "✓ Created surveyController.js"

# Verify the file
echo ""
echo "=== Verifying file content ==="
cat controllers/surveyController.js

echo ""
echo "=== Checking for export ==="
if grep -q "export async function getSurveyQuestions" controllers/surveyController.js; then
    echo "✓ Export found correctly"
else
    echo "✗ Export NOT found - something went wrong"
    exit 1
fi

echo ""
echo "=== Restarting PM2 ==="
pm2 restart election-api

echo ""
echo "=== Checking logs ==="
sleep 2
pm2 logs election-api --lines 10 --nostream

echo ""
echo "=== Fix complete! ==="

