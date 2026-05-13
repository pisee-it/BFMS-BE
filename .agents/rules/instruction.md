---
trigger: always_on
---

# BFMS Agent Instructions

3: ## ⚠️ TUYỆT ĐỐI TUÂN THỦ FILE NÀY TRƯỚC KHI LÀM BẤT CỨ ĐIỀU GÌ
4:
5: Bạn là AI Agent làm việc trên dự án **BFMS (Bus Finance Management System)**.  
6: Đây là tài liệu điều phối trung tâm. Bạn PHẢI tuyệt đối tuân thủ các quy tắc trong file này. Đọc đủ, đọc hết, không bỏ qua bước nào.
7:

---

## 1. Thứ tự đọc tài liệu bắt buộc

Mỗi khi bắt đầu session mới, đọc **theo đúng thứ tự** sau:

```
1. agent-context/AGENT_INSTRUCTIONS.md         ← File này (bạn đang đọc)
2. agent-context/PROJECT_CONTEXT.md            ← Bối cảnh dự án, stack, kiến trúc tổng hợp
3. agent-context/documents_md/BFMS_API.md      ← Tài liệu API
4. agent-context/documents_md/BFMS_SDD.md      ← Tài liệu SDD (System Design Document)
5. agent-context/documents_md/BFMS_SRS.md      ← Tài liệu SRS (Software Requirement Specification)
6. agent-context/CODING_CONVENTIONS.md         ← Quy ước code duy nhất, bắt buộc tuân thủ
7. agent-context/PROJECT_MEMORY.md      ← Trí nhớ tích lũy từ các session trước
8. agent-context/angular-frontend/libs.md    ← Danh sách thư viện Frontend
9. agent-context/spring-backend/libs.md       ← Danh sách thư viện Backend
10. agent-context/jira-current-task.md        ← Task hiện tại cần thực hiện (nếu tồn tại)
```

> **Lưu ý**: Mỗi khi thêm thư viện mới vào dự án, bạn PHẢI cập nhật ngay vào file `libs.md` tương ứng kèm theo lệnh cài đặt/cấu hình.

> Các tài liệu SRS / SDD / API đã được tóm tắt đầy đủ trong `PROJECT_CONTEXT.md`.  
> Nếu chưa thực sự nắm rõ, cần phải đọc thêm các file `document_md/BFMS_API.md`, `document_md/BFMS_SDD.md`, `document_md/BFMS_SRS.md` mà không cần hỏi lại tôi.

---

## 2. Quy trình bắt buộc trước khi code

### Bước 0 — Tuân thủ quy tắc hệ thống

- Đối với các task **Frontend (Angular)**, ngoài các tài liệu trong `agent-context`, bạn PHẢI tuân thủ tuyệt đối các quy tắc trong file `.gemini` (bao gồm `RULE[AGENTS.md]`) về TypeScript, Angular, Accessibility và State Management.

### Bước 1 — Phân tích task từ `jira-current-task.md`

Khi file `jira-current-task.md` tồn tại:

1. Đọc kỹ task: tên, priority, due date, parent epic, description.
2. **Phân tích và phản biện** — đặt câu hỏi nếu có bất kỳ điểm nào chưa rõ:
   - Scope của task là gì? Có phụ thuộc vào task/entity khác không?
   - Acceptance criteria có đủ không? Cần thêm validation gì?
   - Có risk gì về transaction, FK constraint, hay security không?
     49: 3. **Trình bày kế hoạch thực thi (Implementation Plan)** ngắn gọn (bullet points, không code):
     50: - Kế hoạch PHẢI dựa trên task và danh sách **Subtask** cụ thể trong file `jira-current-task.md`.
     51: - Chia kế hoạch tương ứng chính xác với từng Subtask.
     52: - Mỗi giai đoạn phải đảm bảo tính độc lập để có thể **commit riêng biệt** (Atomic Commits).
     53: - Xác định rõ file nào sẽ tạo mới/sửa đổi trong từng giai đoạn.
     54: - Cuối bản kế hoạch, PHẢI dự thảo sẵn **Pull Request Title & Description** để sử dụng khi hoàn thành toàn bộ task.
     55: 4. **Lưu trữ Implementation Plan**: BẮT BUỘC lưu toàn bộ kế hoạch thực thi vào file Markdown tại: `agent-context/angular-frontend/implementation_plans/[Mã task].md`.
     56: 5. **Dừng lại và chờ xác nhận** từ người dùng trước khi viết bất kỳ dòng code nào.

> ❌ NGHIÊM CẤM: Bắt đầu code khi chưa được xác nhận.  
> ❌ NGHIÊM CẤM: Tự suy diễn yêu cầu rồi implement mà không hỏi.
> ❌ NGHIÊM CẤM: Implement gộp nhiều subtask vào một lần commit nếu không được yêu cầu.

### Bước 2 — Implement đúng scope

Sau khi được xác nhận:

- Chỉ làm đúng những gì task yêu cầu.
- Không thêm feature ngoài scope (dù có vẻ "hay").
- Nếu phát hiện vấn đề mới trong khi code → dừng lại, thông báo, hỏi ý kiến.

### Bước 3 — Cập nhật memory & report theo từng giai đoạn

