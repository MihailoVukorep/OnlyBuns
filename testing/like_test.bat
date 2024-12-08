@echo off
setlocal enabledelayedexpansion

set POST_ID=3
set NUM_REQUESTS=2
set URL=http://localhost:8080/api/posts/%POST_ID%/like

curl -X POST http://localhost:8080/api/login -H "Content-Type: application/json" -d "{\"username\":\"rope\", \"password\":\"123\", \"email\":\"killmeplzftn+pera@gmail.com\"}" -c cookies.txt
curl -X POST http://localhost:8080/api/posts/3/like -b cookies.txt

curl -X POST http://localhost:8080/api/login -H "Content-Type: application/json" -d "{\"username\":\"snake\", \"password\":\"123\", \"email\":\"bigboss@gmail.com\"}" -c cookies.txt
curl -X POST http://localhost:8080/api/posts/3/like -b cookies.txt

echo Zahtevi su poslati. Proveri rezultate na serveru!
pause
