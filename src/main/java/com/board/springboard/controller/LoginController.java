package com.board.springboard.controller;

import com.board.springboard.annotation.SocialUser;
import com.board.springboard.domain.User;
import org.springframework.web.bind.annotation.GetMapping;

public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping(value = "/{facebook|google|kakao}/complete")
    public String loginComplete(@SocialUser User user) {
        return "redirect:/board/list";
    }
}
