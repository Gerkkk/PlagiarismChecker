plugins {
    java
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.9.4"
}

group = "PlagiatChecker"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

val grpcVersion = "1.54.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("net.devh:grpc-server-spring-boot-starter:2.15.0.RELEASE")
    implementation("com.google.protobuf:protobuf-java:3.25.2")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("net.devh:grpc-client-spring-boot-starter:2.15.0.RELEASE")
//    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    implementation("org.hibernate.validator:hibernate-validator:6.2.0.Final")
    //implementation("jakarta.validation:jakarta.validation-api:2.0.2")

    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

    compileOnly("org.apache.tomcat:annotations-api:6.0.53")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.3")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.8")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.3")
    implementation("io.swagger.core.v3:swagger-annotations-jakarta:2.2.8")

    //implementation("org.springframework.boot:spring-boot-starter-multipart")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.2"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.63.0"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
            }
        }
    }
}

configurations.all {
    resolutionStrategy {
        eachDependency {
            if (requested.group == "io.swagger.core.v3" && requested.name == "swagger-annotations") {
                useVersion("2.2.8")
                because("Avoid conflicts with older versions of swagger-annotations")
            }
        }
    }
}


tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

sourceSets["main"].java.srcDirs(
    "build/generated/source/proto/main/java",
    "build/generated/source/proto/main/grpc"
)

tasks.withType<Test> {
    useJUnitPlatform()
}
