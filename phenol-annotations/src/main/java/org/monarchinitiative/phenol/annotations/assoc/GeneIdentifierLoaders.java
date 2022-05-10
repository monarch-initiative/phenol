package org.monarchinitiative.phenol.annotations.assoc;

import java.util.Set;

/**
 * Loaders for getting {@link org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers} from different
 * file formats.
 */
public class GeneIdentifierLoaders {

  private GeneIdentifierLoaders() {
  }

  /**
   * Get loader for loading Homo sapiens gene info file.
   *
   * @param geneTypes set of gene types to retain.
   */
  public static GeneIdentifierLoader forHumanGeneInfo(Set<GeneInfoGeneType> geneTypes) {
    return HumanGeneInfoLoader.of(geneTypes);
  }

  /**
   * Get loader for loading HGNC complete set archive file.
   */
  public static GeneIdentifierLoader forHgncCompleteSetArchive() {
    return HGNCGeneIdentifierLoader.instance();
  }

}
