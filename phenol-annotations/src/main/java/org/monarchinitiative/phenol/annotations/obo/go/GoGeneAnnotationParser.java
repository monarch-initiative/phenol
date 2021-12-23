package org.monarchinitiative.phenol.annotations.obo.go;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.monarchinitiative.phenol.annotations.formats.go.GoGaf22Annotation;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.annotations.formats.go.GoGaf21Annotation;
import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;

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



  private GoGeneAnnotationParser() {
  }


  public static List<GoGaf22Annotation> loadAnnotations(String filename) {
    File f = new File(filename);
    return GoGeneAnnotationParser.loadAnnotations(f.toPath());
  }

  public static List<GoGaf22Annotation> loadAnnotations(File file) {
    return GoGeneAnnotationParser.loadAnnotations(file.toPath());
  }

  public static List<GoGaf22Annotation> loadAnnotations(Path path) {
    List<GoGaf22Annotation> goGaf22annots = new ArrayList<>();
    try (BufferedReader br = Files.newBufferedReader(path)) {
      for (String line; (line = br.readLine()) != null; ) {
        // skip the comments, which start with exclamation marks
        if (! line.isEmpty() && ! line.startsWith("!")) {
          try {
            GoGaf22Annotation annot = GoGaf22Annotation.parseAnnotation(line);
            goGaf22annots.add(annot);
          } catch (PhenolException e) {
            System.err.printf("[ERROR] Could not parse GoGaf line (%s): %s", line, e.getMessage());
            // just skip this error. Should actually never happen
          }
        }
      }
    } catch (IOException e) {
      throw new PhenolRuntimeException("Could not parse " + path +": " + e.getMessage());
    }
    return goGaf22annots; // TODO add List.copyOf(...) with Java 11 refactor
  }

  /**
   * @return Return a list of GoGafAnnotation as {@link TermAnnotation} objects.
   */
  public static List<TermAnnotation> loadTermAnnotations(String filename) {
    File f = new File(filename);
    return GoGeneAnnotationParser.loadTermAnnotations(f.toPath());
  }

  /**
   * @return Return a list of GoGafAnnotation as {@link TermAnnotation} objects.
   */
  public static List<TermAnnotation> loadTermAnnotations(File file) {
   return GoGeneAnnotationParser.loadTermAnnotations(file.toPath());
  }

  /**
   * @return Return a list of GoGafAnnotation as {@link TermAnnotation} objects.
   */
  public static List<TermAnnotation> loadTermAnnotations(Path path) {
    List<GoGaf22Annotation> annots = GoGeneAnnotationParser.loadAnnotations(path);
    return new ArrayList<>(annots);
  }

}
