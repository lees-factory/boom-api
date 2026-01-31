#!/bin/bash

# =======================================
# 변수 설정
# =======================================
APP_NAME="core-api"
# CI/CD에서 이 경로로 JAR와 스크립트를 복사할 것입니다.
DEPLOY_PATH="/home/ubuntu/boom-api"
JAR_NAME="core-api-0.0.1-SNAPSHOT.jar"
JAR_PATH="$DEPLOY_PATH/$JAR_NAME"
ACTIVE_PROFILE="prod"

echo "======================================="
echo "   🚀 배포 스크립트 실행: $APP_NAME"
echo "======================================="

# 1. 실행 중인 프로세스 종료
echo "> 1. 현재 구동 중인 애플리케이션 확인 및 종료"
CURRENT_PID=$(pgrep -f "java -jar.*$APP_NAME")

if [ -z "$CURRENT_PID" ]; then
    echo "   > 구동 중인 애플리케이션이 없습니다."
else
    echo "   > 실행 중인 프로세스 종료 (PID: $CURRENT_PID)"
    kill -15 $CURRENT_PID
    sleep 5
fi

# 2. 새 애플리케이션 실행
echo "> 2. 새 애플리케이션 실행"

# 실행 권한 부여 (혹시 모르니)
chmod +x $JAR_PATH

nohup java -jar \
    -Dspring.profiles.active=$ACTIVE_PROFILE \
    -Dstorage.database.core-db.password="${DB_PASSWORD}" \
    $JAR_PATH > $DEPLOY_PATH/nohup.out 2>&1 &

echo "======================================="
echo "   ✅ 배포 완료! (로그: $DEPLOY_PATH/nohup.out)"
echo "======================================="
