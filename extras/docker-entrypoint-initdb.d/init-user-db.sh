#!/bin/bash
set -e

/usr/local/bin/psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE USER bttf;
	CREATE DATABASE bttf;
	GRANT ALL PRIVILEGES ON DATABASE bttf TO bttf;
        ALTER USER bttf PASSWORD 'bttfadmin';
EOSQL
