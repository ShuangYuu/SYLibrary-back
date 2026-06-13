package org.springboot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "book-import.google-books")
public class GoogleBooksProperties {
    private String apiKey;
    private Proxy proxy = new Proxy();

    @Data
    public static class Proxy {
        private boolean enabled = false;
        private String host = "127.0.0.1";
        private int port = 7890;
    }
}
