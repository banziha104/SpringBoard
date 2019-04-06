# Security 

> 스프링 부트 프레임워크는 인증과 권한에 관련된 기능인 스프링 부트 기큐리티를 제공

- OAuth2 : 토큰을 사용한 범용적인 방법의 인증을 제공하는 표준 인증 프로토콜
    - 권한 부여 코드 승인 타입 : 클라이언트가 다른 사용자 대신 특정 리소스에 접근을 요청할 때 사용
    - 암시적 승인 타입 ,: 코드 교환 단계 없이 액세스 토큰을 즉시 반환받아 이를 인증에 적용
    - 리소스 소유자 암호 자격 증명 승인 타입 : 클라이언트가 암호를 사용하여 액세스 토큰에 대한 사용자의 자격 증명을 교환
    - 클라이언트 자격 증명 승인 타입 : 클라이언트가 컨텍스트 외부에서 엑세스 토큰을 얻어 리소스에 접근을 요청할때
    
- 용어 
    - resource owner : 인증이 필요한 사용자
    - 클라이언트 : 웹사이트
    - 권한 서버 : 페이스북/구글/카카오 서버
    - 리소스 : 페이스북/구글/카카오 서버
   
---

# 시큐리티 + OAuth2

1. Enum 정의

```java

public enum SocialType {
    FACEBOOK("facebook"),
    GOOGLE("google"),
    KAKAO("kakao");

    private final String ROLE_PREFIX = "ROLE_";
    private String name;

    SocialType(String name) {
        this.name = name;
    }

    public String getRoleType() { return ROLE_PREFIX + name.toUpperCase(); }

    public String getValue() { return name; }

    public boolean isEquals(String authority) {
        return this.name.equals(authority);
    }
}
```

2. User에 소셜타입 추가


```java
@Getter
@NoArgsConstructor
@Entity
@Table
public class User implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column
    private String name;

    @Column
    private String password;

    @Column
    private String email;

    @Column
    private String pincipal;

    @Column
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime updatedDate;

    @Builder
    public User(String name, String password, String email, String pincipal, SocialType socialType, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.pincipal = pincipal;
        this.socialType = socialType;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
}
```

3. 시큐리티 + OAuth2 의존성 설정

```groovy
apply plugin: 'java'
apply plugin: 'eclipse'
apply plugin: 'org.springframework.boot'
apply plugin: 'io.spring.dependency-management'

group = 'com.board'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'


buildscript {
	ext {
		springBootVersion = '1.5.14.RELEASE'
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
	implementation 'org.springframework.boot:spring-boot-starter-web'
	compile('org.springframework.security.oauth:spring-security-oauth2')
	compile('org.springframework.boot:spring-boot-starter-thymeleaf')
	compile('org.thymeleaf.extras:thymeleaf-extras-java8time')
	compile("org.springframework.boot:spring-boot-starter-data-jpa")
	compile('com.h2database:h2')

	runtime('org.springframework.boot:spring-boot-devtools')
	compileOnly('org.projectlombok:lombok')
	testCompile('org.springframework.boot:spring-boot-starter-test')
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

```


4. SNS 프로퍼티 설정 및 바인딩
    - clientId : OAuth 클라이언트 사용자명
    - clientSecret : OAuth 클라이언트 시크릿 키값
    - accessTokenUri : 
    - userAuthorizationUri :
    - scope : 
    - userInfoUri : 
    
    ```yaml
    facebook :
      client :
        clientId : 
        clientSecret:
        accessTokenUri: https://graph.facebook.com/oauth/access_token
        userAuthorizationUri: https://www.facebook.com/dialog/oauth?display=popup
        tokenName: oauth_token
        authenticationScheme: query
        clientAuthenticationScheme: form
        scope: email
      resource:
        userInfoUri: https://graph.facebook.com/me?fields=id,name,email,link
    
    google :
      client :
        clientId :
        clientSecret:
        accessTokenUri: https://accounts.google.com/o/oauth2/token
        userAuthorizationUri: https://accounts.google.com/o/oauth2/auth
        scope: email, profile
      resource:
        userInfoUri: https://www.googleapis.com/oauth2/v2/userinfo
    
    kakao :
      client :
        clientId :
        accessTokenUri: https://kauth.kakao.com/oauth/token
        userAuthorizationUri: https://kauth.kakao.com/oauth/authorize
      resource:
        userInfoUri: https://kapi.kakao.com/v1/user/me
    ``` 
    
    - 시큐리티 OAuth2 