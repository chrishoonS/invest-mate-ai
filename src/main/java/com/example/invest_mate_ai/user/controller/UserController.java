package com.example.invest_mate_ai.user.controller;

import com.example.invest_mate_ai.user.dto.request.UserUpdateRequest;
import com.example.invest_mate_ai.user.dto.response.UserResponse;
import com.example.invest_mate_ai.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(@RequestParam Long userId) {
        return ResponseEntity.ok(userService.getUsers(userId));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMyInfo(@RequestParam Long userId,
                                                     @RequestBody UserUpdateRequest request
    ) {
        request.setId(userId);
        return ResponseEntity.ok(userService.updateUsers(request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(@RequestParam Long userId) {

        userService.deleteUsers(userId);

        return ResponseEntity.noContent().build();
    }
}