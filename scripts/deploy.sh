#!/bin/bash
set -e

# ============================================================
# Boom API Deploy Script
# Target: AWS Lightsail $7/month (1GB RAM, 1 vCPU)
# ============================================================

readonly APP_NAME="boom-api"
readonly DEPLOY_PATH="/home/ubuntu/boom-api"
readonly JAR_NAME="app.jar"
readonly PROFILE="prod"
readonly JAVA_PATH="/usr/bin/java"
readonly HEALTH_ENDPOINT="http://localhost:8080/health"

# JVM 메모리 설정 (Lightsail $7: 1GB RAM)
# - Heap: 256MB ~ 384MB (여유 메모리 확보)
# - Metaspace: 기본값 사용
readonly JVM_OPTS="-Xms256m -Xmx384m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

log() { echo "[$(date '+%H:%M:%S')] $1"; }

# ------------------------------------------------------------
# 1. 기존 프로세스 종료
# ------------------------------------------------------------
stop_app() {
    local pid
    pid=$(pgrep -f "java.*${JAR_NAME}" || true)

    if [[ -n "$pid" ]]; then
        log "🛑 기존 프로세스 종료 (PID: $pid)"
        kill -15 "$pid"
        sleep 3

        # 강제 종료 (graceful shutdown 실패 시)
        if kill -0 "$pid" 2>/dev/null; then
            log "⚠️  강제 종료"
            kill -9 "$pid"
        fi
    fi
}

# ------------------------------------------------------------
# 2. 애플리케이션 시작
# ------------------------------------------------------------
start_app() {
    cd "$DEPLOY_PATH"

    if [[ ! -f "$JAR_NAME" ]]; then
        log "❌ $JAR_NAME 파일 없음"
        exit 1
    fi

    log "🚀 애플리케이션 시작 (profile: $PROFILE)"

    nohup $JAVA_PATH \
        $JVM_OPTS \
        -Dspring.profiles.active="$PROFILE" \
        -Dstorage.database.core-db.password="${DB_PASSWORD}" \
        -jar "$JAR_NAME" \
        > nohup.out 2>&1 &

    log "📝 PID: $!"
}

# ------------------------------------------------------------
# 3. 헬스체크
# ------------------------------------------------------------
health_check() {
    log "🏥 헬스체크 시작..."
    sleep 10

    for i in {1..12}; do
        local status
        status=$(curl -s -o /dev/null -w "%{http_code}" "$HEALTH_ENDPOINT" || echo "000")

        if [[ "$status" == "200" ]]; then
            log "✅ 배포 성공!"
            return 0
        fi

        log "⏳ 대기 중... ($i/12) - HTTP $status"
        sleep 5
    done

    log "❌ 헬스체크 실패"
    log "📋 로그: tail -100 nohup.out"
    exit 1
}

# ------------------------------------------------------------
# Main
# ------------------------------------------------------------
main() {
    log "=========================================="
    log "🎯 $APP_NAME 배포 시작"
    log "=========================================="

    stop_app
    start_app
    health_check

    log "=========================================="
    log "🎉 배포 완료"
    log "=========================================="
}

main