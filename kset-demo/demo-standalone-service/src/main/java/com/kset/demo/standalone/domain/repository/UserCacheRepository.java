package com.kset.demo.standalone.domain.repository;

import com.kset.demo.standalone.domain.model.User;

import java.time.Duration;

public interface UserCacheRepository {

    User findById(Long id);

    void save(User user, Duration ttl);
}
