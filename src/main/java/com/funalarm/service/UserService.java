package com.funalarm.service;

import com.funalarm.dao.UserDAO;
import com.funalarm.model.User;

import java.sql.SQLException;
import java.util.Optional;

public class UserService {
    private final UserDAO userDAO = new UserDAO();

    public User register(String username, String nickname) throws SQLException {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("昵称不能为空");
        }
        if (userDAO.findByUsername(username.trim()).isPresent()) {
            throw new IllegalArgumentException("用户名已存在");
        }
        return userDAO.insert(username.trim(), nickname.trim());
    }

    public User login(String username) throws SQLException {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        Optional<User> user = userDAO.findByUsername(username.trim());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("用户不存在，请先注册");
        }
        return user.get();
    }

    public void updateNickname(int userId, String nickname) throws SQLException {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("昵称不能为空");
        }
        userDAO.updateNickname(userId, nickname.trim());
    }

    public Optional<User> findById(int userId) throws SQLException {
        return userDAO.findById(userId);
    }
}
