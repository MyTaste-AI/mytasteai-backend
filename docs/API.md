# MyAIMentor API 문서

전 서비스 API 통합 명세. 클라이언트는 항상 **Gateway(8080)** 으로만 호출하며, Gateway 가 user/mentor/chat 으로 라우팅한다.

- **Base URL**: `http://localhost:8080`
- **인증 방식**: JWT (Access Token, Bearer)
- **공통 헤더**: `Authorization: Bearer {accessToken}` (public 엔드포인트 제외)
- **Content-Type**: `application/json`

---

## 0. 공통 사항

### 0.1 인증

- 토큰은 `POST /auth/login` 으로 발급.
- 이후 모든 인증 필요 엔드포인트는 `Authorization: Bearer {accessToken}` 헤더 첨부.
- 토큰 만료/위조 시 401 응답 (`AUTH-001`).

### 0.2 권한 (Role)

| Role | 가능한 작업 |
|---|---|
| `USER` | 회원가입/로그인, 본인 정보, 봇/카테고리 **조회**, 채팅, 지식 **조회** |
| `ADMIN` | 위 + 봇/카테고리/지식 **생성·수정·삭제** |

### 0.3 공통 에러 응답 스키마

모든 에러 응답은 다음 형식.

```json
{
  "code": "AUTH-002",
  "message": "이메일 또는 비밀번호가 올바르지 않습니다.",
  "timestamp": "2026-04-26T15:30:00.000Z",
  "path": "/auth/login"
}
```

검증 실패(`SYS-001`) 시 `errors[]` 추가:

```json
{
  "code": "SYS-001",
  "message": "요청 값이 올바르지 않습니다.",
  "timestamp": "2026-04-26T15:30:00.000Z",
  "path": "/auth/signup",
  "errors": [
    { "field": "email", "rejectedValue": "abc", "reason": "이메일 형식이어야 합니다" },
    { "field": "password", "rejectedValue": "123", "reason": "크기가 8에서 64 사이여야 합니다" }
  ]
}
```

### 0.4 ErrorCode 전체 목록

| 코드 | HTTP | 메시지 |
|---|---|---|
| `AUTH-001` | 401 | 인증이 필요합니다. |
| `AUTH-002` | 401 | 이메일 또는 비밀번호가 올바르지 않습니다. |
| `AUTH-003` | 409 | 이미 사용 중인 이메일입니다. |
| `USER-001` | 404 | 사용자를 찾을 수 없습니다. |
| `BOT-001` | 404 | 봇을 찾을 수 없습니다. |
| `CAT-001` | 404 | 카테고리를 찾을 수 없습니다. |
| `CAT-002` | 409 | 이미 존재하는 카테고리입니다. |
| `CAT-003` | 400 | 존재하지 않는 카테고리입니다. |
| `CHAT-001` | 404 | 세션을 찾을 수 없습니다. |
| `EXT-001` | 502 | mentor 봇 조회 실패 |
| `EXT-002` | 502 | AI generate 실패 |
| `EXT-003` | 502 | AI bot-vector upsert 실패 |
| `EXT-004` | 502 | AI bot-vector 삭제 실패 |
| `EXT-005` | 502 | AI knowledge 등록 실패 |
| `EXT-006` | 502 | AI knowledge 조회 실패 |
| `EXT-007` | 502 | AI knowledge 삭제 실패 |
| `SYS-001` | 400 | 요청 값이 올바르지 않습니다. |
| `SYS-002` | 500 | 서버 오류가 발생했습니다. |

---

## 1. Auth API (`/auth/**`)

### 1.1 회원가입

```
POST /auth/signup
인증: 불필요
```

**Request**

```json
{
  "email": "user@example.com",
  "password": "password123",
  "name": "홍길동"
}
```

| 필드 | 타입 | 제약 |
|---|---|---|
| email | string | `@Email`, 필수 |
| password | string | 8~64자, 필수 |
| name | string | 1~100자, 필수 |

**Response — 201 Created**

```
HTTP/1.1 201 Created
Location: /users/42
(body 없음)
```

**Errors**
- `409 AUTH-003` — 이미 사용 중인 이메일
- `400 SYS-001` — 검증 실패 (이메일 형식, 비번 길이 등)

