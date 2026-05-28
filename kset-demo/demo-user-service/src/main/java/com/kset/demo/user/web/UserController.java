package com.kset.demo.user.web;

import com.kset.web.annotation.OpLog;
import com.kset.demo.user.entity.UserEntity;
import com.kset.demo.user.mapper.UserMapper;
import com.kset.web.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户", description = "用户 CRUD 示例")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserMapper userMapper;

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Operation(summary = "按 ID 查询用户")
    @GetMapping("/{id}")
    public ApiResponse<UserEntity> get(@PathVariable Long id) {
        return ApiResponse.success(userMapper.selectById(id));
    }

    @Operation(summary = "创建用户")
    @PostMapping
    @OpLog(type = "CREATE", target = "user", recordParams = true)
    public ApiResponse<UserEntity> create(@RequestBody UserEntity user) {
        userMapper.insert(user);
        return ApiResponse.success(user);
    }
}
