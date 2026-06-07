# Vocabulary Learning App

Ứng dụng học từ vựng tiếng Anh trên Android giúp người dùng quản lý chủ đề từ vựng, học bằng flashcard, làm bài kiểm tra và theo dõi lịch sử học tập.

## Thành viên thực hiện

- Tuan(frontEnd)
- Vu(backEnd)
- Trieu(BA)
---

## Mục tiêu dự án

Xây dựng ứng dụng học từ vựng hỗ trợ:

- Quản lý các chủ đề từ vựng
- Học từ vựng bằng Flashcard
- Làm bài Quiz kiểm tra kiến thức
- Theo dõi lịch sử làm bài
- Đồng bộ dữ liệu thông qua REST API

---

## Chức năng chính

### Authentication

- Đăng nhập
- Đăng ký tài khoản
- Xác thực người dùng thông qua API

### Topic Management

- Xem danh sách chủ đề
- Tạo chủ đề mới
- Cập nhật chủ đề
- Xóa chủ đề
- Lấy chủ đề theo User ID

### Vocabulary Management

- Thêm từ vựng
- Chỉnh sửa từ vựng
- Xóa từ vựng
- Hiển thị ví dụ minh họa

### Flashcard Learning

- Học từ vựng bằng Flashcard
- Lật thẻ để xem nghĩa
- Duyệt danh sách từ trong chủ đề

### Quiz System

- Tạo bài kiểm tra từ chủ đề
- Chọn đáp án
- Chấm điểm tự động
- Hiển thị kết quả

### History Quiz

- Lưu lịch sử làm bài
- Xem kết quả các lần kiểm tra trước

---

## Công nghệ sử dụng

### Frontend

- Android Studio
- Kotlin
- XML
- Material Design

### Backend

- Spring Boot REST API

### Database

- PostgreSQL

### Công cụ hỗ trợ

- Git
- GitHub
- Postman
- Docker

---

## Kiến trúc hệ thống

```text
Android App
      |
      v
 Spring Boot API
      |
      v
 PostgreSQL
```

---

## Cấu trúc dự án

```text
app/
├── activities
├── fragments
├── adapters
├── models
├── repositories
├── services
├── viewmodels
└── utils
```

---

## Cài đặt

### Clone project

```bash
git clone https://github.com/tuanvu2004/androidProject.git
```

### Mở bằng Android Studio

```bash
File -> Open -> androidProject
```

### Build dự án

```bash
./gradlew build
```

Hoặc trên Windows:

```bash
gradlew.bat build
```

---

## API

Ứng dụng sử dụng REST API để giao tiếp với backend.

Ví dụ:

```http
GET /api/v1/topics
```

```http
GET /api/v1/topics/{id}
```

```http
POST /api/v1/topics
```

```http
DELETE /api/v1/topics/{id}
```

---

## Kết quả đạt được

- Quản lý chủ đề từ vựng
- Quản lý từ vựng
- Học bằng Flashcard
- Làm Quiz
- Theo dõi lịch sử học tập
- Kết nối API thành công

---

## Hướng phát triển

- Đồng bộ dữ liệu thời gian thực
- Thêm AI hỗ trợ học từ vựng
- Thống kê tiến độ học tập
- Hỗ trợ nhiều ngôn ngữ
- Chế độ học ngoại tuyến

---

## License

This project is developed for educational purposes.
