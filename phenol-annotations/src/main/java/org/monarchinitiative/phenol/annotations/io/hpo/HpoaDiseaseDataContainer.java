package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.formats.hpo.AnnotatedItemContainer;
import org.monarchinitiative.phenol.ontology.data.Versioned;

import java.util.*;

public class HpoaDiseaseDataContainer implements AnnotatedItemContainer<HpoaDiseaseData>, Versioned {

  private final String version; // nullable
  private final List<HpoaDiseaseData> diseaseData;

  HpoaDiseaseDataContainer(String version, List<HpoaDiseaseData> diseaseData) {
    this.version = version; // nullable
    this.diseaseData = Objects.requireNonNull(diseaseData);
  }

  @Override
  public Iterator<HpoaDiseaseData> iterator() {
    return diseaseData.iterator();
  }

  public List<HpoaDiseaseData> diseaseData() {
    return diseaseData;
  }

  @Override
  public Optional<String> version() {
    return Optional.ofNullable(version);
  }
}
