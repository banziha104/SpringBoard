package com.board.springboard.repository;

import com.board.springboard.domain.Board;
import com.board.springboard.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board,Long> {
    Board findByUser(User user);
}



