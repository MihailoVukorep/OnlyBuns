#!/bin/bash

cat << EOF > cat_tables_cmds.txt
\l
\c jpa
\dt
SELECT * from $1;
EOF

sudo -u postgres psql -f cat_tables_cmds.txt
