import mysql from 'mysql2/promise';
import dotenv from 'dotenv';

dotenv.config();

async function verifyDatabase() {
  let connection;
  
  try {
    // Create connection
    connection = await mysql.createConnection({
      host: process.env.DB_HOST,
      user: process.env.DB_USER,
      password: process.env.DB_PASS,
      database: process.env.DB_NAME
    });

    console.log('✅ Connected to database:', process.env.DB_NAME);

    // Check if tables exist
    const [tables] = await connection.query(`
      SELECT TABLE_NAME 
      FROM information_schema.TABLES 
      WHERE TABLE_SCHEMA = ?
    `, [process.env.DB_NAME]);

    console.log('\n📊 Tables found:', tables.length);
    tables.forEach(t => console.log('  -', t.TABLE_NAME));

    const requiredTables = ['users', 'areas', 'wards', 'questions', 'options', 'responses'];
    const existingTables = tables.map(t => t.TABLE_NAME);
    const missingTables = requiredTables.filter(t => !existingTables.includes(t));

    if (missingTables.length > 0) {
      console.log('\n❌ Missing tables:', missingTables);
      console.log('\n⚠️  Run database_setup.sql to create missing tables!');
    } else {
      console.log('\n✅ All required tables exist!');
    }

    // Check users count
    try {
      const [users] = await connection.query('SELECT COUNT(*) as count FROM users');
      console.log('\n👥 Users in database:', users[0].count);
      
      if (users[0].count === 0) {
        console.log('⚠️  No users found! Insert test users.');
      } else {
        const [userList] = await connection.query('SELECT id, name, phone FROM users LIMIT 5');
        console.log('Sample users:');
        userList.forEach(u => console.log(`  - ${u.name} (${u.phone})`));
      }
    } catch (err) {
      console.log('\n❌ Error checking users:', err.message);
    }

    // Check areas count
    try {
      const [areas] = await connection.query('SELECT COUNT(*) as count FROM areas');
      console.log('\n📍 Areas in database:', areas[0].count);
    } catch (err) {
      console.log('❌ Error checking areas:', err.message);
    }

    // Check questions count
    try {
      const [questions] = await connection.query('SELECT COUNT(*) as count FROM questions');
      console.log('❓ Questions in database:', questions[0].count);
    } catch (err) {
      console.log('❌ Error checking questions:', err.message);
    }

    console.log('\n✅ Database verification complete!');

  } catch (error) {
    console.error('\n❌ Database Error:', error.message);
    console.error('\nCheck your .env file:');
    console.error('  DB_HOST:', process.env.DB_HOST);
    console.error('  DB_USER:', process.env.DB_USER);
    console.error('  DB_NAME:', process.env.DB_NAME);
    console.error('  DB_PASS:', process.env.DB_PASS ? '***' : 'NOT SET');
  } finally {
    if (connection) {
      await connection.end();
    }
  }
}

verifyDatabase();



