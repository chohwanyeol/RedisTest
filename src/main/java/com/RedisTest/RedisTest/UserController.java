package com.RedisTest.RedisTest;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(
            summary = "ID로 유저 조회",
            description = "Redis 캐시를 먼저 조회하고, 없으면 DB에서 유저 정보를 가져옵니다."
    )
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }



    @GetMapping("/benchmarkRedis")
    @Operation(
            summary = "Redis 성능 조회",
            description = "유저 1000번 조회 후, 성능 측정."
    )
    public ResponseEntity<String> benchmarkRedis()  {
        long start = System.currentTimeMillis();

        for (long i = 1; i <= 1000; i++) {
            userService.getUser(100L);  // Redis 캐시 사용됨
        }

        long end = System.currentTimeMillis();
        long duration = end - start;

        return ResponseEntity.ok("1000건 조회 완료, 걸린 시간: " + duration + "ms");
    }


    @GetMapping("/benchmarkNoneRedis")
    @Operation(
            summary = "Redis 없이 성능 조회",
            description = "유저 1000번 조회 후, 성능 측정."
    )
    public ResponseEntity<String> benchmarkNoneRedis()  {
        long start = System.currentTimeMillis();

        for (long i = 1; i <= 1000; i++) {
            userService.getUserNoneRedis(100L);  // Redis 캐시 사용됨
        }

        long end = System.currentTimeMillis();
        long duration = end - start;

        return ResponseEntity.ok("1000건 조회 완료, 걸린 시간: " + duration + "ms");
    }
}
