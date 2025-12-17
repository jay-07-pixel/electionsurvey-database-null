import express from 'express';
import { getWards } from '../controllers/wardController.js';

const router = express.Router();

// GET /wards/:areaId - Get wards by area ID
router.get('/wards/:areaId', getWards);

export default router;





