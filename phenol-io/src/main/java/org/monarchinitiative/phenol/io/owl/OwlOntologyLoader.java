package org.monarchinitiative.phenol.io.owl;

import org.geneontology.obographs.model.GraphDocument;
import org.geneontology.obographs.owlapi.FromOwl;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.io.obo.OboGraphDocumentAdaptor;
import org.monarchinitiative.phenol.io.obo.OboGraphTermFactory;
import org.monarchinitiative.phenol.io.utils.CurieMapGenerator;
import org.monarchinitiative.phenol.ontology.data.ImmutableOntology;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.prefixcommons.CurieUtil;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Load OWL into an {@link ImmutableOntology}.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class OwlOntologyLoader implements OntologyLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(OwlOntologyLoader.class);
  private final File file;
  private final CurieUtil curieUtil;
  /**
   * Factory object that adds OBO-typical data to each term.
   */
  private final OboGraphTermFactory factory;

  /**
   * Construct an OWL loader that can load an OBO ontology.
   *
   * @param file Path to the OBO file
   */
  public OwlOntologyLoader(File file) {
    this.file = file;
    curieUtil = new CurieUtil(CurieMapGenerator.generate());
    this.factory = new OboGraphTermFactory();
  }

  @Override
  public Ontology load() throws PhenolException {

    // We first load ontologies expressed in owl using Obographs's FromOwl class.
    OWLOntologyManager m = OWLManager.createOWLOntologyManager();
    OWLOntology ontology;
    try {
      ontology = m.loadOntologyFromOntologyDocument(file);
    } catch (OWLOntologyCreationException e) {
      throw new PhenolException("Could not create OWL ontology: " + e.getMessage());
    }
    LOGGER.debug("Finished loading OWLOntology");

    LOGGER.debug("Converting to obograph graph");
    FromOwl fromOwl = new FromOwl();
    GraphDocument gd = fromOwl.generateGraphDocument(ontology);

    OboGraphDocumentAdaptor oboGraphDocumentAdaptor = new OboGraphDocumentAdaptor(gd);
    return oboGraphDocumentAdaptor.createOntology();
  }

}
