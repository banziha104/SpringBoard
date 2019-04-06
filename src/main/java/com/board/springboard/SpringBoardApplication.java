package com.board.springboard;

import com.board.springboard.domain.Board;
import com.board.springboard.domain.User;
import com.board.springboard.domain.enums.BoardType;
import com.board.springboard.repository.BoardRepository;
import com.board.springboard.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

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
