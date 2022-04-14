package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.formats.hpo.*;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * @deprecated use instance of {@link HpoDiseaseLoader}.
 */
@Deprecated
public class HpoDiseaseAnnotationLoader {

  private HpoDiseaseAnnotationLoader() {
  }

  @Deprecated(forRemoval = true)
  public static HpoDiseases loadHpoDiseases(Path annotationPath,
                                            Ontology hpoOntology,
                                            Set<DiseaseDatabase> databases) throws IOException {
    return HpoDiseases.of(List.of());
  }

}
