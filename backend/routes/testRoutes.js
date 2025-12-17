import express from 'express';
import db from '../config/db.js';

const router = express.Router();

// Test database connection
router.get('/test-db', async (req, res) => {
  try {
    const [rows] = await db.query('SELECT 1 as test');
    res.json({
      success: true,
      message: 'Database connection successful',
      data: rows
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Database connection failed',
      error: error.message
    });
  }
});

// Test if tables exist
router.get('/test-tables', async (req, res) => {
  try {
    const [tables] = await db.query(`
      SELECT TABLE_NAME 
      FROM information_schema.TABLES 
      WHERE TABLE_SCHEMA = DATABASE()
    `);
    
    const tableNames = tables.map(t => t.TABLE_NAME);
    const requiredTables = ['users', 'areas', 'wards', 'questions', 'options', 'responses'];
    const missingTables = requiredTables.filter(t => !tableNames.includes(t));
    
    res.json({
      success: true,
      tables: tableNames,
      requiredTables: requiredTables,
      missingTables: missingTables,
      allTablesExist: missingTables.length === 0
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Error checking tables',
      error: error.message
    });
  }
});

// Test if users exist
router.get('/test-users', async (req, res) => {
  try {
    const [users] = await db.query('SELECT id, name, phone FROM users LIMIT 5');
    res.json({
      success: true,
      count: users.length,
      users: users
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      message: 'Error querying users',
      error: error.message
    });
  }
});

export default router;



