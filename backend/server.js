import express from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
import db from './config/db.js';

import authRoutes from './routes/authRoutes.js';
import areaRoutes from './routes/areaRoutes.js';
import wardRoutes from './routes/wardRoutes.js';
import surveyRoutes from './routes/surveyRoutes.js';
import responseRoutes from './routes/responseRoutes.js';
import testRoutes from './routes/testRoutes.js';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 4000;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Test route
app.get('/', (req, res) => {
  res.json({ message: 'Election Survey API is running' });
});

// Mount all routes
app.use('/api', authRoutes);     // /api/login
app.use('/api', areaRoutes);     // /api/areas
app.use('/api', wardRoutes);     // /api/wards/:areaId
app.use('/api', surveyRoutes);   // /api/surveys/:surveyId/questions
app.use('/api', responseRoutes); // /api/responses
app.use('/api', testRoutes);     // /api/test-db, /api/test-tables, /api/test-users

// Error handling middleware (must be last)
app.use((err, req, res, next) => {
  console.error('Error:', err);
  res.status(500).json({
    success: false,
    message: 'Internal server error',
    error: process.env.NODE_ENV === 'development' ? err.message : undefined
  });
});

// 404 handler
app.use((req, res) => {
  res.status(404).json({
    success: false,
    message: 'Route not found'
  });
});

// Start server - listen on all interfaces (0.0.0.0) to accept external connections
app.listen(PORT, '0.0.0.0', () => {
  console.log(`Server is running on port ${PORT}`);
  console.log(`Server accessible at http://0.0.0.0:${PORT}`);
});
