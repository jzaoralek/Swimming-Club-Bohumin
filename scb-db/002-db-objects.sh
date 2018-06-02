#!/bin/bash
echo "Creating database objects, user/database: $1"
echo ""

# Connect to database and run script
mysql -u $1 -p$1 "kosatky" < ./scb-db-objects.sql;

echo "--- SUCCESS ---"

exit