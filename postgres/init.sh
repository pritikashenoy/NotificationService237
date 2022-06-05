#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE notifydb;
    GRANT ALL PRIVILEGES ON DATABASE notifydb TO cs237;
    \c notifydb
    CREATE TABLE notifications (
        ID serial PRIMARY KEY,
        TOPIC varchar(500) NOT NULL,
        DATA varchar(30000) NOT NULL
    )
EOSQL