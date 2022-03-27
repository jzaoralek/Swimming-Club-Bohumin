#!/bin/bash
# ./003-db-data.sh ${user}

echo "Creating database data. User/database $1"
echo ""

# Connect to database and run script
mysql -u $1 -p$1 $1 < ./scb-db-data.sql;

echo "--- SUCCESS ---"

exit