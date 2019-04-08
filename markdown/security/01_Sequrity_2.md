# Security 2.x 버전

1. Gradle 설정

```groovy
apply plugin: 'java'
apply plugin: 'eclipse'
apply plugin: 'org.springframework.boot'
apply plugin: 'io.spring.dependency-management'

group = 'com.board'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'

ext['hibernate.version'] = '5.2.10.Final'


buildscript {
	ext {
		springBootVersion = '2.0.3.RELEASE'
	}
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
	}
}


repositories {
	mavenCentral()
}

dependencies {
	compile('org.springframework.security:spring-security-oauth2-client')
	compile('org.springframework.security:spring-security-oauth2-jose')
	compile('org.springframework.boot:spring-boot-starter-security')
	compile('org.springframework.boot:spring-boot-starter-web')
	compile('org.springframework.boot:spring-boot-starter-thymeleaf')
	compile('org.springframework.boot:spring-boot-starter-data-jpa')
	compile('com.h2database:h2')

	//runtime('mysql:mysql-connector-java')
	runtime('org.springframework.boot:spring-boot-devtools')
	compileOnly('org.projectlombok:lombok')
	testCompile('org.springframework.boot:spring-boot-starter-test')
}

```

2. application.yml 변경

```yaml
spring:
#  datasource:
#    url: jdbc:mysql://
#    username:
#    password:
#    driver-class-name: com.mysql.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: create
  h2:
    console:
      enabled: true
      path: /console
  devtools:
    livereload:
      enabled: true
  thymeleaf:
      cache: false
  security:
    oauth2:
      client:
        registration:
          google:
            client-id:
            client-secret:
          facebook:
            client-id:
            client-secret:
custom:
  oauth2:
    kakao:
      client-id:
```

3. Security Config변경 

```java

```