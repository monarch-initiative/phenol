package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.ontology.data.Ontology;

public class HpoDiseaseLoaders {

  private HpoDiseaseLoaders() {
  }

  /**
   * @param hpo HPO ontology.
   * @param options loader options. <b>Note</b>: the default options are at {@link HpoDiseaseLoaderOptions#defaultOptions()}).
   * @return default disease loader.
   */
  public static HpoDiseaseLoader defaultLoader(Ontology hpo, HpoDiseaseLoaderOptions options) {
    return new HpoDiseaseLoaderDefault(hpo, options);
  }

  /**
   * @param hpo HPO ontology.
   * @param options loader options. <b>Note</b>: the default options are at {@link HpoDiseaseLoaderOptions#defaultOptions()}.
   * @return aggregated disease loader.
   */
  public static HpoDiseaseLoader aggregated(Ontology hpo, HpoDiseaseLoaderOptions options) {
    return new AggregatedHpoDiseaseLoader(hpo, options);
  }
}
