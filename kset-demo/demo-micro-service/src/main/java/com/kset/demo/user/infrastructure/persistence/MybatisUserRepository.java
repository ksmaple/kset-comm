package com.kset.demo.user.infrastructure.persistence;

import com.kset.demo.user.domain.model.User;
import com.kset.demo.user.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MybatisUserRepository implements UserRepository {

    private final UserMapper userMapper;

    public MybatisUserRepository(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User findById(Long id) {
        return toDomain(userMapper.selectById(id));
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        userMapper.insert(entity);
        return toDomain(entity);
    }

    private User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        User user = new User();
        user.setId(entity.getId());
        user.setName(entity.getName());
        user.setCreateTime(entity.getCreateTime());
        user.setUpdateTime(entity.getUpdateTime());
        user.setDeleted(entity.getDeleted());
        return user;
    }

    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setName(user.getName());
        entity.setCreateTime(user.getCreateTime());
        entity.setUpdateTime(user.getUpdateTime());
        entity.setDeleted(user.getDeleted());
        return entity;
    }
}
