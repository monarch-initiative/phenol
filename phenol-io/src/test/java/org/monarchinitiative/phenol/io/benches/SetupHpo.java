package org.monarchinitiative.phenol.io.benches;

import org.monarchinitiative.biodownload.BioDownloader;
import org.monarchinitiative.biodownload.FileDownloadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Helper for downloading a specific version of HPO into a platform-specific tmp directory.
 * <p>
 * The HPO JSON is not overwritten if it already exists.
 */
class SetupHpo {

  private static final Logger LOGGER = LoggerFactory.getLogger(SetupHpo.class);

  /**
   * Download a JSON file of given HPO version into a tmp folder.
   *
   * @param version the version to download, e.g. <pre>v2023-10-09</pre>
   * @return path to the downloaded HPO JSON file.
   */
  static Path hpoJsonPath(String version) {
    LOGGER.info("Preparing HPO URL for version {}", version);
    URL hpoUrl = prepareHpoUrl(version);
    LOGGER.info("Using HPO from {}", hpoUrl);
    String hpJsonName = String.format("hp.%s.json", version);


    // Setup builder
    Path tmpDir = Path.of(System.getProperty("java.io.tmpdir")).resolve("phenol-bench");
    LOGGER.info("Downloading HPO to {}", tmpDir);
    BioDownloader downloader = BioDownloader.builder(tmpDir)
      .custom(hpJsonName, hpoUrl)
      .build();

    // Download
    try {
      downloader.download();
    } catch (FileDownloadException e) {
      throw new RuntimeException(e);
    }

    return tmpDir.resolve(hpJsonName);
  }

  private static URL prepareHpoUrl(String version) {
    try {
      return new URI(String.format("https://github.com/obophenotype/human-phenotype-ontology/releases/download/%s/hp.json", version)).toURL();
    } catch (URISyntaxException | MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

}
