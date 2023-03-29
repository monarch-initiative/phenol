package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.formats.hpo.AnnotatedItemContainer;
import org.monarchinitiative.phenol.ontology.data.Versioned;

import java.util.*;

public class HpoaDiseaseDataContainer implements AnnotatedItemContainer<HpoaDiseaseData>, Versioned {

  private final String version; // nullable
  // Version of HPO used to generate the HPO annotation files.
  // The annotations can be processed with a more recent HPO thanks to backward compatibility.
  private final String hpoVersion; // nullable
  private final List<HpoaDiseaseData> diseaseData;

  HpoaDiseaseDataContainer(String version, String hpoVersion, List<HpoaDiseaseData> diseaseData) {
    this.version = version; // nullable
    this.hpoVersion = hpoVersion; // nullable
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

  /**
   * @return a {@linkplain String} with HPO version used to generate the HPO annotations
   * or an empty {@linkplain Optional} if the version could not be parsed.
   */
  public Optional<String> getHpoVersion() {
    return Optional.ofNullable(hpoVersion);
  }
}
