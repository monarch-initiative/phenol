module org.monarchinitiative.phenol.core {
  exports org.monarchinitiative.phenol.base;
  exports org.monarchinitiative.phenol.graph;
  exports org.monarchinitiative.phenol.ontology.data;
  exports org.monarchinitiative.phenol.ontology.algo;
  exports org.monarchinitiative.phenol.ontology.scoredist;
  exports org.monarchinitiative.phenol.ontology.similarity;
  exports org.monarchinitiative.phenol.utils;

  requires transitive org.jgrapht.core; // due to DefaultDirectedGraph being exposed in MinimalOntology
  requires com.fasterxml.jackson.databind; // for annotating model classes
  requires org.slf4j;

  // To enable custom `TermId` serialization
  opens org.monarchinitiative.phenol.ontology.serialize;
}
