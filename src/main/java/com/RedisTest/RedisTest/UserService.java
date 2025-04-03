package com.RedisTest.RedisTest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public User getUser(Long id) {
        String key = "user:" + id;

        // 캐시에서 먼저 조회
        User cachedUser = (User) redisTemplate.opsForValue().get(key);
        if (cachedUser != null) {
            System.out.println("💡 Redis에서 캐시 조회 성공");
            return cachedUser;
        }

        // DB에서 조회 + Redis에 저장
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        redisTemplate.opsForValue().set(key, user, 60, TimeUnit.SECONDS); // TTL 60초
        System.out.println("🔄 DB에서 조회하고 Redis에 캐시 저장");
        return user;
    }


    public User getUserNoneRedis(Long id) {
        String key = "user:" + id;

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("🔄 DB에서 조회");
        return user;
    }



}
