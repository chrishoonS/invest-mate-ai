package com.example.invest_mate_ai.user.controller;

import com.example.invest_mate_ai.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {
    private final UserMapper userMapper;

    @GetMapping("/db")
    public String testDb() {
        return userMapper.test();
    }
}