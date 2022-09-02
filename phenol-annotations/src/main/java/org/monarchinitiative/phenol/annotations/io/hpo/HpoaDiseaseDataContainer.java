package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.formats.hpo.AnnotatedItemContainer;

import java.util.*;

public class HpoaDiseaseDataContainer implements AnnotatedItemContainer<HpoaDiseaseData> {

  private final List<HpoaDiseaseData> diseaseData;

  public HpoaDiseaseDataContainer(List<HpoaDiseaseData> diseaseData) {
    this.diseaseData = Objects.requireNonNull(diseaseData);
  }

  @Override
  public Iterator<HpoaDiseaseData> iterator() {
    return diseaseData.iterator();
  }

  public List<HpoaDiseaseData> diseaseData() {
    return diseaseData;
  }

}
