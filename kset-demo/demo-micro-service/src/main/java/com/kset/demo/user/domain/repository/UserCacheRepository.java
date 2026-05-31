package com.kset.demo.user.domain.repository;

import com.kset.demo.user.domain.model.User;

import java.time.Duration;

public interface UserCacheRepository {

    User findById(Long id);

    void save(User user, Duration ttl);
}