---

### 1.2 로그인

```
POST /auth/login
인증: 불필요
```

**Request**

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response — 200 OK**

```json
{
  "tokenType": "Bearer",
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 3600
}
```

> `expiresIn` 단위: **초**. 기본 1시간(3600).

**Errors**
- `401 AUTH-002` — 이메일/비번 불일치 (이메일 미존재 / 비번 틀림 모두 같은 메시지)
- `400 SYS-001` — 검증 실패

---

### 1.3 로그아웃

```
POST /auth/logout
인증: 불필요 (서버는 stateless)
```

**Response — 204 No Content**

> 서버는 별도 처리 없음. 클라이언트가 토큰을 폐기해야 한다.

---

### 1.4 내 정보 조회

```
GET /auth/me
인증: 필수
```

**Response — 200 OK**

```json
{
  "id": 42,
  "email": "user@example.com",
  "name": "홍길동",
  "role": "USER",
  "createdAt": "2026-04-26T10:00:00.000Z"
}
```

**Errors**
- `401 AUTH-001` — 토큰 누락/만료/위조 / 사용자 삭제됨

---

## 2. User API (`/users/**`)

### 2.1 사용자 단건 조회

```
GET /users/{id}
인증: 필수 (USER 이상)
```

**Response — 200 OK**

```json
{
  "id": 42,
  "email": "user@example.com",
  "name": "홍길동",
  "role": "USER"
}
```

**Errors**
- `404 USER-001` — 존재하지 않는 ID

---

### 2.2 내 정보 수정

```
PATCH /users/me
인증: 필수
```

**Request**

```json
{ "name": "홍길순" }
```

| 필드 | 타입 | 제약 |
|---|---|---|
| name | string | 1~100자 (null 이면 변경 안 함) |

**Response — 200 OK**

```json
{
  "id": 42,
  "email": "user@example.com",
  "name": "홍길순",
  "role": "USER"
}
```

**Errors**
- `401 AUTH-001` — 토큰 누락 / 토큰 사용자 삭제됨
- `400 SYS-001` — 길이 위반

---

## 3. Category API (`/categories/**`)

### 3.1 카테고리 목록

```
GET /categories
인증: 필수 (USER 이상)
```

**Response — 200 OK**

```json
[
  { "id": 1, "name": "프로그래밍" },
  { "id": 2, "name": "디자인" }
]
```

> id ASC 정렬 고정. 페이징 없음.

---

### 3.2 카테고리 생성

```
POST /categories
인증: 필수 (ADMIN)
```

**Request**

```json
{ "name": "프로그래밍" }
```

| 필드 | 타입 | 제약 |
|---|---|---|
| name | string | 1~100자, 필수 |

**Response — 201 Created**

```
HTTP/1.1 201 Created
Location: /categories/1
(body 없음)
```

**Errors**
- `409 CAT-002` — 이름 중복
- `403` — ADMIN 아님 (Spring Security 기본 응답)

---

### 3.3 카테고리 삭제

```
DELETE /categories/{id}
인증: 필수 (ADMIN)
```

**Response — 204 No Content**

**Errors**
- `404 CAT-001` — 미존재
- `403` — ADMIN 아님

---

## 4. Bot API (`/bots/**`)

### 4.1 봇 목록 (페이징)

```
GET /bots?categoryId={id}&page=0&size=20&sort=id,desc
인증: 필수 (USER 이상)
```

| Query | 설명 | 기본값 |
|---|---|---|
| categoryId | 카테고리 필터 (옵션) | 없음 = 전체 |
| page | 페이지 번호 | 0 |
| size | 페이지 크기 | 20 |
| sort | 정렬 | `id,desc` |

**Response — 200 OK**

```json
{
  "content": [
    {
      "id": 5,
      "name": "Python 멘토",
      "description": "Python 입문자를 도와주는 봇",
      "provider": "OPENAI",
      "categoryId": 1
    }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 20, "sort": { "sorted": true } },
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "size": 20,
  "number": 0,
  "numberOfElements": 1,
  "empty": false
}
```

> Spring Data 의 `Page<T>` 표준 직렬화 형식. `content[]` 가 실제 데이터.

