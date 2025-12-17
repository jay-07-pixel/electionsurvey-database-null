# Server Fix Instructions - Survey API Route Error

## Problem
The server is returning "Cannot GET /api/surveys/1/questions" because the survey route files are missing or incorrect.

## Solution
Upload these THREE files to your server at `/root/backend/`:

---

## 1. `/root/backend/models/surveyModel.js`

```javascript
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
```

---

## 2. `/root/backend/controllers/surveyController.js`

```javascript
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
```

---

## 3. `/root/backend/routes/surveyRoutes.js`

```javascript
import express from "express";
import { getSurveyQuestions } from "../controllers/surveyController.js";

const router = express.Router();

router.get("/surveys/:surveyId/questions", getSurveyQuestions);

export default router;
```

---

## Quick Fix Command (Run on Server)

```bash
# Fix surveyModel.js
cat > /root/backend/models/surveyModel.js << 'EOFMODEL'
import db from "../config/db.js";

export async function getQuestionsWithOptions(surveyId) {
  const [questions] = await db.execute(
    "SELECT id, question_text, type FROM questions WHERE survey_id = ?",
    [surveyId]
  );

  if (!questions.length) return [];

  const questionIds = questions.map(q => q.id);

  const [options] = await db.execute(
    `SELECT id, question_id, option_text 
     FROM options 
     WHERE question_id IN (${questionIds.map(() => "?").join(",")})`,
    questionIds
  );

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

# Fix surveyController.js
cat > /root/backend/controllers/surveyController.js << 'EOFCONTROLLER'
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

# Fix surveyRoutes.js
cat > /root/backend/routes/surveyRoutes.js << 'EOFROUTES'
import express from "express";
import { getSurveyQuestions } from "../controllers/surveyController.js";

const router = express.Router();

router.get("/surveys/:surveyId/questions", getSurveyQuestions);

export default router;
EOFROUTES

# Restart PM2
pm2 restart election-api

# Check logs
pm2 logs election-api --lines 20
```

---

## After Uploading

1. **Restart PM2:**
   ```bash
   pm2 restart election-api
   ```

2. **Test the API:**
   ```bash
   curl http://143.110.252.32:4000/api/surveys/1/questions
   ```

3. **Check PM2 logs:**
   ```bash
   pm2 logs election-api --lines 30
   ```

You should see no errors and the API should return JSON with questions.

