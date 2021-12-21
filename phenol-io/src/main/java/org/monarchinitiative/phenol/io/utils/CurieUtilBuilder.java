package org.monarchinitiative.phenol.io.utils;

import org.prefixcommons.CurieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper class to help build a {@link CurieUtil} using the default mappings and / or just a subset.
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class CurieUtilBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(CurieUtilBuilder.class);


  private static final Map<String, String> DEFAULT_CURIE_MAP = Map.copyOf(generate());
  private static final CurieUtil DEFAULT_CURIE_UTIL = new CurieUtil(DEFAULT_CURIE_MAP);


  private CurieUtilBuilder() {
  }

  public static Map<String, String> defaultCurieMap() {
    return DEFAULT_CURIE_MAP;
  }

  public static CurieUtil defaultCurieUtil() {
    return DEFAULT_CURIE_UTIL;
  }

  public static CurieUtil withDefaultsAnd(Map<String, String> additionalCuries) {
    Map<String, String> merged = new HashMap<>();
    merged.putAll(DEFAULT_CURIE_MAP);
    merged.putAll(additionalCuries);
    return new CurieUtil(Map.copyOf(merged));
  }

  public static CurieUtil just(Map<String, String> curies) {
    return new CurieUtil(Map.copyOf(curies));
  }

  /**
   * Reads curie_map.yaml and put the k-v entries into curieMap, which will be used for initialization of
   * CurieUtil. The original curie_map.yaml is available at Dipper's Github:
   * https://github.com/monarch-initiative/dipper/blob/master/dipper/curie_map.yaml.
   */
  private static Map<String, String> generate() {
    try {
      InputStream inputStream = CurieUtilBuilder.class.getClassLoader().getResourceAsStream("curie_map.yaml");
      Yaml yaml = new Yaml();
      return yaml.load(inputStream);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    return Map.of();
  }
}