71: Sau khi hoàn thành **mỗi Subtask**:
72:
73: 1. **BẮT BUỘC Tạm dừng thực thi** và thông báo cho người dùng. Đợi người dùng kiểm tra và ra lệnh "Tiếp tục" hoặc xác nhận mới được làm subtask tiếp theo.
74: 2. Cập nhật `agent-context/PROJECT_MEMORY.md`: Ghi lại subtask vừa hoàn thành, các quyết định quan trọng. Tuân thủ quy tắc bóc tách theo subtask.
75: 3. Cập nhật (hoặc tạo nháp) file report tại `agent-context/angular-frontend/execution_reports/sprint-fe01/JiraID.md`: Bổ sung dần nội dung của subtask vừa làm vào mục tương ứng.
76: 4. Gợi ý lệnh **Git** rõ ràng cho subtask vừa hoàn thành:
77: - Lệnh `git add [các file liên quan]` (KHÔNG bao gồm thư mục `agent-context`).
78: - Lệnh `git commit -m "<type>(<scope>): [Mã subtask] <mô tả ngắn>"` (ví dụ: `feat(auth): [PRJBFMS-239] Khởi tạo cấu hình Signals`).
79: 5. Chờ chỉ thị tiếp theo từ người dùng trước khi thực hiện subtask kế tiếp.
80:
81: > ❌ NGHIÊM CẤM: Chạy liên tiếp nhiều subtask mà không dừng lại cập nhật, thông báo và chờ người dùng kiểm tra.

---

## 3. Những điều tuyệt đối không được làm

| ❌ Cấm                                                             | ✅ Thay bằng                             |
| ------------------------------------------------------------------ | ---------------------------------------- |
| Code trước khi được xác nhận                                       | Trình bày plan → chờ approve → code      |
| Thêm feature ngoài task scope                                      | Ghi note vào memory để làm task sau      |
| Tự đổi tên entity/field                                            | Hỏi và chờ confirm                       |
| Bỏ qua `CODING_CONVENTIONS.md`                                     | Đọc lại conventions trước khi code       |
| Xóa/sửa code hiện có không liên quan đến task                      | Chỉ touch đúng file liên quan            |
| Commit logic vào tầng sai (ví dụ: business logic trong Controller) | Đặt đúng layer theo Layered Architecture |
| Gộp nhiều subtask vào một commit                                   | Commit riêng biệt theo từng subtask      |

---

## 4. Khi không chắc chắn

Luôn hỏi. Câu hỏi ngắn gọn, rõ ràng, đúng điểm. Không đoán, không tự quyết, không "làm trước hỏi sau".
Nhiệt tình phản biện để nắm rõ được yêu cầu.

## 5. Sau khi hoàn thành task

Sau khi xác nhận đã hoàn tất **toàn bộ task** (bao gồm tất cả các subtask liên quan), bạn PHẢI tạo báo cáo tại `agent-context/execution_reports/sprint-fe01/JiraID.md` (ví dụ: `PRJBFMS-238.md`).

**Lưu ý quan trọng về Report:**

- Chỉ hoàn thiện và xác nhận report sau khi đã hoàn thành **TẤT CẢ** các subtask thuộc về task đó.
- Tuy nhiên, phải **cập nhật dần** nội dung report sau mỗi subtask hoàn thành (như quy định ở Bước 3).
- Một report đại diện cho một Task chính.
- Trong report, các thay đổi phải được liệt kê chi tiết theo từng **Subtask**.
- Quy tắc bóc tách theo subtask này cũng áp dụng tương tự khi cập nhật `PROJECT_MEMORY.md`.

```markdown
# Execution Report: [Jira ID] ([BFMS Key])

## [Tiêu đề task ngắn gọn]

### 1. Thông tin task

- **Jira ID:** [Ví dụ: PRJBFMS-36]
- **Mô tả:** [Mô tả ngắn gọn mục tiêu của task]
- **Ngày hoàn thành:** [Ngày hiện tại]

### 2. Các thay đổi chính (Chi tiết theo Subtask)

#### [Jira Subtask ID 1]: [Tên Subtask]

- **Thay đổi:**
  - `[Tên file]`: [Mô tả chi tiết...]
- **Refactoring (nếu có):**
  - [Mô tả...]

#### [Jira Subtask ID 2]: [Tên Subtask]

- **Thay đổi:**
  - `[Tên file]`: [Mô tả chi tiết...]

### 3. Kết quả kiểm chứng

- [Mô tả kết quả chạy test, biên dịch thành công, hoặc xác minh API]

### 4. Tài liệu bổ sung (nếu có)

- [Link đến Knowledge Item hoặc tài liệu liên quan]
```

## 6. Kết thúc Task và Pull Request

151: Khi đã hoàn tất toàn bộ task và đã đẩy code (push) lên server, bạn PHẢI:
152:
153: 1. Gợi ý **Title** cho Pull Request theo chuẩn: `feat/fix/chore...: [Mã Task] Tên task`.
154: 2. Viết **Description** tóm tắt các subtask đã hoàn thành và các lưu ý quan trọng.
155: 3. **Cập nhật thông tin PR này vào cuối file `implementation_plan.md` tương ứng** để lưu trữ.
156: 4. Gợi ý **Merge Commit Message** & **Extended Description** để sử dụng khi squash/merge PR.
