#!/bin/bash
# ./003-db-data.sh ${user}

echo "Creating database data. User/database $1 $2 $3"
echo ""

cd $4

# Connect to database and run script
mysql -u $1 -p$2 $3 < ./V1.0.0__init-cust-db-data.sql;

echo "--- SUCCESS ---"

exit