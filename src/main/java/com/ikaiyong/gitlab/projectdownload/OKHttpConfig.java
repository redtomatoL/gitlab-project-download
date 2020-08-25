package com.ikaiyong.gitlab.projectdownload;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author ljm
 * @description TODO
 * @className OKHttpConfig
 * @date 2020/7/15 16:41
 */

@Configuration
public class OKHttpConfig {
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient().newBuilder().retryOnConnectionFailure(true).connectionPool(pool())
                .connectTimeout(5, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).writeTimeout(5, TimeUnit.SECONDS)
                .build();

    }

    @Bean
    public ConnectionPool pool() {
        return new ConnectionPool(50, 5, TimeUnit.MINUTES);
    }
}