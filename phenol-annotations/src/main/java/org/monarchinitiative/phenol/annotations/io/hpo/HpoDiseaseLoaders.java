package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.ontology.data.Ontology;

public class HpoDiseaseLoaders {

  private HpoDiseaseLoaders() {
  }

  public static HpoDiseaseLoader defaultLoader(Ontology hpo, HpoDiseaseLoaderOptions options) {
    return new HpoDiseaseLoaderDefault(hpo, options);
  }

  public static HpoDiseaseLoader aggregated(Ontology hpo, HpoDiseaseLoaderOptions options) {
    return new AggregatedHpoDiseaseLoader(hpo, options);
  }
}
