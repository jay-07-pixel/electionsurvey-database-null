import db from '../config/db.js';

/**
 * Get all areas
 * @returns {Promise<Array>} Array of area objects
 */
export async function getAllAreas() {
  try {
    const [rows] = await db.query(
      'SELECT id, area_name FROM areas'
    );
    
    return rows;
  } catch (error) {
    console.error('Error in getAllAreas:', error);
    throw error;
  }
}





