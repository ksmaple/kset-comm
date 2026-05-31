package com.kset.demo.user.domain.repository;

import com.kset.demo.user.domain.model.User;

public interface UserRepository {

    User findById(Long id);

    User save(User user);
}
