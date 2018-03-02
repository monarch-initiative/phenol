package org.monarchinitiative.phenol.io.obo.hpo;

import java.io.File;
import java.io.IOException;

import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.formats.hpo.HpoTerm;
import org.monarchinitiative.phenol.formats.hpo.HpoTermRelation;
import org.monarchinitiative.phenol.graph.data.ImmutableDirectedGraph;
import org.monarchinitiative.phenol.graph.data.ImmutableEdge;
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
 * String fileName = "hp.obo";
 * HPOOBOParser parser = new HPOOBOParser(new File(fileName));
 * HPOntology hpo;
 * try {
 *   hpo = parser.parse();
 * } catch (IOException e) {
 *   System.err.println("Problem reading file " + fileName + ": " + e.getMessage());
 * }
 * </pre>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class HpoOboParser implements OntologyOboParser<HpoOntology> {

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
  public HpoOboParser(File oboFile, boolean debug) {
    this.oboFile = oboFile;
    this.debug = debug;
  }

  /**
   * Constructor, disabled debugging.
   *
   * @param oboFile The OBO file to read.
   */
  public HpoOboParser(File oboFile) {
    this(oboFile, false);
  }

  /**
   * Parse OBO file into {@link HpoOntology} object.
   *
   * @return {@link HpoOntology} from parsing OBO file.
   * @throws IOException In case of problems with file I/O.
   */
  @SuppressWarnings("unchecked")
  public HpoOntology parse() throws IOException {
    final OboImmutableOntologyLoader<HpoTerm, HpoTermRelation> loader =
        new OboImmutableOntologyLoader<>(oboFile, debug);
    final HpoOboFactory factory = new HpoOboFactory();
    final ImmutableOntology<HpoTerm, HpoTermRelation> o = loader.load(factory);

    // Convert ImmutableOntology into HPOntology. The casts here are ugly and require the
    // @SuppressWarnings above but this saves us one factory layer of indirection.
    return new HpoOntology((ImmutableSortedMap<String, String>) o.getMetaInfo(),
        (ImmutableDirectedGraph<TermId, ImmutableEdge<TermId>>) o.getGraph(), o.getRootTermId(),
        o.getNonObsoleteTermIds(), o.getObsoleteTermIds(),
        (ImmutableMap<TermId, HpoTerm>) o.getTermMap(),
        (ImmutableMap<Integer, HpoTermRelation>) o.getRelationMap());
  }

  /**
   * @return The OBO file to parse.
   */
  public File getOboFile() {
    return oboFile;
  }

}
