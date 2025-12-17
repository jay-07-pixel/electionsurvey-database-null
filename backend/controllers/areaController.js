import { getAllAreas } from '../models/areaModel.js';

/**
 * Get all areas controller
 * @param {Object} req - Express request object
 * @param {Object} res - Express response object
 * @param {Function} next - Express next middleware function
 */
export async function getAreas(req, res, next) {
  try {
    const rows = await getAllAreas();
    
    return res.status(200).json({
      success: true,
      data: rows
    });
  } catch (error) {
    console.error('Error in getAreas controller:', error);
    return res.status(500).json({
      success: false,
      message: 'Internal server error'
    });
  }
}





