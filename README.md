# Lotto Web Backend

CLI 기반 로또 프로그램을 웹 애플리케이션으로 확장한 REST API 서버입니다.  
로또 구매, 당첨 결과 계산, 누적 통계를 제공하는 API를 포함합니다.

## 실행 방법
```bash
./gradlew bootRun
```

## 주요 기능
- 구매 금액을 입력하면 자동으로 로또 번호를 생성 및 저장
- 구매 내역 조회 (목록 / 상세)
- 당첨 번호 입력 시 등수별 집계, 총 당첨 금액, 수익률 계산
- 모든 구매의 결과를 기반으로 누적 통계 제공
- 일관된 예외 응답 포맷을 갖춘 REST API 제공

## 기술 스택
- Java 21
- Spring Boot 3.5.7
- Spring Web
- JUnit 5
- In-memory Repository (데이터베이스 미사용)

## 프로젝트 구조

```text
src/main/java/io.woohyeon.lotto.lotto_web
├── config             # CORS 관련 설정
├── controller         # REST API
├── service            # 비즈니스 로직
├── repository         # 인메모리 저장소 (PurchaseStore, ResultStore)
├── model              # Lotto, Rank, PurchaseLog, WinningNumbers 등 도메인 모델
├── support            # LottoGenerator, LottoStatistics, LottoRules 등 유틸/도메인 지원 클래스
└── dto
    ├── request        # Request DTO
    └── response       # Response DTO
```

## API 명세

### 1. 로또 구매 생성
- `POST /lottos`
- Request Body
  ```json
  {
    "purchaseAmount": 5000
  }
  ```
- Response: `201 Created` + `Location` 헤더 혹은 JSON

### 2. 로또 구매 목록 조회
- `GET /lottos`
- Response Body
  ```json
  {
    "count": 3,
    "purchases": [
      {
        "id": 1,
        "purchaseAmount": 5000,
        "lottoCount": 5,
        "purchasedAt": "2025-11-17T12:34:56",
        "hasResult": true,
        "returnRate": 75.0
      }
    ]
  }
  ```

### 3. 로또 구매 상세 조회
- `GET /lottos/{id}`
- Response Body
  ```json
  {
    "id": 1,
    "purchaseAmount": 5000,
    "lottoCount": 5,
    "lottos": [
      {
        "numbers": [3, 11, 15, 22, 34, 41],
        "issuedAt": "2025-11-17T12:34:56"
      }
    ],
    "purchasedAt": "2025-11-17T12:34:56"
  }
  ```

### 4. 당첨 결과 등록/수정
- `PUT /lottos/{id}/result`
- Request Body
  ```json
  {
    "lottoNumbers": [3, 11, 15, 22, 34, 41],
    "bonusNumber": 7
  }
  ```
- Response Body
  ```json
  {
    "purchaseId": 1,
    "purchaseAmount": 5000,
    "totalPrize": 15000,
    "returnRate": 300.0,
    "rankCounts": [
      { "rank": "FIRST", "count": 0 },
      { "rank": "SECOND", "count": 0 }
    ]
  }
  ```

### 5. 당첨 결과 조회
- `GET /lottos/{id}/result`
- Response Body: 당첨 결과 등록/수정과 동일
- 결과 미등록 시: `400/404` + `"아직 당첨 결과가 없습니다."`

### 6. 통계 조회
- `GET /lottos/statistics`
- Response Body
  ```json
  {
    "totalSamples": 25,
    "averageReturnRate": 42.5,
    "accumulatedRankCounts": [
      { "rank": "FIRST", "count": 0 },
      { "rank": "SECOND", "count": 0 },
      { "rank": "THIRD", "count": 1 },
      { "rank": "FOURTH", "count": 2 },
      { "rank": "FIFTH", "count": 5 }
    ]
  }
  ```

### 공통 에러 포맷
```json
{
  "status": 500,
  "errorCode": "INTERNAL_SERVER_ERROR",
  "message": "에러 메시지",
  "timestamp": "2025-11-17T13:25:39.12814"
}
```

> 더 자세한 Request/Response 필드는 `/src/main/java/.../dto` 디렉토리를 참고하십시오.


