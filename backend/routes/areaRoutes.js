import express from 'express';
import { getAreas } from '../controllers/areaController.js';

const router = express.Router();

// GET /areas - Get all areas
router.get('/areas', getAreas);

export default router;





