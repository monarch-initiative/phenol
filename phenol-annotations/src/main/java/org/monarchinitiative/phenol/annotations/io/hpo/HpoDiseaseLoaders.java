package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.Ontology;

public class HpoDiseaseLoaders {

  private HpoDiseaseLoaders() {
  }

  /**
   * Get default {@link HpoDiseaseLoader} implementation.
   *
   * @param hpo HPO ontology.
   * @param options loader options. <b>Note</b>: the default options are at {@link HpoDiseaseLoaderOptions#defaultOptions()}).
   * @return default disease loader.
   */
  public static HpoDiseaseLoader defaultLoader(MinimalOntology hpo, HpoDiseaseLoaderOptions options) {
    return new HpoDiseaseLoaderDefault(hpo, options);
  }

  /**
   * Get {@link HpoDiseaseLoader} that does not use deprecated APIs and is, therefore, faster than {@link #defaultLoader(MinimalOntology, HpoDiseaseLoaderOptions)}.
   *
   * @param hpo HPO ontology.
   * @param options loader options. <b>Note</b>: the default options are at {@link HpoDiseaseLoaderOptions#defaultOptions()}).
   * @return v2 disease loader.
   * @deprecated use {@link #defaultLoader(MinimalOntology, HpoDiseaseLoaderOptions)} instead.
   */
  @Deprecated(forRemoval = true, since = "2.0.0-RC5")
  public static HpoDiseaseLoader v2(Ontology hpo, HpoDiseaseLoaderOptions options) {
    return new HpoDiseaseLoaderDefault(hpo, options);
  }

}
