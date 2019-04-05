package com.board.springboard.domain;

import com.board.springboard.domain.enums.BoardType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

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