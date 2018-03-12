package org.monarchinitiative.phenol.io.utils;

import java.util.List;

import org.geneontology.obographs.model.Meta;
import org.geneontology.obographs.model.meta.BasicPropertyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class with static methods for dumping the fields of Meta in Obographs
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class MetaViewer {
  private static final Logger LOGGER = LoggerFactory.getLogger(MetaViewer.class);

  public static void dump(Meta meta) {
    if (meta == null) return;
    LOGGER.info("-------------------------------------------------------------------------------");
    LOGGER.info("Definition: " + meta.getDefinition());
    List<BasicPropertyValue> bpvs = meta.getBasicPropertyValues();
    if (bpvs != null) {
      for (BasicPropertyValue bpv : bpvs) {
        LOGGER.info("Pred: " + bpv.getPred());
        LOGGER.info("Val: " + bpv.getVal());
        LOGGER.info("Xrefs: " + bpv.getXrefs());
        dump(bpv.getMeta());
      }
    }

    LOGGER.info("Version: " + meta.getVersion());
    LOGGER.info("Synonyms: " + meta.getSynonyms());
    LOGGER.info("Xrefs: " + meta.getXrefs());
    LOGGER.info("XrefValues: " + meta.getXrefsValues());
    LOGGER.info("Comments: " + meta.getComments());
    LOGGER.info("Subsets: " + meta.getSubsets());
    LOGGER.info("-------------------------------------------------------------------------------");
  }
}
