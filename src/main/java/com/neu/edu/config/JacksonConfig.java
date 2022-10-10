package com.neu.edu.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * @author arronshentu
 */
@Configuration
public class JacksonConfig {

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer customJackson() {
    return jacksonObjectMapperBuilder -> {
//      jacksonObjectMapperBuilder.simpleDateFormat("yyyy-MM-dd hh:mm");
      jacksonObjectMapperBuilder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .serializers(
          new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
          new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        .deserializers(
          new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
          new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        .modules(
          new JavaTimeModule(),
          new ParameterNamesModule(),
          new Jdk8Module());
      jacksonObjectMapperBuilder.timeZone(TimeZone.getTimeZone("US/Eastern"));
      jacksonObjectMapperBuilder.serializationInclusion(JsonInclude.Include.NON_NULL);
      jacksonObjectMapperBuilder.failOnUnknownProperties(false);
      jacksonObjectMapperBuilder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    };
  }

}
