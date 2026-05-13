---
trigger: always_on
---

# BFMS — Coding Conventions

## Quy ước duy nhất · Tất cả agent phải tuân thủ chính xác

> Đây là "luật". Không được sáng tạo, không được phá vỡ, không được bổ sung mà không có sự đồng ý của owner.

---

## 1. Layered Architecture — Quy tắc phân tầng

```
Controller → Service (Interface) → ServiceImpl → Repository → Entity
```

| Tầng                    | Trách nhiệm                                                  | KHÔNG được làm                                     |
| ----------------------- | ------------------------------------------------------------ | -------------------------------------------------- |
| **Controller**          | Nhận request, validate DTO cơ bản, gọi Service, trả Response | Chứa business logic, truy cập Repository trực tiếp |
| **Service (Interface)** | Định nghĩa contract của business logic                       | Chứa implementation                                |
| **ServiceImpl**         | Toàn bộ business logic, tính toán, transaction               | Truy cập DB trực tiếp ngoài Repository             |
| **Repository**          | Kế thừa `JpaRepository`, chỉ query methods                   | Chứa logic                                         |
| **Entity**              | Ánh xạ DB table, annotation JPA                              | Chứa business logic                                |
| **DTO (req/res)**       | Truyền dữ liệu qua API, dùng `record`                        | Có annotation JPA                                  |

## Cấu trúc này phải tuân thủ chặt chẽ, không được phép tạo thừa. Đối với dự án này, các package/class... sẽ nằm trong **src/main/java/com/bfms/bfms_backend**

## 2. Đặt tên (Naming)

### Package

```
controller/          → *Controller.java
service/             → *Service.java (interface)
service/impl/        → *ServiceImpl.java
repository/          → *Repository.java
entity/              → tên entity (Bus, Route, Node...)
dtos/req/            → *Request.java
dtos/res/            → *Response.java
config/              → *Config.java
security/            → JwtUtil, JwtFilter, AuthService, UserDetailsServiceImpl
```

### Class

- Entity: `PascalCase`, đơn số — `Bus`, `Route`, `BusShift`, `AdContract`
- Controller: `PascalCase` + `Controller` — `BusController`, `RouteController`
- Service interface: `PascalCase` + `Service` — `BusService`, `RouteService`
- Service impl: `PascalCase` + `ServiceImpl` — `BusServiceImpl`
- DTO: `PascalCase` + `Request`/`Response` — `BusRequest`, `BusResponse`
- Enum: `PascalCase` — `BusStatus`, `Role`

### Method

- `camelCase` rõ nghĩa, động từ đứng đầu
- `getAllBuses()`, `createBus()`, `updateBus()`, `deleteBus()`, `sellBus()`
- Service method names khớp với Controller action

### Field (Entity)

- Java field: `camelCase` — `routeNumber`, `stopA`, `licensePlate`
- DB column: `snake_case` — `route_number`, `stop_a`, `license_plate`
- Luôn explicit `@Column(name = "...")` nếu tên không tự map được

---

## 3. Entity Rules

```java
@Entity
@Table(name = "ten_bang_lowercase")  // tên bảng LUÔN lowercase để an toàn với Postgres
@Getter
@Setter
@NoArgsConstructor                   // Lombok — luôn có no-args constructor cho JPA
public class TenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;              // dùng Integer (không Long) nếu SERIAL trong DB

    @Column(name = "ten_cot", nullable = false)
    private String tenField;

    // FK → @ManyToOne với FetchType.LAZY là default
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id")
    private EntityKhac entityKhac;

    // Enum → lưu dạng STRING trong DB
    @Enumerated(EnumType.STRING)
    @Column(name = "ten_cot")
    private TenEnum tenEnum;
}
```

**Quy tắc bắt buộc:**

- Dùng Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor` — KHÔNG dùng `@Data` trên entity (tránh vòng lặp equals/hashCode với lazy loading)
- `FetchType.LAZY` cho tất cả quan hệ `@ManyToOne`, `@OneToMany`
- Không để business logic trong Entity
- `AppUser` phải implement `UserDetails` — fix `getPassword()` và `getUsername()` trả đúng field, không hardcode empty string

---

## 4. DTO Rules

```java
// Dùng Java record — immutable, ngắn gọn
package dtos.req;
public record BusRequest(
        Integer routeId,
        String busModel,
        // ...
) {}

