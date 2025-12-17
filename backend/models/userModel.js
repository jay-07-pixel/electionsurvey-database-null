import db from '../config/db.js';

/**
 * Find a user by phone number
 * @param {string} phone - The phone number to search for
 * @returns {Promise<Object|null>} User object or null if not found
 */
export async function findUserByPhone(phone) {
  try {
    const [rows] = await db.query(
      'SELECT * FROM users WHERE phone = ? LIMIT 1',
      [phone]
    );
    
    return rows.length > 0 ? rows[0] : null;
  } catch (error) {
    console.error('Error in findUserByPhone:', error);
    throw error;
  }
}





