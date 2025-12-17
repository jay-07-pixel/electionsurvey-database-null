#!/bin/bash

# Script to check server status and connectivity

echo "=== Server Status Check ==="
echo ""

# Check if PM2 is running
echo "1. Checking PM2 status..."
pm2 status

echo ""
echo "2. Checking if port 4000 is listening..."
netstat -tuln | grep 4000 || ss -tuln | grep 4000

echo ""
echo "3. Testing local server connection..."
curl -s http://localhost:4000/ || echo "Failed to connect locally"

echo ""
echo "4. Testing login endpoint locally..."
curl -X POST http://localhost:4000/api/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"9822961688","password":"12345678"}' \
  -s || echo "Failed to connect"

echo ""
echo "5. Checking firewall status..."
if command -v ufw &> /dev/null; then
    sudo ufw status
elif command -v firewall-cmd &> /dev/null; then
    sudo firewall-cmd --list-all
else
    echo "No firewall tool found"
fi

echo ""
echo "6. Checking server IP address..."
hostname -I || ip addr show | grep "inet " | grep -v 127.0.0.1

echo ""
echo "=== Check Complete ==="

