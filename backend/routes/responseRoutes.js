import express from 'express';
import { saveResponses } from '../controllers/responseController.js';

const router = express.Router();

router.post('/responses', saveResponses);

export default router;
