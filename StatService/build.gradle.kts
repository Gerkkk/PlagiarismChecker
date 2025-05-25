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

repositories {
	mavenCentral()
}

dependencies {
//	implementation("org.springframework.boot:spring-boot-starter")
//	testImplementation("org.springframework.boot:spring-boot-starter-test")
//	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
//	implementation("org.springframework.boot:spring-boot-starter-web")
//	implementation("net.devh:grpc-server-spring-boot-starter:2.15.0.RELEASE")
//	implementation("com.google.protobuf:protobuf-java:3.25.2")
//	implementation("io.grpc:grpc-protobuf:1.63.0")
//	implementation("io.grpc:grpc-stub:1.63.0")
//	implementation("javax.annotation:javax.annotation-api:1.3.2")
//	implementation("org.springframework.boot:spring-boot-starter-actuator")
//	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//	implementation("org.postgresql:postgresql:42.7.3")
//	compileOnly("org.projectlombok:lombok")
//	annotationProcessor("org.projectlombok:lombok")
//	implementation("org.springframework.boot:spring-boot-starter-webflux")

	implementation(platform("io.grpc:grpc-bom:1.58.0"))

	implementation("org.springframework.boot:spring-boot-starter")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("net.devh:grpc-server-spring-boot-starter:2.15.0.RELEASE")
	implementation("com.google.protobuf:protobuf-java:3.25.2")

	implementation("io.grpc:grpc-protobuf")
	implementation("io.grpc:grpc-stub")

	implementation("javax.annotation:javax.annotation-api:1.3.2")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.postgresql:postgresql:42.7.3")
	implementation("org.apache.commons:commons-math3:3.6.1")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	implementation("org.springframework.boot:spring-boot-starter-webflux")

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

sourceSets {
	main {
		proto {
			srcDir("src/main/java/proto")
		}
		java {
			srcDirs(
				"build/generated/source/proto/main/java",
				"build/generated/source/proto/main/grpc"
			)
		}
	}
}


tasks.withType<Test> {
	useJUnitPlatform()
}
