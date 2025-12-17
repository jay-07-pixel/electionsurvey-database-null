import { findUserByPhone } from '../models/userModel.js';

/**
 * Login controller
 * @param {Object} req - Express request object
 * @param {Object} res - Express response object
 * @param {Function} next - Express next middleware function
 */
export async function login(req, res, next) {
  try {
    const { phone, password } = req.body;

    // Validate required fields
    if (!phone || !password) {
      return res.status(400).json({
        success: false,
        message: 'Phone and password are required'
      });
    }

    // Find user by phone
    const user = await findUserByPhone(phone);

    if (!user) {
      return res.status(401).json({
        success: false,
        message: 'Invalid credentials'
      });
    }

    // Compare plain text password
    if (password !== user.password_hash) {
      return res.status(401).json({
        success: false,
        message: 'Invalid credentials'
      });
    }

    // Login successful
    return res.status(200).json({
      success: true,
      data: {
        id: user.id,
        name: user.name,
        phone: user.phone
      }
    });

  } catch (error) {
    console.error('Login error:', error);
    return res.status(500).json({
      success: false,
      message: 'Internal server error'
    });
  }
}





