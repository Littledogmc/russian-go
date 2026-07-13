@echo off
cd /d "%~dp0"
echo {"@class":"com.russtudy.model.User","id":1,"username":"enfarction8081","passwordHash":"$2a$10$O5Yyyb3ViB.B.4w3zX1ifeX.BeMD4SaProfaYYfsAih7qFPtbHFPW","email":"","role":0,"createdAt":"2026-07-12T15:29:25"} > user1.tmp
redis-cli.exe -x SET user:1 < user1.tmp
del user1.tmp
echo Done. User 1 is now admin.
pause