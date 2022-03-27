#!/bin/bash
# ./002-db-objects.sh ${user}

echo "Creating database objects, user/database: $1 $2 $3"
echo ""

# Connect to database and run script
mysql -u $1 -p$2 $3 < ./scb-db-objects.sql;

echo "--- SUCCESS ---"

exit