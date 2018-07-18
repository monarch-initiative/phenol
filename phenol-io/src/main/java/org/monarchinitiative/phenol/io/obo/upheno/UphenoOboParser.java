//package org.monarchinitiative.phenol.io.obo.upheno;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Optional;
//
//import org.monarchinitiative.phenol.io.obo.OboOntologyLoader;
//import org.monarchinitiative.phenol.ontology.data.*;
//import org.monarchinitiative.phenol.formats.upheno.UphenoOntology;
//import org.monarchinitiative.phenol.io.base.OntologyOboParser;
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.ImmutableSortedMap;
//
///**
// * Facade class for parsing uPheno from an OBO file.
// *
// * <h5>Usage Example</h5>
// *
// * <pre>
// * String fileName = "crossSpeciesOntology.obo";
// * UphenoOBOParser parser = new UphenoOBOParser(new File(fileName));
// * UphenoOntology upheno;
// * try {
// *   upheno = parser.parse();
// * } catch (IOException e) {
// *   System.err.println("Problem reading file " + fileName + ": " + e.getMessage());
// * }
// * </pre>
// *
// * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
// */
//public final class UphenoOboParser implements OntologyOboParser<UphenoOntology> {
//
//  /** Path to the OBO file to parse. */
//  private final File oboFile;
//
//  /** Whether debugging is enabled or not. */
//  private final boolean debug;
//
//  /**
//   * Constructor.
//   *
//   * @param oboFile The OBO file to read.
//   * @param debug Whether or not to enable debugging.
//   */
//  public UphenoOboParser(File oboFile, boolean debug) {
//    this.oboFile = oboFile;
//    this.debug = debug;
//  }
//
//  /**
//   * Constructor, disabled debugging.
//   *
//   * @param oboFile The OBO file to read.
//   */
//  public UphenoOboParser(File oboFile) {
//    this(oboFile, false);
//  }
//
//  /**
//   * Parse OBO file into {@link UphenoOntology} object.
//   *
//   * @return {@link UphenoOntology} from parsing OBO file.
//   * @throws IOException In case of problems with file I/O.
//   */
//  public UphenoOntology parse() throws IOException {
//    Ontology o;
//    final OboOntologyLoader loader = new OboOntologyLoader(oboFile);
//    Optional<Ontology> optOnto = loader.load();
//    if (!optOnto.isPresent()) {
//      System.err.println("[ERROR] Failed to load ontology");
//      return null; // TODO return optional.
//    } else {
//      o = optOnto.get();
//    }
//    if (debug) {
//      System.err.println(String.format("Parsed a total of %d MP terms", o.countAllTerms()));
//    }
//    return new UphenoOntology(
//      (ImmutableSortedMap<String, String>) o.getMetaInfo(),
//      o.getGraph(),
//      o.getRootTermId(),
//      o.getNonObsoleteTermIds(),
//      o.getObsoleteTermIds(),
//      (ImmutableMap<TermId, Term>) o.getTermMap(),
//      (ImmutableMap<Integer, Relationship>) o.getRelationMap());
//  };
//
//    // Convert ImmutableOntology into UberphenoOntology. The casts here are ugly and require the
//    // @SuppressWarnings above but this saves us one factory layer of indirection.
//
//
//  /** @return The OBO file to parse. */
//  public File getOboFile() {
//    return oboFile;
//  }
//}
