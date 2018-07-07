#!/bin/bash
# ./002-db-objects.sh ${user}

echo "Creating database objects, user/database: $1"
echo ""

# Connect to database and run script
mysql -u $1 -p$1 "scb" < ./scb-db-objects.sql;

echo "--- SUCCESS ---"

exit