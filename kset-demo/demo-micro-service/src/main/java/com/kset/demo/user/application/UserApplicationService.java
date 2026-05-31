package com.kset.demo.user.application;

import com.kset.demo.user.domain.model.User;
import com.kset.demo.user.domain.repository.UserCacheRepository;
import com.kset.demo.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class UserApplicationService {

    private static final Duration USER_CACHE_TTL = Duration.ofMinutes(5);

    private final UserRepository userRepository;
    private final UserCacheRepository userCacheRepository;

    public UserApplicationService(UserRepository userRepository, UserCacheRepository userCacheRepository) {
        this.userRepository = userRepository;
        this.userCacheRepository = userCacheRepository;
    }

    public User getUser(Long id) {
        User cached = userCacheRepository.findById(id);
        if (cached != null) {
            return cached;
        }
        User user = userRepository.findById(id);
        if (user != null) {
            userCacheRepository.save(user, USER_CACHE_TTL);
        }
        return user;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public String getUserName(Long id) {
        User user = getUser(id);
        return user != null ? user.getName() : "unknown";
    }
}
