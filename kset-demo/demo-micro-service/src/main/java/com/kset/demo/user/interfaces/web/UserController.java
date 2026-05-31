package com.kset.demo.user.interfaces.web;

import com.kset.demo.user.application.UserApplicationService;
import com.kset.demo.user.domain.model.User;
import com.kset.web.annotation.OpLog;
import com.kset.web.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
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

    private final UserApplicationService userApplicationService;

    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @Operation(summary = "按 ID 查询用户（Redis 缓存）")
    @GetMapping("/{id}")
    public ApiResponse<User> get(@PathVariable Long id) {
        return ApiResponse.success(userApplicationService.getUser(id));
    }

    @Operation(summary = "创建用户")
    @PostMapping
    @OpLog(type = "CREATE", target = "user", recordParams = true)
    public ApiResponse<User> create(@RequestBody CreateUserRequest request) {
        User user = new User();
        user.setName(request.name());
        return ApiResponse.success(userApplicationService.createUser(user));
    }

    public record CreateUserRequest(@Schema(description = "用户名") String name) {
    }
}
