package com.cb.workflow.persistence.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.cb.workflow.persistence.mapper")
public class MyBatisConfig {}