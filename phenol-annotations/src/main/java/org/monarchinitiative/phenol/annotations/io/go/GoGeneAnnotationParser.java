package org.monarchinitiative.phenol.annotations.io.go;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.monarchinitiative.phenol.annotations.formats.go.GoGaf22Annotation;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parser for GO "gene annotation file" (GAF) format.
 *
 * <p><b>Usage Example</b>
 *
 * <pre>
 * String filename = "goa_human.gaf";
 * List<GoGaf21Annotation> annots = GoGeneAnnotationParser.loadAnnotations(filename);
 * </pre>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public final class GoGeneAnnotationParser  {

  private static final Logger LOGGER = LoggerFactory.getLogger(GoGeneAnnotationParser.class);

  private GoGeneAnnotationParser() {
  }


  public static List<GoGaf22Annotation> loadAnnotations(Path path) {
    List<GoGaf22Annotation> goGaf22annots = new ArrayList<>();
    try (BufferedReader br = Files.newBufferedReader(path)) {
      for (String line; (line = br.readLine()) != null; ) {
        // skip the comments, which start with exclamation marks
        if (! line.isEmpty() && ! line.startsWith("!")) {
          try {
            goGaf22annots.add(GoGaf22Annotation.parseAnnotation(line));
          } catch (PhenolException e) {
            LOGGER.error("Could not parse GoGaf line ({}): {}", line, e.getMessage());
            // just skip this error. Should actually never happen
          }
        }
      }
    } catch (IOException e) {
      throw new PhenolRuntimeException("Could not parse " + path +": " + e.getMessage());
    }
    return List.copyOf(goGaf22annots);
  }

  /**
   * @return a list of GoGafAnnotation as {@link TermAnnotation} objects.
   */
  // This should be considered as redundant as it is super easy to get Path from File. As such, I do not see
  // a lot of convenience here, just API that is larger than necessary.
  @Deprecated(forRemoval = true)
  public static List<GoGaf22Annotation> loadTermAnnotations(File file) {
   return GoGeneAnnotationParser.loadAnnotations(file.toPath());
  }

  /**
   * @return a list of GoGafAnnotation as {@link TermAnnotation} objects.
   */
  @Deprecated(forRemoval = true) // this is redundant as loadAnnotations returns List<GoGaf22Annotation>
  public static List<TermAnnotation> loadTermAnnotations(Path path) {
    List<GoGaf22Annotation> annots = GoGeneAnnotationParser.loadAnnotations(path);
    return new ArrayList<>(annots);
  }

}
