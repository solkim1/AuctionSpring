package com.oggo.auction.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.oggo.auction.model.Users;
import com.oggo.auction.repository.UsersRepository;

import jakarta.transaction.Transactional;

@Service
public class UsersService {

    @Autowired
    private UsersRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 사용자 저장 (회원가입)
    public void join(Users member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        repository.save(member);
    }

    // 로그인 기능
    public Users login(Users member) {
        Users result = repository.findByUserId(member.getUserId());
        if (result != null && passwordEncoder.matches(member.getPassword(), result.getPassword())) {
            return result;
        } else {
            return null;
        }
    }

    // 모든 사용자 가져오기
    public List<Users> getAllUsers() {
        return repository.findAll();
    }

    // 사용자 정보 가져오기
    public Users findUserById(String userId) {
        return repository.findByUserId(userId);
    }

    // 사용자 삭제
    @Transactional
    public void deleteUser(String userId) {
        Optional<Users> userOptional = repository.findById(userId);
        if (userOptional.isPresent()) {
            repository.delete(userOptional.get());
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }

    @Transactional
    public void updateProfile(String oldUserId, String newUserId, String newPassword, String newNickname) {
        Users user = repository.findByUserId(oldUserId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // If the userId is changed, create a new user entry
        if (newUserId != null && !newUserId.isEmpty() && !oldUserId.equals(newUserId)) {
            // Create a new user entry
            Users newUser = new Users();
            newUser.setUserId(newUserId);
            newUser.setPassword(user.getPassword());
            newUser.setNickname(user.getNickname());
            newUser.setLikes(user.getLikes());

            repository.save(newUser);

            // Delete the old user entry
            repository.delete(user);
            return;  // Exit the method to avoid further updates to the old user entry
        }

        // Update password and nickname if provided
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        if (newNickname != null && !newNickname.isEmpty()) {
            user.setNickname(newNickname);
        }

        repository.save(user);
    }

    @Transactional
    public Users incrementLikes(String userId) {
        Users user = repository.findByUserId(userId);
        if (user != null) {
            user.setLikes(user.getLikes() + 1);
            repository.save(user);
            return user;
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }
}
