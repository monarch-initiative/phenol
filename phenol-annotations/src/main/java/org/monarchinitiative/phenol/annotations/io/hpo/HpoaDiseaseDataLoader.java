package org.monarchinitiative.phenol.annotations.io.hpo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public interface HpoaDiseaseDataLoader {

  static HpoaDiseaseDataLoader of(Set<String> databasePrefixes) {
    return new HpoaDiseaseDataLoaderDefault(databasePrefixes);
  }

  HpoaDiseaseDataContainer loadDiseaseData(InputStream is) throws IOException;

}
