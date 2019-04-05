# 스프링 웹 구축

1. 열거형 생성 : BoardType, SocialType

```java
package com.board.springboard.domain.enums;

public enum BoardType {
    notice("공지사항"),
    free("자유게시판");

    private String value;

    BoardType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
```

```java
package com.board.springboard.domain.enums;

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

2. 도메인 매핑 

```java
@Getter // 게터생성
@NoArgsConstructor
@Entity // 엔티티
@Table // 테이블
public class Board implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 인티저값으로 기본키가 자동으로 할당
    private Long idx;

    @Column
    private String title;

    @Column
    private String subTitle;

    @Column
    private String content;

    @Column
    @Enumerated(EnumType.STRING)
    // Enum 타입 매핑용 어노테이션 , @Enumerated 어노테이션을 이용해 enum과 데이터베이스 데이터 변환을 지원
    private BoardType boardType;

    @Column
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime updatedDate;

    @OneToOne(fetch = FetchType.LAZY)
    // Board와 필드 값으로 갖고 있는 User 도메인을 1:1관계로 설정하는 어노테이션, DB저장시 User객체가 아닌 , PK값이 저장
    private User user;

    @Builder
    public Board(String title, String subTitle, String content, BoardType boardType, LocalDateTime createdDate, LocalDateTime updatedDate, User user) {
        this.title = title;
        this.subTitle = subTitle;
        this.content = content;
        this.boardType = boardType;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.user = user;
    }
}
```

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


3. Repository 생성

```java
public interface BoardRepository extends JpaRepository<Board,Long> {
    Board findByUser(User user);
}
```

```java
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
```


4. Test 생성

```java
@RunWith(SpringRunner.class) // JUnit 러너 대신 SpringRunner사용
@DataJpaTest // JAP 테스트를 위한 전용 어노테이션
public class JpaMappingTest {
    private final String boardTestTitle = "테스트";
    private final String email = "test@gmail.com";

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;


    @Before  // 각 테스트가 실행되기전에 실행될 메서드선언
    public void init() {
        User user = userRepository.save(User.builder()
                .name("havi")
                .password("test")
                .email(email)
                .createdDate(LocalDateTime.now())
                .build());

        boardRepository.save(Board.builder()
                .title(boardTestTitle)
                .subTitle("서브 타이틀")
                .content("컨텐츠")
                .boardType(BoardType.free)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .user(user).build());
    }

    @Test // 테스트가 진핼될메서드
    public void 제대로_생성_됐는지_테스트() {
        User user = userRepository.findByEmail(email);
        assertThat(user.getName(), is("havi"));
        assertThat(user.getPassword(), is("test"));
        assertThat(user.getEmail(), is(email));

        Board board = boardRepository.findByUser(user);
        assertThat(board.getTitle(), is(boardTestTitle));
        assertThat(board.getSubTitle(), is("서브 타이틀"));
        assertThat(board.getContent(), is("컨텐츠"));
        assertThat(board.getBoardType(), is(BoardType.free));
    }

}
```

5. Service 생성

```java
@Service// 서비스로 사용될 컴포넌트 정의
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Page<Board> findBoardList(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1, pageable.getPageSize());
        return boardRepository.findAll(pageable);
    }

    public Board findBoardByIdx(Long idx) {
        return boardRepository.findById(idx).orElse(new Board());
    }
}

```

6. Controller 생성

```java
@Controller
@RequestMapping("/board") // api url 경로를 /board로 정의
public class BoardController {

    @Autowired // boardService에 의존성주입
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping({"", "/"}) // 매핑 경로를 중괄호로 여러개 넣을 수 있음
    public String board(@RequestParam(value = "idx", defaultValue = "0") Long idx, Model model) { // @RequestParam 어노테이션을 사용하여 idx 파라미터를 필수로 받음ㅇ
        model.addAttribute("board", boardService.findBoardByIdx(idx));
        return "/board/form";
    }

    @GetMapping("/list")
    public String list(@PageableDefault Pageable pageable, Model model) { // PageableDefault 어노테이션의 파라미터인 size,sort,direction 등을 사용하여 페이징 처리에 대한 규약을 정의할 수 있습니다.
        model.addAttribute("boardList", boardService.findBoardList(pageable));
        return "/board/list";
    }

}
```

7. CommandLineRunner를 사용하여 DB에 데이터 넣기

 ```java

@SpringBootApplication
public class SpringBoardApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBoardApplication.class, args);
    }

    // CommandLineRunner는 애플리케이션 구동후 특정 코드를 실행시키고 싶을때 직접 구현하는 인터페이스
    @Bean // 스프링은 빈으로 생성된 메서드에 파라미터로 DI 시키는 매커니즘이 존재
    public CommandLineRunner runner(UserRepository userRepository, BoardRepository boardRepository) {
        return (args) -> {
            User user = userRepository.save(User.builder() // 빌더패턴 사용
                    .name("havi")
                    .password("test")
                    .email("havi@gmail.com")
                    .createdDate(LocalDateTime.now())
                    .build());

            IntStream.rangeClosed(1, 200).forEach(index ->
                    boardRepository.save(Board.builder()
                            .title("게시글"+index)
                            .subTitle("순서"+index)
                            .content("컨텐츠")
                            .boardType(BoardType.free)
                            .createdDate(LocalDateTime.now())
                            .updatedDate(LocalDateTime.now())
                            .user(user).build())
            );
        };
    }
    }
    
```

8. 뷰 페이지 작성