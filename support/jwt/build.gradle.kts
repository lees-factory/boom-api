dependencies {

    implementation(project(":core:core-domain"))

    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

    // ConfigurationProperties 사용을 위해
    implementation("org.springframework.boot:spring-boot-web")
}
