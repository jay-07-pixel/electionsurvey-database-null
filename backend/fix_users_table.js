import db from './config/db.js';

async function checkAndFixUsersTable() {
  let connection;
  try {
    connection = await db.getConnection();
    
    // Check current table structure
    console.log('Checking users table structure...');
    const [columns] = await connection.execute('SHOW COLUMNS FROM users');
    console.log('Current columns:', columns.map(c => c.Field));
    
    // Check if phone column exists
    const hasPhone = columns.some(col => col.Field.toLowerCase() === 'phone');
    
    if (!hasPhone) {
      console.log('Phone column not found. Adding it...');
      
      // Add phone column
      await connection.execute(`
        ALTER TABLE users 
        ADD COLUMN phone VARCHAR(15) UNIQUE AFTER name
      `);
      
      console.log('✓ Phone column added successfully!');
      
      // If there are existing users, you might want to update them
      const [users] = await connection.execute('SELECT id, name FROM users');
      if (users.length > 0) {
        console.log(`\n⚠️  Found ${users.length} existing users without phone numbers.`);
        console.log('You may need to update them manually or provide a migration script.');
      }
    } else {
      console.log('✓ Phone column already exists!');
    }
    
    // Show final structure
    const [finalColumns] = await connection.execute('SHOW COLUMNS FROM users');
    console.log('\nFinal table structure:');
    finalColumns.forEach(col => {
      console.log(`  - ${col.Field} (${col.Type})`);
    });
    
  } catch (error) {
    console.error('Error:', error.message);
    if (error.code === 'ER_DUP_FIELDNAME') {
      console.log('Phone column already exists with a different case.');
    }
  } finally {
    if (connection) connection.release();
    process.exit();
  }
}

checkAndFixUsersTable();

