package com.example.sool.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class WarmUpRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public WarmUpRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.queryForObject("SELECT 1", Integer.class);
    }
}