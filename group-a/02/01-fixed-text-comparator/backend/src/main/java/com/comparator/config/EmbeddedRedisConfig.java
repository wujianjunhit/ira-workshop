package com.comparator.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import java.io.IOException;

@Configuration
@Profile("!prod")  // 在非生产环境启用
public class EmbeddedRedisConfig {

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {
        // 启动嵌入式 Redis，使用 6379 端口
        redisServer = new RedisServer(6379);
        redisServer.start();
        System.out.println("✅ Embedded Redis started on port 6379");
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
            System.out.println("✅ Embedded Redis stopped");
        }
    }
}
