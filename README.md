# GPT_ImageLabeling_Diary
- TenserFlow Lite와 GPT를 활용하여 사진 일기를 작성하는 Android 기반 어플리케이션입니다.
- 사용자가 사진을 고르면 이미지 라벨링을 통해 태그를 생성합니다.
- 생성한 태그를 기반으로 GPT가 사용자에 맞는 질문을 합니다.
- 질문에 대한 답을 활용하여 각 사진에 대한 일기를 작성합니다.
<img src="https://github.com/YooKyungmi/week1/blob/main/screenshot/KakaoTalk_20230705_191243673.jpg" width="200">

## 팀원
* [차민호](https://github.com/Cha-Minho)  
* [유경미](https://github.com/YooKyungmi)  

## 개발 환경
- **OS**: Android (minSdk: 19, targetSdk: 31)
- **Language**: Kotlin
- **IDE**: Android Studio
- **Target Device**: Galaxy S7

## 기능 설명

### 탭1 – 연락처
- Tab1에서는 현재 기기에 저장된 연락처들을 확인할 수 있습니다.
- 인물 item을 누르면 해당 인물의 상세 정보를 확인할 수 있습니다.
- 상세정보에는 인물의 이름과 번호가 표시됩니다.
- add 버튼을 누르면 연락처가 추가됩니다.
- 인물 리스트의 인물을 좌측으로 스와이프하여 수정과 삭제를 할 수 있습니다.
<img src="https://github.com/YooKyungmi/week1/blob/main/screenshot/KakaoTalk_20230705_191243673_01.jpg" width="200">
<img src="https://github.com/YooKyungmi/week1/blob/main/screenshot/KakaoTalk_20230705_191243673_02.jpg" width="200">


#### 기술 설명
- 사용자에게 연락처에 대한 권한을 요청하여 외부 저장소에 접근하였다.
- 연락처 데이터를 쿼리문을 활용해 조회, 수정, 삭제 등을 구현하였다.
- 연락처 데이터가 변경될 때마다 notifyDataSetChanged()를 활용하여 실시간으로 정보를 갱신하여 사용자에게 보여주었다.

### 탭2 – 갤러리
- 사용자에게 연락처에 대한 권한을 요청하여 외부 저장소에 접근하였다.
- 사진 데이터를 쿼리문을 활용해 조회, 수정, 삭제 등을 구현하였다
- 우측 하단 카메라 버튼을 누르면 카메라 앱과 연결된다
 <img src="https://github.com/YooKyungmi/week1/blob/main/screenshot/KakaoTalk_20230705_191243673_03.jpg" width="200">

#### 기술설명
- viewPager을 통해 각 사진간 스와이프로 이동이 가능하다

### 탭3 – 이미지 라벨링과 OpenAI API를 통한 다이어리 작성
- 사용자가 갤러리에서 선택한 사진들을 사용하여 하루 일기를 작성한다
- 이미지 라벨링을 통해 사진에서 태그를 추출하고, 사용자는 태그 수정&삭제하여 정확도를 올린다
- 사진에 대한 태그를 통해 GPT가 사진에 대한 구체적인 질문을 작성하여 채팅 형식으로 인터뷰한다
- 질문에 대한 답변을 기반으로 각 사진에 대한 일기를 쓴다
<img src="https://github.com/YooKyungmi/week1/blob/main/screenshot/KakaoTalk_20230705_191243673_04.jpg" width="200">
<img src="https://github.com/YooKyungmi/week1/blob/main/screenshot/KakaoTalk_20230705_191243673_05.jpg" width="200">
<img src="https://github.com/YooKyungmi/week1/blob/main/screenshot/KakaoTalk_20230705_191243673_06.jpg" width="200">


#### 기술 설명
- **ImageLabeling**
    - Tensorflow에서 제공하는 ImageLabeling api를 바탕으로 (pretrained model) 사진으로부터 4~5개의 태그를 추출한다. 추출되는 태그의 후보는 labels.txt라는 파일로 주어지고 각 태그에 대한 score가 함께 주어진다. Score가 높은 4개의 태그를 선택하여 이를 바탕으로, 사용자가 하루에서 느낀 감정이나 생각을 묻는 질문을 생성하는데 사용한다.
- **OpenAI API**
    - ImageLabeling 절차에서 추출한 4개의 태그를 바탕으로, 각 이미지에 대한 질문을 1개 생성한다. 사용된 모델을 gpt-3.5-turbo이다. 생성된 질문과 그에 대한 사용자의 답변을 pair로 저장하여, 이를 다시 gpt API에 전달한다. GPT는 이 질의응답 내용을 바탕으로 사용자의 각 사진에 대한 짧은 요약이 담긴 일기를 작성한다.
