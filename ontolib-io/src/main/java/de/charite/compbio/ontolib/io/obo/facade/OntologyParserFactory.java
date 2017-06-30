package de.charite.compbio.ontolib.io.obo.facade;

import java.io.File;
import java.io.IOException;

import de.charite.compbio.ontolib.io.base.OntologyOboParser;
import de.charite.compbio.ontolib.io.base.TermAnnotationParser;
import de.charite.compbio.ontolib.io.base.TermAnnotationParserException;
import de.charite.compbio.ontolib.io.obo.go.GoGeneAnnotationParser;
import de.charite.compbio.ontolib.io.obo.go.GoOboParser;
import de.charite.compbio.ontolib.io.obo.hpo.HpoGeneAnnotationParser;
import de.charite.compbio.ontolib.io.obo.hpo.HpoOboParser;
import de.charite.compbio.ontolib.io.obo.uberpheno.UberphenoGeneAnnotationParser;
import de.charite.compbio.ontolib.io.obo.uberpheno.UberphenoOboParser;
import de.charite.compbio.ontolib.io.obo.upheno.UphenoGeneAnnotationParser;
import de.charite.compbio.ontolib.io.obo.upheno.UphenoOboParser;
import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.TermAnnotation;

/**
 * Class with helper method for creating parsers for OBO and related files.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class OntologyParserFactory {

  /**
   * Construct new {@link OntologyOboParser} given an {@link OntologyType} enumeration value and
   * path to OBO file by {@code oboFile}.
   *
   * @param ontology Specifies the ontology to parse.
   * @param oboFile The OBO {@link File} specification.
   * @return The constructed {@link OntologyOboParser}.
   */
  public static OntologyOboParser<? extends Ontology<?, ?>> constructOboParser(
      OntologyType ontology, File oboFile) {
    switch (ontology) {
      case GO:
        return new GoOboParser(oboFile);
      case HPO:
        return new HpoOboParser(oboFile);
      case UBERPHENO:
        return new UberphenoOboParser(oboFile);
      case UPHENO:
        return new UphenoOboParser(oboFile);
      default:
        throw new RuntimeException("Invalid ontology selected " + ontology);
    }
  }

  /**
   * Construct new {@link TermAnnotationParser} for the term-to-gene annotation, given an
   * {@link OntologyType} enumeration value and path to annotation file by {@code annoFile}.
   *
   * @param ontology Specifies the ontology to parse.
   * @param annoFile The OBO {@link File} specification.
   * @return The constructed {@link OntologyOboParser}.
   * @throws TermAnnotationParserException When there is a problem with initializing the term
   *         annotation parser.
   * @throws IOException When there is a problem with file I/O.
   */
  public static TermAnnotationParser<? extends TermAnnotation> constructGeneAnnotationParser(
      OntologyType ontology, File annoFile) throws IOException, TermAnnotationParserException {
    switch (ontology) {
      case GO:
        return new GoGeneAnnotationParser(annoFile);
      case HPO:
        return new HpoGeneAnnotationParser(annoFile);
      case UBERPHENO:
        return new UberphenoGeneAnnotationParser(annoFile);
      case UPHENO:
        return new UphenoGeneAnnotationParser(annoFile);
      default:
        throw new RuntimeException("Invalid ontology selected " + ontology);
    }
  }

}
