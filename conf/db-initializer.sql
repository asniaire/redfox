CREATE DATABASE IF NOT EXISTS redfox;

# Allowing every host for simplicity
CREATE USER 'redfox'@'%' IDENTIFIED BY 'redfox';
GRANT ALL PRIVILEGES ON *.* TO 'redfox'@'%';
FLUSH PRIVILEGES;