---

### 4.2 봇 상세

```
GET /bots/{id}
인증: 필수
```

**Response — 200 OK**

```json
{
  "id": 5,
  "name": "Python 멘토",
  "description": "Python 입문자를 도와주는 봇",
  "systemPrompt": "당신은 친절한 Python 강사입니다...",
  "provider": "OPENAI",
  "categoryId": 1,
  "createdBy": 1,
  "createdAt": "2026-04-26T10:00:00.000Z",
  "updatedAt": "2026-04-26T10:00:00.000Z"
}
```

**Errors**
- `404 BOT-001`

---

### 4.3 봇 생성

```
POST /bots
인증: 필수 (ADMIN)
```

**Request**

```json
{
  "name": "Python 멘토",
  "description": "Python 입문자를 도와주는 봇",
  "systemPrompt": "당신은 친절한 Python 강사입니다...",
  "provider": "OPENAI",
  "categoryId": 1
}
```

| 필드 | 타입 | 제약 |
|---|---|---|
| name | string | 1~100자, 필수 |
| description | string | 필수 |
| systemPrompt | string | 필수 |
| provider | enum | `OPENAI` 또는 `GEMINI`, 필수 |
| categoryId | long | 양수, 필수 |

**Response — 201 Created**

```
HTTP/1.1 201 Created
Location: /bots/5
(body 없음)
```

**Errors**
- `400 CAT-003` — 카테고리 미존재
- `502 EXT-003` — AI 벡터 동기화 실패 (트랜잭션 롤백 후 응답)
- `403` — ADMIN 아님

---

### 4.4 봇 수정 (부분)

```
PATCH /bots/{id}
인증: 필수 (ADMIN)
```

**Request** (모든 필드 옵셔널, null 인 필드는 변경 안 함)

```json
{
  "description": "새 설명",
  "provider": "GEMINI"
}
```

**Response — 200 OK** (`BotResponse` 와 동일)

**Errors**
- `404 BOT-001`
- `400 CAT-003` — categoryId 변경했는데 미존재
- `502 EXT-003/004` — AI 재동기화 실패

---

### 4.5 봇 삭제

```
DELETE /bots/{id}
인증: 필수 (ADMIN)
```

**Response — 204 No Content**

**Errors**
- `404 BOT-001`
- `502 EXT-004` — AI 벡터 삭제 실패

---

## 5. Knowledge API (`/bots/{botId}/knowledge/**`)

> **AI 마이크로서비스로 위임** — Spring DB 에 별도 저장하지 않고 응답을 그대로 전달.

### 5.1 지식 목록

```
GET /bots/{botId}/knowledge?limit=20&offset=0
인증: 필수
```

| Query | 설명 | 기본값 | 제약 |
|---|---|---|---|
| limit | 가져올 개수 | 20 | 1~100 |
| offset | 시작 위치 | 0 | ≥ 0 |

**Response — 200 OK**

```json
[
  {
    "id": 101,
    "bot_id": 5,
    "content": "Python 의 list comprehension 은...",
    "provider": "openai",
    "has_embedding": true,
    "created_at": "2026-04-26T10:00:00+00:00"
  }
]
```

> 응답 필드명은 AI 서비스의 snake_case 그대로.

**Errors**
- `404 BOT-001`
- `502 EXT-006` — AI 호출 실패

---

### 5.2 지식 등록

```
POST /bots/{botId}/knowledge
인증: 필수 (ADMIN)
```

**Request**

```json
{ "content": "Python 의 list comprehension 은..." }
```

**Response — 201 Created** (5.1 의 단일 객체 형태)

**Errors**
- `404 BOT-001`
- `502 EXT-005`

---

### 5.3 지식 삭제

```
DELETE /bots/{botId}/knowledge/{id}
인증: 필수 (ADMIN)
```

**Response — 204 No Content**

**Errors**
- `404 BOT-001`
- `502 EXT-007`

---

## 6. Chat API (`/chat/**`)

### 6.1 질문 송신 / 답변 받기

```
POST /chat
인증: 필수
```

**Request**

```json
{
  "sessionId": null,
  "botId": 5,
  "question": "list comprehension 이 뭐야?"
}
```

