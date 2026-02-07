dependencies {
    compileOnly(project(":core:core-domain"))
    implementation(platform("software.amazon.awssdk:bom:2.31.23"))
    implementation("software.amazon.awssdk:s3")
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}
