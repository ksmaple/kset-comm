package com.kset.demo.user.interfaces.rpc;

import com.kset.demo.api.UserQueryService;
import com.kset.demo.user.application.UserApplicationService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class UserQueryServiceImpl implements UserQueryService {

    private final UserApplicationService userApplicationService;

    public UserQueryServiceImpl(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @Override
    public String getUserName(Long userId) {
        return userApplicationService.getUserName(userId);
    }
}
