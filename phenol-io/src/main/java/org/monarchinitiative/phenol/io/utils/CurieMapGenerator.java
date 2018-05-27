package org.monarchinitiative.phenol.io.utils;

import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

/**
 * Helper class with static methods for initializing CurieUtil. The static method reads
 * curie_map.yaml and put the k-v entries into curieMap, which will be used for initialization of
 * CurieUtil. The original curie_map.yaml is available at Dipper's Github:
 * https://github.com/monarch-initiative/dipper/blob/master/dipper/curie_map.yaml.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class CurieMapGenerator {
  private static final Logger LOGGER = LoggerFactory.getLogger(CurieMapGenerator.class);

  @SuppressWarnings("unchecked")
  public static Map<String, String> generate() {
    try {
      InputStream inputStream = CurieMapGenerator.class.getClassLoader().getResourceAsStream("curie_map.yaml");
      Yaml yaml = new Yaml();
      Map<String, String> curieMap = yaml.load(inputStream);
      return curieMap;
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
    return null;
  }
}
