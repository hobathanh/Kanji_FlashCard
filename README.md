# Kanji_FlashCard
android course project

## Kanji_FlashCard là 1 app di động được xây dựng và phát triển bởi team !Loser. Đây là một dự án kết thúc học phần môn học lập trình di động.
## Từ nhu cầu thực tế của học sinh học tiếng Nhật chúng mình, cần thiết có một ứng dụng điện thoại để học tiếng nhật một các chủ động và hiệu quả, đặc biết là với phần ghi nhớ Kanji- một phần khá khó đối với học sinh sinh viên. Theo đó, Kanji_FlashCard được xây dựng nhằm mục đích giúp người học có một nền tảng học tốt và hiệu quả. 
## Ở Kanji_FlashCard, người học sẽ toàn quyền chọn cho mình những từ vựng cần học. Các từ vựng sẽ được chia thành cách thẻ riêng biệt (Category). Trong Các bộ thẻ này, người dùng sẽ thêm cho mình những từ cần học hoặc chưa ghi nhớ 
## Chức năng đặc biệt của app này đó là có thể ôn tập và làm trắc nghiệm theo từng chủ đề cần học. Cụ thể, hãy tự mình tạo ra các bộ thẻ nhớ cần học và làm trắc nghiệm. Các bạn sẽ thấy được số từ mình trắc nghiệm đúng và những từ mình làm chưa đúng.
## Ngoài ra, mỗi người dùng khác nhau sẽ có những bộ dữ liệu khác nhau được lưu trữ trên FireBase. Để truy cập được, người dùng sẽ cần xác nhận danh tính và đăng nhập. Có thể lựa chọn đăng nhập bằng tài khoản đăng ký hoặc đăng nhập bằng tài khoản google
## Do thời gian thực hiện còn ít nên dự án vẫn còn nhiều thiếu sót. Trong tương lai, chúng mình sẽ tiếp tục hoàn thiện và mở rộng dự án, thêm các chức năng như learning together, ...

## HowToRunApp 
### Clone Project -> Open Android Studio -> Open Project -> Choose Device and Run 
### Bug&Fix 
- If text to speech not working on your device, go to setting > Language > Text-to-speech > install voice data > install japanese, then reopen FlashCard App If signin with Google not work, do this: Select Gradle in android studio from right panel Select Your App In Tasks -> android-> signingReport Double click signingReport. You will find the SHA-1 fingerprint in the "Gradle Console" Add this SHA-1 fingerprint in firebase console MacOS just paste in the Terminal: keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore -storepass android -keypass android
- If text to speech not working on your device, go to setting > Language > Text-to-speech > install voice data > install japanese, then reopen flashCard app
