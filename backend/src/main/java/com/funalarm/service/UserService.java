package com.funalarm.service;

import com.funalarm.config.AppConstants;
import com.funalarm.dto.RegisterRequest;
import com.funalarm.entity.User;
import com.funalarm.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username().trim())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        User user = new User();
        user.setUsername(request.username().trim());
        user.setNickname(request.nickname().trim());
        return userRepository.save(user);
    }

    public User login(String username) {
        return userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在，请先注册"));
    }

    @Transactional
    public User updateNickname(Integer userId, String nickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setNickname(nickname.trim());
        return userRepository.save(user);
    }
}
