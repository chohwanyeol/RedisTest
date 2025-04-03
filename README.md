
# 🔁 Redis 캐시 성능 비교 실험 프로젝트

Spring Boot 기반으로 Redis 캐시 적용 여부에 따라 REST API의 응답 속도 차이를 직접 체감해보기 위한 실험 프로젝트입니다.  
처음에는 캐시가 당연히 성능을 향상시킬 것이라 기대했지만, 실험 결과는 예상과는 전혀 다른 결과를 보여주었습니다.

---

## ✅ 프로젝트 정보

- **프로젝트명**: Redis Test
- **기술스택**: Java 21, Spring Boot 3.4.4, Spring Web, Lombok, Spring Data JPA (H2), Spring Data Redis, Swagger
- **실험 목적**:
  - Redis 캐시가 실제로 얼마나 성능 향상을 가져오는지 체감해보기
  - 동일 요청 반복 vs 서로 다른 요청 상황에서 캐시 효과를 수치로 확인
  - 캐시가 항상 빠를 것이라는 기대와 실제 결과의 차이 분석

---

## 📦 주요 의존성

```groovy
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
implementation 'org.springframework.boot:spring-boot-devtools'
implementation 'org.projectlombok:lombok'
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'
runtimeOnly 'com.h2database:h2'
```

---

## 🔧 실행 방법

```bash
# Redis 실행
docker run -d -p 6379:6379 --name redis redis

# 프로젝트 실행
./gradlew bootRun
```

> 실행 후 Swagger 접속:  
> [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🧪 실험 API

| Method | Endpoint                   | 설명 |
|--------|-----------------------------|------|
| GET    | `/user/{id}`                | Redis 캐시를 먼저 조회하고, 없으면 DB 조회 후 캐시 등록 |
| GET    | `/user/benchmarkRedis`      | 동일 ID를 1000회 Redis 기반으로 조회, 총 소요 시간 측정 |
| GET    | `/user/benchmarkNoneRedis`  | 동일 ID를 1000회 DB에서 직접 조회, 총 소요 시간 측정 |

---

## 📊 실험 결과 비교

### 동일 ID 1000번 조회

| 방식         | 응답 시간 |
|--------------|-----------|
| Redis 사용   | **적중률이 낮아 오래걸림** |
| DB만 사용    | **빠름**  |

### 각각 다른 ID 1000번 조회

| 방식         | 응답 시간 |
|--------------|-----------|
| Redis 사용   | **적중률이 높았지만 빠르지 않았음** |
| DB만 사용    | **빠름** |

---

## 🧠 느낀 점: "캐시는 무조건 빠르지 않다."

- 실험 전에는 Redis 캐시가 당연히 DB보다 빠를 것이라 기대했지만,  
  **Redis가 오히려 더 느린 결과가 나왔다.**

- 분석 결과, 그 이유는 다음과 같았다:
  - Redis 사용 시마다 발생하는 **JSON 직렬화/역직렬화 비용**
  - **네트워크 I/O**를 포함한 Redis 접근 비용
  - 반면 **H2 인메모리 DB는 JVM 내부에서 바로 응답**하므로 훨씬 빠름
  - 또한 반복 호출이 누적되면서 JVM/JPA 수준에서도 **런타임 최적화(JIT, 쿼리 캐시 등)** 가 일어났을 가능성도 있음

- 이 실험을 통해 단순히 “캐시는 빠르다”는 생각보다,  
  “어떤 조건에서 캐시를 쓰는 것이 효과적인가” 를 고민해야 한다는 걸 체감했다.

---

## ✍️ 구조 파일 예시

```
redis-performance-test/
├── RedisPerformanceTestApplication.java
├── controller/
│   └── UserController.java
├── service/
│   └── UserService.java
├── entity/
│   └── User.java
├── repository/
│   └── UserRepository.java
├── config/
│   └── RedisConfig.java
├── resources/
│   ├── application.properties
└── ...
```
```
