#!/bin/bash
# ./001-db-user.sh root password ${user}

echo "Creating Swimming club database and user."
echo "Input params:"
echo "- admin user: $1"
echo "- admin pwd: $2"
echo "- database/user: $3"
echo ""

# Check input params
if [ -z "$1" ]
  then
    echo "No required input param 'admin user' -> EXIT!"
    exit
fi
if [ -z "$2" ]
  then
    echo "No required input param 'admin pwd' -> EXIT!"
    exit
fi
if [ -z "$3" ]
  then
    echo "No required input param 'database/user' -> EXIT!"
    exit
fi

# Connect to database and run script
mysql -u $1 -p$2 < ./$3/001-db-user.sql;

echo "--- SUCCESS ---"

exit
