## 소개

해당 프로젝트는 **디자이너와 의뢰자를 이어주는 플랫폼**으로 스타트업을 준비하는 onfree에서 제안하여 개발에 참여하였으며 향후에  포트폴리오로 사용하는 것을 전제하에 프로젝트를 참여하였으며현재 투자가 미루어져 프로젝트는 개발은 종료된 상태이며 , 포트폴리오 용도로 추가적으로 개발 중입니다.

**※ 프로젝트가 잠정 중단된 관계로 관계자로 부터 모든 소스에 대한 공개를 허락받았습니다.**


[Onfree API Document](https://www.notion.so/Onfree-API-Document-985007ac18a64b9c811a96adf569eb01)

## 📘서비스 내용

그래픽픽, 캐릭터, 3D 일러스트, UI/UX 등 여러 디자인 분야 전문가에게 의뢰 외주 및 프로젝트, 채용 공고를 할 수 있는 웹 앱 플랫폼


### ☝️핵심 기능

1. 원하는 디자인 의뢰를 **[실시간 의뢰]**  게시글을 작성하여 해당 분야 여러 디자이너들이 해당 게시글에 지원하여 디자이너와 의뢰자를 이어주는 기능
2. 의뢰자가 의뢰하고자 하는 디자인 분야에 작가에게 직접 의뢰하여 이어주는 기능
3. 토스페이먼트를 사용 하여  안전하게 디자이너와 의뢰자의 거래
4. 디자이너들의 평판 및 솜씨를 확인 할 수 있는 포트폴리오룸
5. 소규모 의뢰가 아닌 프로젝트 단위에  제안 또는 채용 공고 기능 제공

## 🪛기술 스택

>- Spring
>  - spring boot, security, hateoas, Jpa
>- Swagger
>- DB 
>  - MariaDB, Redis, H2
>- AWS EC2, RDS, ElastiCache, S3
>- JWT
### 실행 방법
**※ 프로젝트 내부 중요 암호키들은 다 수정하였습니다(암호화x).**

```
> build
.\gradlew build

> 서버 start
docker-compose up
```

## 📖개발 내용

해당 프로젝트에서 모든 서버 개발과 DB 설계, 문서화 ,등 전반적인 백엔드 업무를 맡아 진행하였습니다.

1. **데이터 설계**
2. **모든 RestApi 개발**
    1. 로그인 구현 (회원가입, 로그인, 아이디 찾기, 패스워드 , 등)
        1. 작가 유저, 일반유저, 관리자 유저 분류
        2. JWT 토큰을 사용하여 인증 처리
        3. Redis를 활용하여 RefreshToken 관리 및 이메일 인증 관리
        4. ~~Oauth2 인증(네이버, 카카오, 인스타그램, 구글)~~
    2. 포트폴리오룸, 실시간 의뢰, 직접 의뢰
    3. 채팅 시스템 구현
        1. 웹소켓을 사용한 채팅 시스템 구현
        2. 채팅창에서 견적서, 결제 요청 알림, 환불 알림, 등 다양한 채팅 타입 구현
    4. PG 연동(토스페이먼트)
3. **문서화**
    1. Swagger를  사용하여 문서화 작업 진행
    2. Nothion으로 추가적으로 문서화 작업 진행
4. DevOps
    1. Docker, EC2, RDS, S3, ElastiCache, 연동

## 🔅향후 구현 계획

- 관리자 API
    - 각 설계된 데이터들을 조회 및 수정할 수 있는 관리자 API 구현
- Oauth2 로그인 기능 추가
    - 카카오, 구글, 인스타, 네이버
- Admin 권한에 세부 권한 분리
- 프로젝트 공고/채용 공고 구현

## 🎨서비스 디자인(피그마)

![01_메인페이지.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/b72c7c83-efcf-45a6-bb3d-fc37e1591082/01_메인페이지.png)

![03_로그인.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/fb09e0a4-d930-4c56-8527-a8d0892f1aa4/03_로그인.png)

![04_회원가입.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/de878ba3-d2f4-433e-8ed1-928dfacd5049/04_회원가입.png)

- 전체 이미지 보기

  ![01_메인페이지.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/bdfc1916-548d-4fb1-a218-4df00d6903d4/01_메인페이지.png)

  ![01_메인페이지_작가이미지.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/5deda421-eca9-47c9-83dc-84d74475be61/01_메인페이지_작가이미지.png)

  ![02_팝업창.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/fdf2f02f-ade9-4009-86dc-4ea49051240f/02_팝업창.png)

  ![03_로그인.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/bb73c00a-09a1-4e68-908a-d5de04b13358/03_로그인.png)

  ![04_회원가입.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/b00719bd-354b-4318-bdf0-143d46fb4771/04_회원가입.png)

  ![04_회원가입_set.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/fbc9b916-bc15-4db9-b623-8a72585dd874/04_회원가입_set.png)

  ![05_프로필_실시간의뢰.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/6832de69-6c6c-4c71-b6c9-78836bce7519/05_프로필_실시간의뢰.png)

  ![05_프로필_작가한정.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/f17276b1-bbdb-4606-8da2-3f890621ae4b/05_프로필_작가한정.png)

  ![05_프로필_최근결제(일반유저).png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/29629d94-49aa-45e1-a6f3-662b1f54311e/05_프로필_최근결제(일반유저).png)

  ![05_프로필_최근결제(작가).png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/847f1fae-b0cc-4730-a72e-7d5da7ec5495/05_프로필_최근결제(작가).png)

  ![06_프로필_내정보.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/f5145041-4193-45cc-88cc-dc3f688aecd6/06_프로필_내정보.png)

  ![07_프로필_알림설정.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/cb834d4f-f15c-4fa5-9629-dcee19db5573/07_프로필_알림설정.png)

  ![08_포트폴리오룸.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/a6344dde-7ad6-410d-a2d9-fbee16bb836d/08_포트폴리오룸.png)

  ![08_포트폴리오룸_19,영업설정 없음.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/4ff3d593-a6b8-4df8-a257-210f3b8c4533/08_포트폴리오룸_19영업설정_없음.png)

  ![09_포트폴리오룸_리뷰별점.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/d99828d0-3288-4bc9-bf96-a667fd8dcccd/09_포트폴리오룸_리뷰별점.png)

  ![10_고객센터.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/60570777-9357-49ad-b2b2-d3d665c4828c/10_고객센터.png)

  ![10_고객센터_이용약관.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/6943a3ff-790f-407b-bb38-5c7e89dd3d9b/10_고객센터_이용약관.png)

  ![12_업로드_2.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/82ab1446-9a4d-4045-88a4-965bf6f1ab66/12_업로드_2.png)

  ![13_업로드_2.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/81dfa6e4-a14e-4f0f-9962-9cbc2f1df0e9/13_업로드_2.png)

  ![13_업로드_2_19가림막.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/91840367-4c37-4421-8008-4b2be2a5af4d/13_업로드_2_19가림막.png)

  ![14_프로젝트공고.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/0a5d6e3a-d157-4829-bf76-6713d988d164/14_프로젝트공고.png)

  ![15_프로젝트공고_상세.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/76b0e460-94f3-4c25-b812-20dcb2f58f9e/15_프로젝트공고_상세.png)

  ![16_채용공고.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/f4108d9a-7a65-431c-a4a4-9cea5b9261da/16_채용공고.png)

  ![17_채팅시스템.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/634a4a29-c546-44b0-b342-652e8d20d7ac/17_채팅시스템.png)

  ![17_채팅시스템_더보기.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/080a1e8f-33ee-4ec8-9015-4a8b9b0ad1d7/17_채팅시스템_더보기.png)

  ![17_채팅시스템_제안서.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/aee7b9bd-b615-4676-9ff3-7cfc6cdd7ac8/17_채팅시스템_제안서.png)

  ![17_채팅시스템_지급요청.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/d95f95b1-8029-4f41-bdea-6a50b077c23e/17_채팅시스템_지급요청.png)

  ![18_채팅시스템_제안서.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/6202c45e-f28b-4a74-9daf-dda0b99530c3/18_채팅시스템_제안서.png)

  ![19_실시간의뢰.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/efd16096-c582-4b12-8883-ef8a5a6007c2/19_실시간의뢰.png)

  ![20_실시간의뢰_의뢰하기.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/ff8a1d6b-962b-4262-a8a3-20c750249ea8/20_실시간의뢰_의뢰하기.png)

  ![21_실시간의뢰_이용안내.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/65c9f83e-aa8d-4bc3-b2a6-36e676f62206/21_실시간의뢰_이용안내.png)

  ![22_견적서결제하기.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/9490a11c-371a-48e6-80e9-c2de60b24327/22_견적서결제하기.png)

  ![22_견적서결제하기_무통장(현금영수증발급).png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/f05f90fe-d30c-41c4-8c5f-2af9f353c1c5/22_견적서결제하기_무통장(현금영수증발급).png)