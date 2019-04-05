# 스프링 부트 테스트

- @SpringBootTest : 통합 테스트를 제공, 여러 단위 테스트를 하나의 통합된 테스트로 수행할 때 적합

```java
package com.board.springboard;

// 설정된 빈을 모두 로드하기 떄문에 규모가 커지면 느려짐
@RunWith(SpringRunner.class) // 내장된 러너를 사용하는 대신 어노테이션에 정의된 러너 클래스를 사용함
@SpringBootTest // 실제 스프링과 똑같이 애플리케이션 컨텍스트를 로드하여 테스트를 함
public class SpringBoardApplicationTests {
    @Test
    public void contextLoads(){
        
    }
}

```

- @WebMvcTest : MVC를 위한 테스트 
- @DataJpaTest : JPA 관련 테스트 설정만 로드함
- @RestClientTest : REST 관련 테스트를 도와주는 어노테이션 
- @JsonTest : JSON 직렬화 역직렬화 테스트 


```java
package com.board.springboard;


import com.board.springboard.domain.Board;
import com.board.springboard.domain.User;
import com.board.springboard.domain.enums.BoardType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.hamcrest.core.Is.is;

/**
 * Created by KimYJ on 2017-07-12.
 */
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
