# 원데이 러닝
<img src="https://user-images.githubusercontent.com/74566094/111119105-07b3ea80-85ad-11eb-8590-3d72f673bb91.png" width = "50%|height = 30"/>  

## 어플 소개
대다수의 러닝 어플들은 사용자의 움직임을 측정하고 거리 별 칼로리, 운동거리를 측정합니다.

원데이 러닝은 사용자의 편의를 위해 현재 및 2시간 이후의 날씨까지 사용자에게 알려줍니다.  
또한 서버와 연동하여 사용자가 설정한 목표 및 자신이 적은 다짐, 목표까지 남은 거리와 함께 매일 몇 미터를 걸었는지 확인이 가능합니다.

마지막으로 사용자는 대화 공간을 통해 커뮤니티 기능을 사용할 수 있습니다.
작성되는 게시글들과 댓글은 서버에 기록되며, 자신이 쓴 글에 대해 확인이 가능합니다.

### < 로그인 구현부 >
사용자가 어플을 최초로 켰을 경우 가장 먼저 나타나는 로그인 구현 화면입니다.  
<img src="https://user-images.githubusercontent.com/74566094/133252889-4730064f-cb43-429c-ad49-5127296096d9.jpg" width = "50%|height = 30"/> 

### < 메인 화면 >
원데이 러닝 어플의 기본 화면입니다. 네비게이션 드로어와 바텀 네비게이션이 구현되어있으며, 기본 프래그먼트는 날씨 확인입니다.
<img src="https://user-images.githubusercontent.com/74566094/133252963-e073a50b-b809-4186-be39-e2fe648ab3e8.jpg" width = "50%|height = 30"/> 

### < 달리기 화면 >
바텀 네비게이션의 두 번째이자, 달리기 화면입니다. 시작 버튼을 누를 시 구글맵이 현재 사용자의 위치로 움직이며, 시작위치에 애드마커가 발생합니다. 이때, 사용자가 움직이기 시작하면 파란색 선이 나타나 사용자의 경로를 나타냅니다.  
종료 버튼을 누를 시 이동한 거리가 합계되어 서버에 전송되고, 화면에 출력됩니다.  
<img src="https://user-images.githubusercontent.com/74566094/133253328-9aff1e9b-74de-4eb0-a788-e4aaaadbc26f.jpg" width = "50%|height = 30"/> 

### < 달리기 기록 화면 >
사용자가 입력한 정보가 보관되는 장소입니다. 미입력 상태일시 기초값이 나타나게 되며, 아래의 리사이클러뷰로 DB에서 전송된 데이터를 받아와 사용자의 일일 운동 기록을 알려줍니다.
선택 정렬을 통해 최신 기록이 위로가게 만들었습니다.  
<img src="https://user-images.githubusercontent.com/74566094/133253378-61420020-d553-4361-a7ba-b72b06645de1.jpg" width = "50%|height = 30"/>

### < 대화 공간 화면 >

대화 공간입니다. 서버에서 받아온 데이터를 리사이클러뷰에 출력합니다. 글을 선택할 시 포지션 값에 따라 해당 글로 이동합니다.  
<img src="https://user-images.githubusercontent.com/74566094/133253871-9231fbe6-e1ba-49f4-82e1-7359d332b657.jpg" width = "50%|height = 30"/> 

게시글 작성이 가능한 화면입니다. 임시 저장 시 preference를 통해 보관이 가능하며, 등록버튼을 누를시 해당 글이 서버에 전송됩니다.  
<img src="https://user-images.githubusercontent.com/74566094/133253834-fa26cba6-3fc8-4cc6-a7f7-7f93367d7f79.jpg" width = "50%|height = 30"/> 

내가 작성한 글을 확인할 수 있는 공간입니다. 리사이클러뷰와 DB를 통해 구현되었으며 선택 시 포지션값에 따라 해당 글로 이동합니다.  
<img src="https://user-images.githubusercontent.com/74566094/133253718-b2d3b7f9-9ed0-4253-9162-22be6b0df3aa.jpg" width = "50%|height = 30"/> 

내가 선택한 글입니다. 해당 코드에서는 DB를 받아와 화면에 출력, 댓글 부분은 DB전송 및 리사이클러뷰로 구현하였습니다.
<img src="https://user-images.githubusercontent.com/74566094/133253766-2a2f9ea0-29c3-4147-acce-85ab32bb4e2a.jpg" width = "50%|height = 30"/> 
