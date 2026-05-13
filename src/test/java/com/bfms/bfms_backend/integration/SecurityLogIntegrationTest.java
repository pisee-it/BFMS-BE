package com.bfms.bfms_backend.integration;

import com.bfms.bfms_backend.entity.AppUser;
import com.bfms.bfms_backend.entity.Role;
import com.bfms.bfms_backend.entity.SecurityLog;
import com.bfms.bfms_backend.repository.AppUserRepository;
import com.bfms.bfms_backend.repository.SecurityLogRepository;
import com.bfms.bfms_backend.security.JwtUtil;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityLogIntegrationTest {

    static {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private SecurityLogRepository securityLogRepository;

    private Integer adminId;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Setup Admin User
        String username = "admin_" + UUID.randomUUID().toString().substring(0, 8);
        AppUser admin = new AppUser();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        admin.setFullName("Security Admin");
        admin = userRepository.save(admin);
        adminId = admin.getId();

        jwtToken = jwtUtil.generateToken(username);

        // Seed some logs
        SecurityLog log1 = new SecurityLog();
        log1.setUsername(username);
        log1.setAction("TEST_ACTION");
        log1.setDescription("Test description");
        securityLogRepository.save(log1);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        securityLogRepository.deleteAll();
        if (adminId != null) userRepository.deleteById(adminId);
    }

    @Test
    void testGetSecurityLogsSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/security-logs")
                .header("Authorization", "Bearer " + jwtToken)
                .param("action", "TEST_ACTION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].action").value("TEST_ACTION"));
    }

    @Test
    void testGetSecurityLogs_Unauthorized_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/security-logs"))
                .andExpect(status().isForbidden());
    }
}
