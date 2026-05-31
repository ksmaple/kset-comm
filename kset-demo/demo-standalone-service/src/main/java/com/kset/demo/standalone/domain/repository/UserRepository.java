package com.kset.demo.standalone.domain.repository;

import com.kset.demo.standalone.domain.model.User;

public interface UserRepository {

    User findById(Long id);

    User save(User user);
}
