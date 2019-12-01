package org.monarchinitiative.phenol.analysis;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;
import java.util.Set;

public class PopulationSet extends StudySet {

  public PopulationSet(Set<TermId> genes,
                       Map<TermId, DirectAndIndirectTermAnnotations> associationContainer,
                       Ontology ontology) {

    super(genes, "population set", associationContainer, ontology);
  }
}
