import { getWardsByArea } from '../models/wardModel.js';

/**
 * Get wards by area controller
 * @param {Object} req - Express request object
 * @param {Object} res - Express response object
 * @param {Function} next - Express next middleware function
 */
export async function getWards(req, res, next) {
  try {
    const { areaId } = req.params;
    
    if (!areaId) {
      return res.status(400).json({
        success: false,
        message: 'Area ID is required'
      });
    }
    
    const rows = await getWardsByArea(areaId);
    
    return res.status(200).json({
      success: true,
      data: rows
    });
  } catch (error) {
    console.error('Error in getWards controller:', error);
    return res.status(500).json({
      success: false,
      message: 'Internal server error'
    });
  }
}





