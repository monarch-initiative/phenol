package org.monarchinitiative.phenol.io.obo.mpo;

import java.io.File;
import java.io.IOException;

import org.monarchinitiative.phenol.formats.mpo.MpoOntology;
import org.monarchinitiative.phenol.formats.mpo.MpoTerm;
import org.monarchinitiative.phenol.formats.mpo.MpoTermRelation;
import org.monarchinitiative.phenol.graph.data.ImmutableDirectedGraph;
import org.monarchinitiative.phenol.graph.data.ImmutableEdge;
import org.monarchinitiative.phenol.io.base.OntologyOboParser;
import org.monarchinitiative.phenol.io.obo.OboImmutableOntologyLoader;
import org.monarchinitiative.phenol.ontology.data.ImmutableOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

/**
 * Facade class for parsing the Mpo from an OBO file.
 *
 * <h5>Usage Example</h5>
 *
 * <pre>
 * String fileName = "mp.obo";
 * MpoOBOParser parser = new MpoOBOParser(new File(fileName));
 * Mpontology Mpo;
 * try {
 *   Mpo = parser.parse();
 * } catch (IOException e) {
 *   System.err.println("Problem reading file " + fileName + ": " + e.getMessage());
 * }
 * </pre>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class MpoOboParser implements OntologyOboParser<MpoOntology> {

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
  public MpoOboParser(File oboFile, boolean debug) {
    this.oboFile = oboFile;
    this.debug = debug;
  }

  /**
   * Constructor, disabled debugging.
   *
   * @param oboFile The OBO file to read.
   */
  public MpoOboParser(File oboFile) {
    this(oboFile, false);
  }

  /**
   * Parse OBO file into {@link MpoOntology} object.
   *
   * @return {@link MpoOntology} from parsing OBO file.
   * @throws IOException In case of problems with file I/O.
   */
  @SuppressWarnings("unchecked")
  public MpoOntology parse() throws IOException {
    final OboImmutableOntologyLoader<MpoTerm, MpoTermRelation> loader =
        new OboImmutableOntologyLoader<>(oboFile, debug);
    final MpoOboFactory factory = new MpoOboFactory();
    final ImmutableOntology<MpoTerm, MpoTermRelation> o = loader.load(factory);

    // Convert ImmutableOntology into Mpontology. The casts here are ugly and require the
    // @SuppressWarnings above but this saves us one factory layer of indirection.
    return new MpoOntology((ImmutableSortedMap<String, String>) o.getMetaInfo(),
        (ImmutableDirectedGraph<TermId, ImmutableEdge<TermId>>) o.getGraph(), o.getRootTermId(),
        o.getNonObsoleteTermIds(), o.getObsoleteTermIds(),
        (ImmutableMap<TermId, MpoTerm>) o.getTermMap(),
        (ImmutableMap<Integer, MpoTermRelation>) o.getRelationMap());
  }

  /**
   * @return The OBO file to parse.
   */
  public File getOboFile() {
    return oboFile;
  }

}
