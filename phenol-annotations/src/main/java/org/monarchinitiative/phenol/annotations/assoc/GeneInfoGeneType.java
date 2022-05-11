package org.monarchinitiative.phenol.annotations.assoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.Set;

/**
 * Gene types corresponding to values found in the 10th column of the Homo sapiens gene info file that is parsed by
 * {@link HumanGeneInfoLoader}.
 */
public enum GeneInfoGeneType {

  protein_coding,
  pseudo,
  biological_region,
  snoRNA,
  snRNA,
  scRNA,
  rRNA,
  tRNA,
  ncRNA,
  other,
  unknown;

  private static final Logger LOGGER = LoggerFactory.getLogger(GeneInfoGeneType.class);

  public static final Set<GeneInfoGeneType> ALL = EnumSet.allOf(GeneInfoGeneType.class);

  /**
   * The default {@link GeneInfoGeneType}s include {@link #protein_coding}, {@link #ncRNA}, and {@link #tRNA}.
   */
  public static final Set<GeneInfoGeneType> DEFAULT = EnumSet.of(protein_coding, ncRNA, tRNA);

  public static GeneInfoGeneType parse(String value) {
    switch (value.toLowerCase()) {
      case "protein-coding":
        return protein_coding;
      case "ncrna":
        return ncRNA;
      case "pseudo":
        return pseudo;
      case "biological-region":
        return biological_region;
      case "unknown":
        return unknown;
      case "snorna":
        return snoRNA;
      case "other":
        return other;
      case "rrna":
        return rRNA;
      case "trna":
        return tRNA;
      case "snrna":
        return snRNA;
      case "scrna":
        return scRNA;
      default:
        LOGGER.warn("Unknown gene info gene type '{}', falling back to 'unknown'", value);
        return unknown;
    }

  }

}
