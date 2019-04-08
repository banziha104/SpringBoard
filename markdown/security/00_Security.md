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
    
5. 시큐리티 OAuth2 설정 객체 생성 
    
```java
@Configuration
@EnableOAuth2Client // OAuth2 기능을 사용하겠다는 어노테이션
@EnableWebSecurity // 웹에서 시큐리티 기능을 사용하겠다는 어노테이션
public class SecurityConfig extends WebSecurityConfigurerAdapter { // 요청, 권한 , 기타 설정에 대해 필수적인 기능 최적화하여  설정
    @Autowired
    private OAuth2ClientContext oAuth2ClientContext;

    @Override
    protected void configure(HttpSecurity http) throws Exception { // 메서드의 프로퍼티에 대한 설명
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        http
                .authorizeRequests()
                .antMatchers("/", "/login/**",  "/css/**", "/images/**", "/js/**", "/console/**").permitAll()  // HttpServletRequest 기반으로 설정
                .antMatchers("/facebook").hasAuthority(FACEBOOK.getRoleType())// 요청 패턴을 리스트 형식으로 설정
                .antMatchers("/google").hasAuthority(GOOGLE.getRoleType())
                .antMatchers("/kakao").hasAuthority(KAKAO.getRoleType())
                .anyRequest() // 설정한 요청 이외의 리퀘스트를 요청
                .authenticated() // 인증된 사용자만
                .and()
                .headers().frameOptions().disable() // 해더설정
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                .and()
                .formLogin()
                .successForwardUrl("/board/list")
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .and()
                .addFilterBefore(filter, CsrfFilter.class)
                .addFilterBefore(oauth2Filter(), BasicAuthenticationFilter.class)
                .csrf().disable();
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    private Filter oauth2Filter() { 
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(oauth2Filter(facebook(), "/login/facebook", FACEBOOK));
        filters.add(oauth2Filter(google(), "/login/google", GOOGLE));
        filters.add(oauth2Filter(kakao(), "/login/kakao", KAKAO));
        filter.setFilters(filters);
        return filter;
    }

    private Filter oauth2Filter(ClientResources client, String path, SocialType socialType) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path); /// OAuth2 클라이언트 인증 처리 필터를 생성
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oAuth2ClientContext); // 권한 서버와의 통신을 위해 OAuth2RestTemplate 생성

        filter.setRestTemplate(template); // User의 권한을 최적화해서 생성하고자 UserInfoTokenServices를 상속받음 UserToken 
        filter.setTokenServices(new UserTokenService(client, socialType));
        filter.setAuthenticationSuccessHandler((request, response, authentication) -> response.sendRedirect("/" + socialType.getValue() + "/complete")); // 인증성공시
        filter.setAuthenticationFailureHandler((request, response, exception) -> response.sendRedirect("/error")); //인증 실패시
        return filter;
    }


    @Bean
    @ConfigurationProperties("facebook")
    public ClientResources facebook(){
        return new ClientResources();
    }

    @Bean
    @ConfigurationProperties("google")
    public ClientResources google(){
        return new ClientResources();
    }

    @Bean
    @ConfigurationProperties("kakao")
    public ClientResources kakao(){
        return new ClientResources();
    }
}
```

6. 토큰 클래스 설정

```java
public class UserTokenService extends UserInfoTokenServices { // UserInfoTokenServices는 OAuth2 인증을위해 스프링에서 제공 

    public UserTokenService(ClientResources resources, SocialType socialType) {
        super(resources.getResource().getUserInfoUri(), resources.getClient().getClientId());
        setAuthoritiesExtractor(new OAuth2AuthoritiesExtractor(socialType));
    }

    public static class OAuth2AuthoritiesExtractor implements AuthoritiesExtractor {

        private String socialType;

        public OAuth2AuthoritiesExtractor(SocialType socialType) {
            this.socialType = socialType.getRoleType();
        }

        @Override
        public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
            return AuthorityUtils.createAuthorityList(this.socialType);
        }
    }

}

```

7. AOP 구현

```java
```