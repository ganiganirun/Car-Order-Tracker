# Car-Order-Tracker

## 프로젝트 개요

차량 주문 및 생산 추적 시스템을 위한 백엔드 서비스 및 머신러닝 학습 스크립트를 포함하는 프로젝트입니다.

* **프로젝트 기간**: 2025.05.27 \~ 2025.07.08

## 팀 구성

팀원 및 역할을 아래와 같이 기재하세요.

* 송가은 (order, payment, history , AOP - Logging, Batch, waitingOrder, event처리, DB 분리)
* 최성원 (머신러닝 활용 차량 출고일 예측, 상담)
* 유세영 (Model, Option, MyCar, Email, event 처리)
* 현정근 (인증/인가, Master, Dealer, User, License, Front, Intercepter - Logging)


## 주요 기능

* **인증/인가**: Master, Dealer, User 역할 기반 JWT 인증 (Spring Security)
* **회원 관리**: Master, Dealer, User CRUD 기능
* **도메인**: 차량 모델(Model), 옵션(Option), 주문(Order), 사용자 보유 차량(MyCar), 결제(Payment), 이벤트(Event), 이력(History), 알림(Email)
* **결제 연동**: 아임포트(iamport-rest-client-java)
* **메시지 처리**: RabbitMQ(Spring AMQP)
* **캐싱**: Caffeine
* **로깅**: AOP 기반 메소드 실행 로그
* **QueryDSL**: 동적 쿼리 지원
* **머신러닝**: MLP머신러닝모델 기반 공정 소요 시간 예측 스크립트 (`train.py`) + 자동화
* **배치**: 대기 중인 주문을 조회해 외부 ML API로 예측 공정 소요 시간을 계산, 이를 기반으로 공정 이력을 생성해 DB에 저장하는 배치 작업 + 자동화


## 핵심 기능

* **인증/인가**: 역할별 접근 제어로 보안을 강화
* **주문 관리**: 차량 주문 생성·조회·추적 워크플로우 완성
* **결제 연동 & 이벤트 처리**: iamport 결제와 RabbitMQ 기반 비동기 이벤트
* **ML 기반 예측**: MLP머신러닝모델을 통한 생산 소요 시간 예측 + 자동화
* **이메일 알림**: 결제 완료, 주문 상태 변경 등 주요 이벤트 발생 시 사용자에게 이메일로 실시간 알림을 보내주는 기능도 프로젝트 가치를 높여줄 수 있습니다.


## 기술 스택

* **백엔드**: Java 17, Spring Boot 3.5, Gradle
* **DB**: MySQL 8
* **메시징**: RabbitMQ
* **ORM**: Spring Data JPA, Hibernate
* **보안**: Spring Security, JWT
* **메일**: Spring Mail (Gmail SMTP)
* **캐시**: Spring Cache + Caffeine
* **결제**: iamport-rest-client-java
* **스크립트**: Python 3.8+, pandas, scikit-learn, SQLAlchemy 등
* **인프라**: Docker, Docker Compose

## 사전 준비

1. Java 17 설치
2. Python 3.8 이상 및 `pip` 설치
3. Docker & Docker Compose 설치
4. VSCode REST Client 등 HTTP 클라이언트

## 환경 변수 설정

프로젝트 루트에서 `.env.example`을 복사하여 `.env` 파일을 생성하고, 아래 내용을 채워주세요:

```dotenv
# Local
LOCAL_DB_USERNAME=your_local_username
LOCAL_DB_PASSWORD=your_local_password

# Dev
DB_HOST=your_db_host
DB_PORT=your_db_port
DB_NAME=your_db_name
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# MySQL
MYSQL_ROOT_PASSWORD=your_root_password

# JWT
SPRING_JWT_SECRET=your_jwt_secret

# 아임포트
IMP_API_KEY=your_imp_api_key
IMP_SECRET_KEY=your_imp_secret_key

# RabbitMQ
RABBITMQ_HOST=your_rabbitmq_host
RABBITMQ_PORT=your_rabbitmq_port
RABBITMQ_USERNAME=your_rabbitmq_username
RABBITMQ_PASSWORD=your_rabbitmq_password

# Email (Gmail)
EMAIL_ADDRESS=your_email_address
EMAIL_PASSWORD=your_email_password

# 머신러닝 DB
DB_MLP_HOST=your_ml_db_host
DB_MLP_PORT=your_ml_db_port
DB_MLP_NAME=your_ml_db_name
```

## 인프라 실행

```bash
docker-compose up -d
```

* MySQL, Redis, RabbitMQ 컨테이너 실행

## 데이터베이스 초기화

```bash
mysql -h localhost -P 3308 -u root -p${MYSQL_ROOT_PASSWORD} osid < sql/master.sql
mysql -h localhost -P 3308 -u root -p${MYSQL_ROOT_PASSWORD} osid < sql/dealer.sql
# ... 도메인별 SQL 실행
```

## 애플리케이션 실행

```bash
# Unix/Mac
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

* 서버 실행: `http://localhost:8080`

## API 테스트

`api/` 폴더 내 `.http` 파일 사용 (VSCode REST Client)

## 테스트

```bash
./gradlew test
```

## ML 모델 학습

```bash
pip install -r requirements.txt
python train.py
```

* `mlp_single_custom.pkl` 생성

## 프로젝트 구조

```text
├─src/main/java/com/example/osid
│  ├─config
│  ├─common
│  ├─domain
│  └─event
├─api/
├─sql/
├─train.py
├─requirements.txt
├─docker-compose.yml
├─.env.example
└─.env.example
```

## 프론트엔드

프론트엔드는 백엔드 저장소에 포함되어 있지 않으며, 별도 리포지토리로 관리됩니다.

* 저장소 URL: `https://osid-system.replit.app/`

