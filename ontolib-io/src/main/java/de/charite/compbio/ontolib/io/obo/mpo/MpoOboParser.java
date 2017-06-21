package de.charite.compbio.ontolib.io.obo.mpo;

import java.io.File;
import java.io.IOException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

import de.charite.compbio.ontolib.formats.mpo.MpoOntology;
import de.charite.compbio.ontolib.formats.mpo.MpoTerm;
import de.charite.compbio.ontolib.formats.mpo.MpoTermRelation;
import de.charite.compbio.ontolib.graph.data.ImmutableDirectedGraph;
import de.charite.compbio.ontolib.graph.data.ImmutableEdge;
import de.charite.compbio.ontolib.io.obo.OboImmutableOntologyLoader;
import de.charite.compbio.ontolib.ontology.data.ImmutableOntology;
import de.charite.compbio.ontolib.ontology.data.TermId;

/**
 * Facade class for parsing the Mpo from an OBO file.
 *
 * <h5>Usage Example</h5>
 *
 * <pre>
 * String fileName = "hp.obo";
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
public final class MpoOboParser {

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
        (ImmutableMap<TermId, MpoTerm>) o.getTermMap(),
        (ImmutableMap<TermId, MpoTerm>) o.getObsoleteTermMap(),
        (ImmutableMap<Integer, MpoTermRelation>) o.getRelationMap());
  }

  /**
   * @return The OBO file to parse.
   */
  public File getOboFile() {
    return oboFile;
  }

}
