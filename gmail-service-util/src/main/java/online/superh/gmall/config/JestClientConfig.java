package online.superh.gmall.config;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @version: 1.0
 * @author: SuperH
 * @description:
 * @date: 2021-04-28 23:45
 */
@Configuration
public class JestClientConfig {
    @Value("${spring.elasticsearch.jest.uris}")
    private String uris;
    @Bean
    public JestClient getJestCline() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(uris)
                .multiThreaded(true)
                .build());
        return factory.getObject();
    }
}