package dtos.res;
public record BusResponse(
        Integer id,
        Integer routeId,
        String routeNumber,  // flattened từ Route entity
        // ...
) {}
```

- **Request DTO:** chỉ chứa field nhận từ client, không có `id`
- **Response DTO:** chứa những gì trả về client, có thể flatten nested entity
- Không dùng Entity class trực tiếp làm request/response body

---

## 5. Service Rules

```java
@Service
public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;

    public BusServiceImpl(BusRepository busRepository) {  // Constructor injection — không dùng @Autowired
        this.busRepository = busRepository;
    }

    @Override
    @Transactional  // Chỉ đặt ở method làm thay đổi dữ liệu (create, update, delete)
    public BusResponse createBus(BusRequest request) {
        // 1. Validate
        // 2. Map request → entity
        // 3. Save
        // 4. Map entity → response
        // 5. Return
    }
}
```

**Quy tắc `@Transactional`:**

- Luôn đặt ở method ghi (create/update/delete)
- Các method đặc biệt như US-03 (complete shift) cần `@Transactional` bao toàn bộ: update BUS_SHIFT + DAILY_TICKET_STAT + NODE.total_passengers
- Không đặt `@Transactional` ở Controller

**Exception handling:**

- **Quy tắc bắt buộc:** Tất cả các class khi muốn trả lỗi hoặc ngoại lệ, PHẢI sử dụng `com.bfms.bfms_backend.exception.AppException` và `com.bfms.bfms_backend.exception.ErrorCode`.
- Tuyệt đối không throw `RuntimeException` chung chung hoặc các Exception tự định nghĩa khác ngoài `AppException`.
- Message lỗi phải đi kèm với `ErrorCode` tương ứng để frontend có thể xử lý tập trung.

---

## 6. Controller Rules

```java
@RestController
@RequestMapping("/api/v1/buses")
public class BusController {

    private final BusService busService;   // Inject INTERFACE, không phải Impl

    public BusController(BusService busService) {
        this.busService = busService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")      // RBAC theo role đã định nghĩa
    public ResponseEntity<List<BusResponse>> getAll() {
        return ResponseEntity.ok(busService.getAllBuses());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BusResponse> create(@RequestBody BusRequest request) {
        return ResponseEntity.ok(busService.createBus(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        busService.deleteBus(id);
        return ResponseEntity.noContent().build();
    }
}
```

**Quy tắc role trong `@PreAuthorize`:**

- Dùng `hasRole('ROLE_NAME')` — Spring Security tự thêm prefix `ROLE_`
- Nhiều role: `hasAnyRole('ADMIN', 'OWNER')`
- Mapping đúng với bảng US trong `PROJECT_CONTEXT.md`

---

## 7. Repository Rules

```java
@Repository
public interface BusRepository extends JpaRepository<Bus, Integer> {
    // Chỉ khai báo query methods theo convention Spring Data
    Optional<Bus> findByLicensePlate(String licensePlate);
    boolean existsByRouteId(Integer routeId);
}
```

- Không viết `@Query` trừ khi query phức tạp không thể dùng method naming
- Luôn dùng `Optional<T>` khi tìm theo field không phải PK
- Tên method tuân thủ Spring Data JPA convention

---

## 8. Security & JWT

- Endpoint public duy nhất: `/api/v1/auth/**`
- Tất cả endpoint còn lại: require JWT
- Token lifetime: 1 ngày (86400000ms), Refresh token: 7 ngày
- JWT secret: đọc từ `application.yaml` — không hardcode
- `UserDetails.getUsername()` phải return `this.username` (field thực)
- `UserDetails.getPassword()` phải return `this.password` (field thực)

---

## 9. Database & Flyway

- Migration files: `src/main/resources/db/migration/`
- Naming: `V{số}__{mô_tả}.sql` — ví dụ: `V2__Add_node_entity.sql`
- Schema hiện tại đã có trong `V1__Initial_Setup.sql` — KHÔNG sửa file này
- Nếu cần thay đổi schema: tạo file migration mới `V2__...sql`
- JPA ddl-auto: `validate` — Hibernate chỉ validate, không tự tạo/sửa bảng

---

## 10. Comments & Documentation

```java
// Tiếng Việt cho comment giải thích logic nghiệp vụ
// Tiếng Anh cho comment kỹ thuật (annotation explanation)

// ✅ Đúng:
// Kiểm tra xe có đang trong ca chạy chưa hoàn thành không
if (busShiftRepository.existsByBusIdAndStatusNot(id, "COMPLETED")) { ... }

// ❌ Sai:
// check bus shift
```

- Comment giải thích **tại sao**, không giải thích **cái gì** (code tự nói cái gì)
- Không comment code đã bị xóa — dùng Git thay thế
- Ngoại lệ: code commented-out với TODO rõ ràng được phép tạm thời

---

## 11. Clean Code Checklist (trước khi submit)

- [ ] Không có magic number (dùng constant hoặc enum)
- [ ] Tên biến/method/class rõ nghĩa, không abbreviate
- [ ] Mỗi method làm đúng 1 việc
- [ ] Không trùng lặp logic — extract helper method nếu dùng ≥ 2 lần
- [ ] Không `System.out.println` — dùng `@Slf4j` nếu cần log
- [ ] Không hardcode giá trị cấu hình — đọc từ `application.yaml`
- [ ] Exception message có đủ context để debug
- [ ] `@Transactional` đặt đúng chỗ
- [ ] DTO tách biệt hoàn toàn khỏi Entity
