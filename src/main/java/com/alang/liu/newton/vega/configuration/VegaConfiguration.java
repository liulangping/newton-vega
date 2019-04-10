package com.alang.liu.newton.vega.configuration;


import com.alang.liu.newton.vega.core.VegaEngine;
import com.alang.liu.newton.vega.utils.SpringBeanUtil;
import org.springframework.context.annotation.Bean;


public class VegaConfiguration {
    @Bean
    SpringBeanUtil applicationContextUtils() {
        return new SpringBeanUtil();
    }

    @Bean
    VegaEngine VegaEngine() {
        return new VegaEngine();
    }

}
