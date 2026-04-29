package com.bfms.bfms_backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "application.security")
@Getter
@Setter
public class SecurityProperties {

    private JwtProperties jwt = new JwtProperties();
    private List<String> permitAllPatterns;
    private CorsProperties cors = new CorsProperties();

    @Getter
    @Setter
    public static class JwtProperties {
        private String secretKey;
        private long expiration;
        private RefreshTokenProperties refreshToken = new RefreshTokenProperties();

        @Getter
        @Setter
        public static class RefreshTokenProperties {
            private long expiration;
        }
    }

    @Getter
    @Setter
    public static class CorsProperties {
        private List<String> allowedOrigins;
        private List<String> allowedMethods;
        private List<String> allowedHeaders;
        private boolean allowCredentials;
    }
}
