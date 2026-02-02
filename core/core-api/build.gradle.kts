tasks.named<Jar>("bootJar").configure {
    enabled = true
}

tasks.named<Jar>("jar").configure {
    enabled = false
}

dependencies {
    implementation(project(":core:core-enum"))
    implementation(project(":core:core-domain"))
    implementation(project(":support:monitoring"))
    implementation(project(":support:logging"))
    implementation(project(":clients:client-oauth2"))
    runtimeOnly(project(":storage:db-core"))

    testImplementation(project(":tests:api-docs"))

    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
}
