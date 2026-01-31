#!/bin/bash

APP_NAME="core-api"
DEPLOY_PATH="/home/ubuntu/boom-api"
JAR_NAME="app.jar"
ACTIVE_PROFILE="prod" # [변경] live -> prod

echo "🚀 배포 시작: $APP_NAME (환경: $ACTIVE_PROFILE)"

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

if [ ! -f "$JAR_NAME" ]; then
    echo "❌ 오류: $JAR_NAME 파일이 없습니다."
    exit 1
fi

# [수정] prod 프로필로 실행
nohup java -jar \
    -Dspring.profiles.active=$ACTIVE_PROFILE \
    -Dstorage.database.core-db.password="${DB_PASSWORD}" \
    $JAR_NAME > nohup.out 2>&1 &

# 3. 헬스 체크 (Health Check)
echo "🏥 서비스 헬스 체크 시작..."
sleep 10 # 앱 구동 대기

for i in {1..10}; do
    # /health 엔드포인트 호출 (HTTP 상태 코드 확인)
    RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/health)

    if [ "$RESPONSE_CODE" -eq 200 ]; then
        echo "✅ 배포 성공! (Health Check: 200 OK)"
        exit 0
    else
        echo "⏳ 대기 중... ($i/10) - 응답 코드: $RESPONSE_CODE"
        sleep 5
    fi
done

echo "❌ 배포 실패: 헬스 체크가 응답하지 않습니다."
echo "   > 로그 확인: cat nohup.out"
exit 1
