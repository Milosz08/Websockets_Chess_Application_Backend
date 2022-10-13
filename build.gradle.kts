plugins {
    id("org.springframework.boot") version "2.7.3"
    id("io.spring.dependency-management") version "1.0.13.RELEASE"
    id("java")
}

group = "pl.miloszgilga"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

configurations {
    create("testCompile").apply {
        extendsFrom(configurations.compileOnly.get())
    }
}

repositories {
    mavenCentral()
}

extra.apply {
    set("springVersion", "2.7.3")
    set("lombokVersion", "1.18.20")
    set("jjwtVersion", "0.11.5")
    set("jTuplesVersion", "1.2")
    set("freemarkerVersion", "2.3.31")
    set("mySqlVersion", "8.0.30")
    set("liquibaseVersion", "4.16.1")
    set("dotEnvVersion", "2.5.4")
    set("xmlBinderVersion", "2.13.4")
    set("h2DatabaseVersion", "1.4.200")
    set("orikaMapperVersion", "1.6.0")
    set("reflectionsApiVersion", "0.10.2")
}

dependencies {
    implementation("org.javatuples:javatuples:${rootProject.extra.get("jTuplesVersion") as String}")
    implementation("org.reflections:reflections:${rootProject.extra.get("reflectionsApiVersion") as String}")
    implementation("org.projectlombok:lombok:${rootProject.extra.get("lombokVersion") as String}")
    implementation("org.freemarker:freemarker:${rootProject.extra.get("freemarkerVersion") as String}")
    implementation("org.liquibase:liquibase-core:${rootProject.extra.get("liquibaseVersion") as String}")
    implementation("io.jsonwebtoken:jjwt-api:${rootProject.extra.get("jjwtVersion") as String}")
    implementation("io.jsonwebtoken:jjwt-impl:${rootProject.extra.get("jjwtVersion") as String}")
    implementation("io.jsonwebtoken:jjwt-jackson:${rootProject.extra.get("jjwtVersion") as String}")
    implementation("mysql:mysql-connector-java:${rootProject.extra.get("mySqlVersion") as String}")
    implementation("me.paulschwarz:spring-dotenv:${rootProject.extra.get("dotEnvVersion") as String}")
    implementation("net.rakugakibox.spring.boot:orika-spring-boot-starter:${rootProject.extra.get("orikaMapperVersion") as String}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${rootProject.extra.get("xmlBinderVersion") as String}")

    implementation("org.springframework.boot:spring-boot-starter-web:${rootProject.extra.get("springVersion") as String}")
    implementation("org.springframework.boot:spring-boot-starter-mail:${rootProject.extra.get("springVersion") as String}")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:${rootProject.extra.get("springVersion") as String}")
    implementation("org.springframework.boot:spring-boot-starter-security:${rootProject.extra.get("springVersion") as String}")
    implementation("org.springframework.boot:spring-boot-starter-websocket:${rootProject.extra.get("springVersion") as String}")
    implementation("org.springframework.boot:spring-boot-starter-validation:${rootProject.extra.get("springVersion") as String}")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client:${rootProject.extra.get("springVersion") as String}")
    implementation("org.springframework.boot:spring-boot-starter-hateoas:${rootProject.extra.get("springVersion") as String}")

    runtimeOnly("com.h2database:h2:${rootProject.extra.get("h2DatabaseVersion") as String}")
    developmentOnly("org.springframework.boot:spring-boot-devtools:${rootProject.extra.get("springVersion") as String}")
    annotationProcessor("org.projectlombok:lombok:${rootProject.extra.get("lombokVersion") as String}")

    testImplementation("org.springframework.boot:spring-boot-starter-test:${rootProject.extra.get("springVersion") as String}")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.test {
    useJUnitPlatform()
}
