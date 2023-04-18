# 프로젝트 소개

<img width="240" alt="KakaoTalk_20230408_184239973" src="https://user-images.githubusercontent.com/107843779/232654475-7c25c85b-8b9f-4319-bf50-4a2182afb3d8.png">


## 📚 기술스택

<div align="center">
	<img src="https://img.shields.io/badge/Java-007396?style=flat&logo=Java&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat&logo=Spring Boot&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat&logo=Spring Security&logoColor=white" />
  <img src="https://img.shields.io/badge/Amazon S3-569A31?style=flat&logo=Amazon S3&logoColor=white" />
  <img src="https://img.shields.io/badge/Amazon RDS-527FFF?style=flat&logo=Amazon RDS&logoColor=white" />
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=MySQL&logoColor=white" />
  <img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=flat&logo=Amazon EC2&logoColor=white" />
</div>


## 💡 구현기능

### 1. 유저의 위치기반 반려동물 동반 시설 정보 및 추천 (map)
### 2. 방문 시설에 대한 리뷰 작성
### 3. 필터를 사용한 조회
### 4. 유저, 사업자 모드가 분리되어 사업자의 경우 업체등록 가능
### 5. 채팅문의와 후기 등록시 알림 기능

## 🏗️ API명세서

![화면 캡처 2023-04-18 191834](https://user-images.githubusercontent.com/107843779/232747808-52219ec5-a182-42af-a466-a9203bb2e32e.png)

https://www.notion.so/266ffd7e3c204b8792fb1c69e2d451f4?v=57c8874ec74d46e8b455455ee6ce58da

## 🧱 ERD

<img width="970" alt="스크린샷 2023-04-18 오전 2 43 48" src="https://user-images.githubusercontent.com/107843779/232655250-e93b3cb3-68dd-45bc-bee0-49279482f5e7.png">


## 🔗 와이어 프레임

<img width="1116" alt="1" src="https://user-images.githubusercontent.com/107843779/232656078-f32b5829-1db7-4fc8-a9bc-528801995fda.png">
<img width="1088" alt="2" src="https://user-images.githubusercontent.com/107843779/232656083-3951d433-8741-43cd-90ab-268866118d71.png">


## ⁉️ Trouble Shooting

### 1. 리눅스 권한
    
#### 문제상황
    
    - CodeDeploy를 통해서 CICD를 구축해서 AWS EC2에 Spring Server를 배포.
    - FE서버와의 연결상태 확인.
    - 게시글 작성에서 이미지를 리스트로 업로드하는 부분이 서버상에서 작동하지 않는다..
    
#### 에러코드
    
    - java.io.ioexception: permission denied
    - ??? 갑자기 이게 무슨일인가 했다.
    
#### 시도 및 확인
    
    - Local환경에서 Test 결과 - 성공
    - 서버 환경에서 Test결과 - 실패
    - 어떤 변수값에 의한 문제발생인지 파악이 필요했다.
    - 대부분의 기능들이 정상적으로 작동하나 이미지 업로드와 관련된 부분에서 동일한 에러가 발생함을 확인.
        - 이미지 사이즈에 의한 문제인가? - 아니다 - 로컬상에도 사이즈를 줄여서 해봤지만 지정한 예외가 처리되게 되어있다.
        - 이미지 용량에 의한 문제인가? - 아니다 - 로컬상에서 도합 10MB넘는 용량을 업로드하면 지정한 예외가 처리되었다.
    - 코드나 기능상에서 예상할 수 있는 문제는 모두 아닌것으로 생각되었다.
    - 환경이나 설정의 문제라고 생각.
    - 권한이라는 단어에 집중했다 -> 어디의 권한인가? -> 서버상의 권한일것 -> 서버는 어디인가? -> 우분투 서버이며 이는 리눅스 기반의 서버이다.
    - 시도1. 현재 ubuntu 에서 사용자 권환상태를 확인.
    - 시도2. 권한 침범이 예상되는 경로에 chmod 755 -R /경로 를 사용해서 권한 업데이트 후 메서드 실행 - 실패
    
#### 해결
    
    - appspec.yml에서 petmissions 부분을 변경.
    아래 
version: 0.0
os: linux

files:
  - source:  /
    destination: /home/ubuntu/app
    overwrite: yes

permissions:
  - object: /
    pattern: "**"
    owner: ubuntu -> root 로 변경
    group: ubuntu

hooks:
  AfterInstall:
    - location: scripts/stop.sh
      timeout: 60
      runas: ubuntu
  ApplicationStart:
    - location: scripts/start.sh
      timeout: 60
      runas: root
