package com.example.compatibilityservice.exception;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@Configuration
public class Config implements InitializingBean {

    @Autowired
    private RequestMappingHandlerAdapter converter;

    @Override
    public void afterPropertiesSet() throws Exception {
        configureJacksonToFailOnUnknownProperties();
    }

    //Changes Spring Boot's default exception handling on invalid JSON payloads
    private void configureJacksonToFailOnUnknownProperties() {
        MappingJackson2HttpMessageConverter httpMessageConverter = converter.getMessageConverters().stream()
                .filter(mc -> mc.getClass()
                        .equals(MappingJackson2HttpMessageConverter.class))
                .map(mc -> (MappingJackson2HttpMessageConverter) mc)
                .findFirst()
                .get();

        httpMessageConverter.getObjectMapper().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
