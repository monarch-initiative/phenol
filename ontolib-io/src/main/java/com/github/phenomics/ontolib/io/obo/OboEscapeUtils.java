package com.github.phenomics.ontolib.io.obo;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility code for escaping and unescaping strings in OBO.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class OboEscapeUtils {

  /** Escape characters mapped to their unescaped ones. */
  private static final ImmutableMap<String, String> MAPPING;

  /** Pattern to use for replacement. */
  private static final Pattern PATTERN;

  static {
    ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
    builder.put("\\n", "\n");
    builder.put("\\W", " ");
    builder.put("\\t", "\t");
    builder.put("\\:", ":");
    builder.put("\\,", ",");
    builder.put("\\\"", "\"");
    builder.put("\\\\", "\\");
    builder.put("\\(", "(");
    builder.put("\\)", ")");
    builder.put("\\[", "[");
    builder.put("\\]", "]");
    builder.put("\\{", "{");
    builder.put("\\}", "}");
    builder.put("\\\n", "\n");
    MAPPING = builder.build();

    PATTERN = Pattern.compile("("
        + Joiner.on('|')
            .join(MAPPING.keySet().stream().map(s -> Pattern.quote(s)).collect(Collectors.toList()))
        + ")");
  }

  /**
   * Trim leading and trailing quotes if necessary and unescape characters.
   *
   * @param s String to perform unescaping on.
   * @return Unescaped string.
   */
  public static String unescape(String s) {
    if (s == null) {
      return s;
    } else if (!s.startsWith("\"") || !s.endsWith("\"")) {
      return unescapeImpl(s);
    } else {
      return unescapeImpl(s.substring(1, s.length() - 1));
    }
  }

  private static String unescapeImpl(String s) {
    final Matcher matcher = PATTERN.matcher(s);

    final StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(sb, MAPPING.get(matcher.group(1)));
    }
    matcher.appendTail(sb);

    return sb.toString();
  }

}
