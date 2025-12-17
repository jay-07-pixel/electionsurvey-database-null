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