| 필드 | 타입 | 제약 |
|---|---|---|
| sessionId | string | 옵셔널. null 이면 새 세션 생성, 있으면 기존 세션에 이어쓰기 |
| botId | long | 양수, 필수 |
| question | string | 필수 |

**Response — 200 OK**

```json
{
  "sessionId": "65a1f...",
  "botId": 5,
  "answer": "list comprehension 은 리스트를 간결하게 만드는 문법입니다...",
  "mode": "openai"
}
```

> `mode`: AI 서비스 동작 모드. `dummy` (개발용) 또는 provider 명.

**Errors**
- `404 BOT-001` — 봇 없음
- `404 CHAT-001` — sessionId 가 본인 세션이 아니거나 미존재
- `502 EXT-001` — mentor 봇 조회 실패
- `502 EXT-002` — AI generate 실패

---

### 6.2 내 세션 목록

```
GET /chat/sessions?page=0&size=20&sort=updatedAt,desc
인증: 필수
```

| Query | 설명 | 기본값 |
|---|---|---|
| page | 페이지 번호 | 0 |
| size | 페이지 크기 | 20 |
| sort | 정렬 | `updatedAt,desc` (최근 활동 순) |

**Response — 200 OK**

```json
{
  "content": [
    {
      "id": "65a1f...",
      "botId": 5,
      "title": "list comprehension 이 뭐야?",
      "messageCount": 4,
      "lastMessagePreview": "...간결하게 만드는 문법입니다.",
      "createdAt": "2026-04-26T10:00:00.000Z",
      "updatedAt": "2026-04-26T10:05:00.000Z"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "size": 20,
  "number": 0,
  "empty": false
}
```

> 본인 세션만 반환. `messages[]` 본문은 제외, 미리보기만 포함.

---

### 6.3 세션 상세 (메시지 전체)

```
GET /chat/sessions/{id}
인증: 필수
```

**Response — 200 OK**

```json
{
  "id": "65a1f...",
  "botId": 5,
  "title": "list comprehension 이 뭐야?",
  "messages": [
    {
      "role": "USER",
      "content": "list comprehension 이 뭐야?",
      "createdAt": "2026-04-26T10:00:00.000Z"
    },
    {
      "role": "ASSISTANT",
      "content": "list comprehension 은 리스트를 간결하게 만드는 문법입니다...",
      "createdAt": "2026-04-26T10:00:02.000Z"
    }
  ],
  "createdAt": "2026-04-26T10:00:00.000Z",
  "updatedAt": "2026-04-26T10:00:02.000Z"
}
```

> `role` enum: `USER` | `ASSISTANT`

**Errors**
- `404 CHAT-001` — 세션 미존재 / 본인 세션 아님 (구분하지 않음 — 보안)

---

## 7. Gateway 라우팅 매핑 (참고)

내부적으로 이렇게 라우팅된다 (클라이언트는 신경 안 써도 됨).

| 패턴 | → 대상 서비스 | 포트 |
|---|---|---|
| `/auth/**` | user | 8081 |
| `/users/**` | user | 8081 |
| `/categories/**` | mentor | 8082 |
| `/bots/**` | mentor | 8082 |
| `/chat`, `/chat/**` | chat | 8083 |

> Public path (`/auth/signup`, `/auth/login`, `/auth/logout`) 는 Gateway 1차 검증을 거치지 않고 user 서비스로 forward.

---

## 8. cURL 빠른 참고

```bash
# 1. 회원가입
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"a@b.com","password":"password123","name":"홍길동"}'

# 2. 로그인 → 토큰 받기
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"a@b.com","password":"password123"}' | jq -r .accessToken)

# 3. 내 정보
curl http://localhost:8080/auth/me -H "Authorization: Bearer $TOKEN"

# 4. 카테고리 목록
curl http://localhost:8080/categories -H "Authorization: Bearer $TOKEN"

# 5. 봇 목록
curl "http://localhost:8080/bots?page=0&size=20" -H "Authorization: Bearer $TOKEN"

# 6. 채팅
curl -X POST http://localhost:8080/chat \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"sessionId":null,"botId":1,"question":"안녕"}'
```
