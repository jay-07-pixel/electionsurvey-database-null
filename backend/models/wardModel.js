import db from '../config/db.js';

/**
 * Get wards by area ID
 * @param {number} areaId - The area ID to filter wards
 * @returns {Promise<Array>} Array of ward objects
 */
export async function getWardsByArea(areaId) {
  try {
    const [rows] = await db.query(
      'SELECT id, ward_name FROM wards WHERE area_id = ?',
      [areaId]
    );
    
    return rows;
  } catch (error) {
    console.error('Error in getWardsByArea:', error);
    throw error;
  }
}





