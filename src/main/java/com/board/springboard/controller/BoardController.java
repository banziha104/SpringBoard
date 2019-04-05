package com.board.springboard.controller;


import com.board.springboard.domain.User;
import com.board.springboard.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("board") // api url 경로를 /board로 정의
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