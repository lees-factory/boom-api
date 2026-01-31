#!/bin/bash

APP_NAME="core-api"
DEPLOY_PATH="/home/ubuntu/boom-api"
JAR_NAME="app.jar"
ACTIVE_PROFILE="prod"

# [추가] Java 절대 경로 설정 (which java 결과로 바꾸세요)
# 보통 /usr/bin/java 이거나 설치 경로입니다.
JAVA_PATH="/usr/bin/java"

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

# [수정] java -> $JAVA_PATH 로 변경 (절대 경로 실행)
# [수정] 1GB 서버용 메모리 설정 (512MB)
nohup $JAVA_PATH -jar \
    -Dspring.profiles.active=$ACTIVE_PROFILE \
    -Dstorage.database.core-db.password="${DB_PASSWORD}" \
    -Xms512m -Xmx512m \
    $JAR_NAME > nohup.out 2>&1 &

# 3. 헬스 체크
echo "🏥 서비스 헬스 체크 시작..."
sleep 15 # 자바 부팅 대기 (여유있게 15초)

for i in {1..10}; do
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
