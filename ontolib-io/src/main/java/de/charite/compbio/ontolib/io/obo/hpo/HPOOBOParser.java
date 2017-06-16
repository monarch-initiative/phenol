package de.charite.compbio.ontolib.io.obo.hpo;

import com.google.common.collect.ImmutableMap;
import de.charite.compbio.ontolib.formats.hpo.HPOTerm;
import de.charite.compbio.ontolib.formats.hpo.HPOTermRelation;
import de.charite.compbio.ontolib.formats.hpo.HPOntology;
import de.charite.compbio.ontolib.graph.data.ImmutableDirectedGraph;
import de.charite.compbio.ontolib.graph.data.ImmutableEdge;
import de.charite.compbio.ontolib.io.obo.OBOImmutableOntologyLoader;
import de.charite.compbio.ontolib.ontology.data.ImmutableOntology;
import de.charite.compbio.ontolib.ontology.data.TermID;
import java.io.File;
import java.io.IOException;

/**
 * Facade class for parsing the HPO from an OBO file.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class HPOOBOParser {

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
  public HPOOBOParser(File oboFile, boolean debug) {
    this.oboFile = oboFile;
    this.debug = debug;
  }

  /**
   * Constructor, disabled debugging.
   * 
   * @param oboFile The OBO file to read.
   */
  public HPOOBOParser(File oboFile) {
    this(oboFile, false);
  }

  /**
   * Parse OBO file into {@link HPOntology} object.
   *
   * @return {@link HPOntology} from parsing OBO file.
   * @throws IOException In case of problems with file I/O.
   */
  @SuppressWarnings("unchecked")
  public HPOntology parse() throws IOException {
    final OBOImmutableOntologyLoader<HPOTerm, HPOTermRelation> loader =
        new OBOImmutableOntologyLoader<>(oboFile, debug);
    final HPOOBOEntryFactory factory = new HPOOBOEntryFactory();
    final ImmutableOntology<HPOTerm, HPOTermRelation> o = loader.load(factory);

    // Convert ImmutableOntology into HPOntology. The casts here are ugly and require the
    // @SuppressWarnings above but this saves us one factory layer of indirection.
    return new HPOntology((ImmutableDirectedGraph<TermID, ImmutableEdge<TermID>>) o.getGraph(),
        o.getRootTermID(), (ImmutableMap<TermID, HPOTerm>) o.getTermMap(),
        (ImmutableMap<Integer, HPOTermRelation>) o.getRelationMap());
  }

  /**
   * @return The OBO file to parse.
   */
  public File getOboFile() {
    return oboFile;
  }

}
