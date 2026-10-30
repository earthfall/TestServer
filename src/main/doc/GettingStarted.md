# 실행 및 검증 가이드

TestServer는 Spring Boot 배포와 API 기술을 검증하는 실험용 서버입니다.
Java 25, Gradle Wrapper, MVC 가상 스레드, Protobuf, Firebase FCM v1을 사용합니다.
사용자 인증이 구현된 운영 서비스는 아닙니다.

## 로컬 실행

JDK 25를 설치하고 `JAVA_HOME`을 설정합니다. 저장소 루트에서 실행합니다.

```powershell
.\gradlew.bat bootRun
curl.exe http://localhost:8080/hello/world
curl.exe http://localhost:8081/actuator/health
curl.exe -H "Accept: application/json" http://localhost:8080/permission-valid/world
curl.exe -i http://localhost:8080/permission/world
```

기본 응답은 각각 `Hello world!`, health 상태, `This hello`, HTTP 403입니다.
Linux에서는 `.\gradlew.bat` 대신 `./gradlew`을 사용합니다.
기본 기동과 위 요청은 Firebase 또는 DynamoDB 서버를 요구하지 않습니다.
DynamoDB Local 연동은 `--args='--spring.profiles.active=local'`로 실행합니다.
실제 DynamoDB 작업을 호출하려면 해당 서버를 별도로 준비해야 합니다.

## 테스트

```powershell
.\gradlew.bat build
```

테스트 보고서는 `build/reports/tests/test/index.html`에 생성됩니다.
테스트에는 모듈 경계, 입력 검증, 외부 호출 실패, 권한 검사,
Protobuf 응답, 변환 처리, 가상 스레드 검증이 포함됩니다.
Firebase 관련 테스트는 모의 응답과 대체 인증 정보를 사용합니다.
실제 알림 전송 성공이나 실제 클라우드 권한을 보증하는 테스트는 아닙니다.

Bash 환경에서는 다음 검증도 실행할 수 있습니다.

```sh
bash .github/scripts/check-nullness.sh
bash .github/scripts/check-container.sh
```

두 번째 명령은 Docker와 curl이 필요하며 이미지를 빌드하고 임시 컨테이너에서
health, 인사 API, 권한 허용·거부를 확인한 후 컨테이너를 제거합니다.
호스트 포트는 자동 배정하며 루프백 주소에만 바인딩합니다.
GitHub Actions에서도 빌드와 테스트 후 동일한 검증을 수행합니다.

## 실제 Firebase 전송

`FIREBASE_PROJECT_ID`와 Google Application Default Credentials를 준비합니다.
서비스 계정 파일을 쓴다면 저장소 밖에 두고 `GOOGLE_APPLICATION_CREDENTIALS`로
경로를 지정합니다. 인증 정보는 첫 전송 시 읽습니다.
요청 본문과 설정은 [API 계약](ApiAndConfiguration.md)을 참고합니다.

`POST /send`는 실제 알림을 전송하며 현재 호출자 인증과 호출 제한이 없습니다.
현재 서버는 신뢰할 수 있는 로컬 테스트 환경에서 실행해야 합니다.
공유 환경으로 확장할 때는 호출자 인증, 전송 권한, 호출량 제한을 먼저 구현합니다.
`FIREBASE_API_URL` 변경만으로 Google 인증 과정이 모의 처리되지는 않습니다.

## 구현 범위와 다음 검증

* 권한 기능은 고정 문자열을 비교하는 예제이며 사용자별 권한 저장소가 없습니다.
* Protobuf는 직렬화 형식입니다. 통신 암호화는 배포 환경의 TLS 설정이 담당합니다.
* Spring 이벤트는 발행 스레드에서 동기 실행됩니다.
* Dockerfile은 계층 추출과 JVM AOT 캐시를 검증하는 배포 경로입니다.
  Jib는 별도의 이미지 생성 경로이며 Dockerfile의 AOT 학습 절차를 실행하지 않습니다.
* 가상 스레드 활성화는 성능 개선 수치를 보증하지 않습니다. 동일한 모의 외부 지연,
  동시 사용자 수와 JVM 자원으로 활성화 전후 처리량, p95 지연, 오류율을 비교해야 합니다.
  측정값을 확보하기 전에는 성능 목표 달성을 주장하지 않습니다.

추가 설명: [AOT 캐시](AotCache.md), [nullness 검증](Nullness.md),
[모듈 구조](Modulith.md).
