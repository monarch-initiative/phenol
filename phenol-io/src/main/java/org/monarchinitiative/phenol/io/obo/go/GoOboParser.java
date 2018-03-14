package org.monarchinitiative.phenol.io.obo.go;

import java.io.File;
import java.io.IOException;

import org.monarchinitiative.phenol.formats.go.GoOntology;
import org.monarchinitiative.phenol.formats.go.GoTerm;
import org.monarchinitiative.phenol.formats.go.GoRelationship;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.base.OntologyOboParser;
import org.monarchinitiative.phenol.io.obo.OboImmutableOntologyLoader;
import org.monarchinitiative.phenol.ontology.data.ImmutableOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

/**
 * Facade class for parsing the HPO from an OBO file.
 *
 * <h5>Usage Example</h5>
 *
 * <pre>
 * String fileName = "go.obo";
 * GoOboParser parser = new GoOboParser(new File(fileName));
 * GoOntology go;
 * try {
 *   go = parser.parse();
 * } catch (IOException e) {
 *   System.err.println("Problem reading file " + fileName + ": " + e.getMessage());
 * }
 * </pre>
 *
 * <h5>Multiple Root Terms</h5>
 *
 * <p>The Gene Ontology has multiple root terms. As documented in {@link
 * OboImmutableOntologyLoader}, an artificial root term with id {@link GO:0000000} will be inserted.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class GoOboParser implements OntologyOboParser<GoOntology> {

  /** Path to the OBO file to parse. */
  private final File oboFile;

  /** Whether debugging is enabled or not. */
  private final boolean debug;

  /**
   * Constructor.
   *
   * @param oboFile The OBO file to read.
   * @param debug Whether or not to enable debugging.
   */
  public GoOboParser(File oboFile, boolean debug) {
    this.oboFile = oboFile;
    this.debug = debug;
  }

  /**
   * Constructor, disabled debugging.
   *
   * @param oboFile The OBO file to read.
   */
  public GoOboParser(File oboFile) {
    this(oboFile, false);
  }

  /**
   * Parse OBO file into {@link HpoOntology} object.
   *
   * @return {@link HpoOntology} from parsing OBO file.
   * @throws IOException In case of problems with file I/O.
   */
  public GoOntology parse() throws IOException {
    final OboImmutableOntologyLoader<GoTerm, GoRelationship> loader =
        new OboImmutableOntologyLoader<>(oboFile, debug);
    final GoOboFactory factory = new GoOboFactory();
    final ImmutableOntology<GoTerm, GoRelationship> o = loader.load(factory);

    // Convert ImmutableOntology into GoOntology. The casts here are ugly and require the
    // @SuppressWarnings above but this saves us one factory layer of indirection.
    return new GoOntology(
        (ImmutableSortedMap<String, String>) o.getMetaInfo(),
        o.getGraph(),
        o.getRootTermId(),
        o.getNonObsoleteTermIds(),
        o.getObsoleteTermIds(),
        (ImmutableMap<TermId, GoTerm>) o.getTermMap(),
        (ImmutableMap<Integer, GoRelationship>) o.getRelationMap());
  }

  /** @return The OBO file to parse. */
  public File getOboFile() {
    return oboFile;
  }
}
