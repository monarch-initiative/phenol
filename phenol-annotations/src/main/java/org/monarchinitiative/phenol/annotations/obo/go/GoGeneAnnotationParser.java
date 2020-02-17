package org.monarchinitiative.phenol.annotations.obo.go;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.PrettyPrinter;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.annotations.formats.go.GoGaf21Annotation;
import org.monarchinitiative.phenol.annotations.base.TermAnnotationParserException;
import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;

/**
 * Parser for GO "gene annotation file" (GAF) format.
 *
 * <p><b>Usage Example</b>
 *
 * <pre>
 * File inputFile = "goa_human.gaf";
 * try {
 *   GoGeneAnnotationFileParser parser = new GoGeneAnnotationFileParser(inputFile);
 *   List<GoGaf21Annotation> = parser.getAnnotations();
 *   ...
 *   }
 * } except (PhenolException e) {
 *   System.err.println("Problem reading from file.");
 * }
 * </pre>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public final class GoGeneAnnotationParser  {



  private GoGeneAnnotationParser() {
  }


  public static List<GoGaf21Annotation> loadAnnotations(String filename) {
    File f = new File(filename);
    return GoGeneAnnotationParser.loadAnnotations(f);
  }

  public static List<GoGaf21Annotation> loadAnnotations(File file) {
    String nextLine;
    ImmutableList.Builder<GoGaf21Annotation> builder = new ImmutableList.Builder<>();
    try (BufferedReader br = new  BufferedReader(new FileReader(file))) {
      nextLine =br.readLine();
      // skip the comments, which start with exclamation marks
      while (nextLine.isEmpty() || nextLine.startsWith("!")) {
        nextLine = br.readLine();
      }
      do {
        final String[] arry = nextLine.split("\t");
        try {
          GoGaf21Annotation annot = new GoGaf21Annotation(arry);
          builder.add(annot);
        } catch (PhenolException e) {
          System.err.printf("[ERROR] Could not parse GoGaf line (%s): %s", nextLine, e.getMessage());
          // just skip this error. Should actually never happen
        }
      } while ((nextLine = br.readLine()) != null);
    } catch (IOException e) {
      throw new PhenolRuntimeException(e.getMessage());
    }

    return builder.build();
  }

  /**
   * @return Return a list of GoGafAnnotation as {@link TermAnnotation} objects.
   */
  public static List<TermAnnotation> loadTermAnnotations(String filename) {
    File f = new File(filename);
    return GoGeneAnnotationParser.loadTermAnnotations(f);
  }

  /**
   * @return Return a list of GoGafAnnotation as {@link TermAnnotation} objects.
   */
  public static List<TermAnnotation> loadTermAnnotations(File file) {
    List<GoGaf21Annotation> annots = GoGeneAnnotationParser.loadAnnotations(file);
    return new ArrayList<>(annots);
  }

}
