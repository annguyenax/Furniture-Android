package com.furniture.api.config;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Hibernate6Module hibernate6Module() {
        // By default, uninitialized lazy proxies are serialized as null (no LazyInitializationException)
        return new Hibernate6Module();
    }
}
