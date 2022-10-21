package com.neu.edu.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author arronshentu
 */
@Slf4j
public class ApplicationConfig {

  private static Map<String, Object> props = null;

  private static final Map<String, String> paramMap = new HashMap<>();

  static {
    try {
      reload();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private ApplicationConfig() {
  }

  public static Object getProperty(Map<?, ?> map, Object qualifiedKey) {
    if (map != null && !map.isEmpty() && qualifiedKey != null) {
      String input = String.valueOf(qualifiedKey);
      if (!"".equals(input)) {
        if (input.contains(".")) {
          int index = input.indexOf(".");
          String left = input.substring(0, index);
          String right = input.substring(index + 1);
          return getProperty((Map<?, ?>) map.get(left), right);
        } else {
          return map.getOrDefault(input, null);
        }
      }
    }
    return null;
  }

  public static String get(String name) {
    String value = paramMap.get(name);
    if (value == null) {
      Object o = getProperty(props, name);
      value = o == null ? "" : String.valueOf(o);
      paramMap.put(name, value != null ? value : "");
    }
    return value;
  }

  public static void set(String name, String newValue) {
    paramMap.put(name, newValue != null ? newValue : "");
  }

  public static void reload() throws Exception {
    PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
    InputStream is = resourceLoader.getResources("classpath*:/application.yml")[0].getInputStream();
    props = new Yaml().load(is);
    loadFromEnv();
  }

  private static void loadFromEnv() {
    Map<String, String> envs = System.getenv();
    envs.forEach((k, v) -> {
      String formatted = keyFormat(k);
      String s = ApplicationConfig.get(formatted);
      if (!"".equals(s)) {
        ApplicationConfig.set(formatted, v);
      }
    });
  }

  public static String getEndpoint(String pathName) {
    return get("server.endpoint") + get(pathName);
  }

  private static String keyFormat(String k) {
    k = k.toLowerCase(Locale.US);
    k = k.replaceAll("_", ".");
    return k;
  }
}
