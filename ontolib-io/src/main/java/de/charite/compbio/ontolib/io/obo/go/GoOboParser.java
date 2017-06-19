package de.charite.compbio.ontolib.io.obo.go;

import com.google.common.collect.ImmutableMap;
import de.charite.compbio.ontolib.formats.go.GoOntology;
import de.charite.compbio.ontolib.formats.go.GoTerm;
import de.charite.compbio.ontolib.formats.go.GoTermRelation;
import de.charite.compbio.ontolib.formats.hpo.HpoOntology;
import de.charite.compbio.ontolib.graph.data.ImmutableDirectedGraph;
import de.charite.compbio.ontolib.graph.data.ImmutableEdge;
import de.charite.compbio.ontolib.io.obo.OboImmutableOntologyLoader;
import de.charite.compbio.ontolib.ontology.data.ImmutableOntology;
import de.charite.compbio.ontolib.ontology.data.TermId;
import java.io.File;
import java.io.IOException;

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
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class GoOboParser {

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
  @SuppressWarnings("unchecked")
  public GoOntology parse() throws IOException {
    final OboImmutableOntologyLoader<GoTerm, GoTermRelation> loader =
        new OboImmutableOntologyLoader<>(oboFile, debug);
    final GoOboFactory factory = new GoOboFactory();
    final ImmutableOntology<GoTerm, GoTermRelation> o = loader.load(factory);

    // Convert ImmutableOntology into GoOntology. The casts here are ugly and require the
    // @SuppressWarnings above but this saves us one factory layer of indirection.
    return new GoOntology((ImmutableDirectedGraph<TermId, ImmutableEdge<TermId>>) o.getGraph(),
        o.getRootTermId(), (ImmutableMap<TermId, GoTerm>) o.getTermMap(),
        (ImmutableMap<Integer, GoTermRelation>) o.getRelationMap());
  }

  /**
   * @return The OBO file to parse.
   */
  public File getOboFile() {
    return oboFile;
  }

}
