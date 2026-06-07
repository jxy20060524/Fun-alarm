package com.funalarm.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class DatabaseConfig {

    private static HikariDataSource dataSource;

    private DatabaseConfig() {
    }

    public static void init() {
        if (dataSource != null) {
            return;
        }
        Properties props = loadProperties();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.username"));
        config.setPassword(props.getProperty("db.password"));
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        dataSource = new HikariDataSource(config);
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        Path configPath = Path.of("config.properties");
        try {
            if (Files.exists(configPath)) {
                try (InputStream in = Files.newInputStream(configPath)) {
                    props.load(in);
                    return props;
                }
            }
        } catch (IOException ignored) {
        }
        try (InputStream in = DatabaseConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new RuntimeException("无法加载 config.properties", e);
        }
        if (props.isEmpty()) {
            throw new RuntimeException("未找到 config.properties，请配置数据库连接");
        }
        return props;
    }

    public static HikariDataSource getDataSource() {
        if (dataSource == null) {
            init();
        }
        return dataSource;
    }

    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }
}
