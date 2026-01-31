#!/bin/bash

APP_NAME="core-api"
DEPLOY_PATH="/home/ubuntu/boom-api"
JAR_NAME="app.jar"
ACTIVE_PROFILE="live"

echo "🚀 배포 시작: $APP_NAME"

# 1. 기존 프로세스 종료
CURRENT_PID=$(pgrep -f "java -jar.*$APP_NAME")
if [ -n "$CURRENT_PID" ]; then
    echo "   > 실행 중인 프로세스 종료 (PID: $CURRENT_PID)"
    kill -15 $CURRENT_PID
    sleep 5
fi

# 2. 실행
echo "   > 새 애플리케이션 실행"
cd $DEPLOY_PATH

# app.jar가 있는지 확인
if [ ! -f "$JAR_NAME" ]; then
    echo "❌ 오류: $JAR_NAME 파일이 없습니다."
    exit 1
fi

nohup java -jar \
    -Dspring.profiles.active=$ACTIVE_PROFILE \
    -Dstorage.database.core-db.password="${DB_PASSWORD}" \
    $JAR_NAME > nohup.out 2>&1 &

echo "✅ 배포 완료!"
