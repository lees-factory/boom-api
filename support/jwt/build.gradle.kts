dependencies {

    implementation(project(":core:core-domain"))

    api("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

    implementation("org.springframework.boot:spring-boot")

    // 설정 메타데이터 생성을 위한 프로세서 (기존 유지)
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}
