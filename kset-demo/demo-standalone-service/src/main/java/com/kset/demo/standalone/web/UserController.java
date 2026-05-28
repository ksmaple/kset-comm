package com.kset.demo.standalone.web;

import com.kset.web.annotation.OpLog;
import com.kset.demo.standalone.entity.UserEntity;
import com.kset.demo.standalone.mapper.UserMapper;
import com.kset.web.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.kset.redis.core.KsetRedisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@Tag(name = "用户", description = "单机示例 API")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserMapper userMapper;
    private final KsetRedisService redisService;

    public UserController(UserMapper userMapper, KsetRedisService redisService) {
        this.userMapper = userMapper;
        this.redisService = redisService;
    }

    @Operation(summary = "按 ID 查询用户（Redis 缓存）")
    @GetMapping("/{id}")
    public ApiResponse<UserEntity> get(@PathVariable Long id) {
        String cacheKey = "standalone:user:" + id;
        UserEntity cached = redisService.get(cacheKey, UserEntity.class);
        if (cached != null) {
            return ApiResponse.success(cached);
        }
        UserEntity user = userMapper.selectById(id);
        if (user != null) {
            redisService.setEx(cacheKey, user, Duration.ofMinutes(5));
        }
        return ApiResponse.success(user);
    }

    @Operation(summary = "创建用户")
    @PostMapping
    @OpLog(type = "CREATE", target = "user", recordParams = true)
    public ApiResponse<UserEntity> create(@RequestBody UserEntity user) {
        userMapper.insert(user);
        return ApiResponse.success(user);
    }
}
