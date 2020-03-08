package org.monarchinitiative.phenol.io.obographs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geneontology.obographs.model.GraphDocument;
import org.geneontology.obographs.owlapi.FromOwl;
import org.monarchinitiative.phenol.base.PhenolException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads an ontology using the obograph library.
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class OboGraphDocumentLoader {

  private OboGraphDocumentLoader() {
  }

  public static GraphDocument loadOwl(Path path) throws IOException, PhenolException {
    return loadObo(Files.newInputStream(path));
  }

  public static GraphDocument loadOwl(InputStream inputStream) throws PhenolException {
    return loadObo(inputStream);
  }

  public static GraphDocument loadObo(Path path) throws IOException, PhenolException {
    return loadObo(Files.newInputStream(path));
  }

  public static GraphDocument loadObo(InputStream inputStream) throws PhenolException {
    OWLOntologyManager m = OWLManager.createOWLOntologyManager();
    OWLOntology owlOntology;
    try {
      owlOntology = m.loadOntologyFromOntologyDocument(inputStream);
    } catch (OWLOntologyCreationException e) {
      throw new PhenolException(e);
    }

    FromOwl fromOwl = new FromOwl();
    return fromOwl.generateGraphDocument(owlOntology);
  }

  public static GraphDocument loadJson(Path path) throws IOException {
      return loadJson(Files.newInputStream(path));
  }

  public static GraphDocument loadJson(InputStream inputStream) throws IOException {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(inputStream, GraphDocument.class);
  }

}
