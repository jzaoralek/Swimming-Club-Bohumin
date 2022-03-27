#!/bin/bash
# ./002-db-objects.sh ${user}

echo "Creating database objects, user/database: $1 $2 $3"
echo ""

cd $4

# Connect to database and run script
mysql -u $1 -p$2 $3 < ./V1.0.0__init-cust-db-objects.sql;

echo "--- SUCCESS ---"

exit