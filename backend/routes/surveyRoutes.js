import express from "express";
import { getSurveyQuestions } from "../controllers/surveyController.js";

const router = express.Router();

router.get("/surveys/:surveyId/questions", getSurveyQuestions);

export default router;