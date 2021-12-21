module org.monarchinitiative.phenol.core {
  exports org.monarchinitiative.phenol.base;
  exports org.monarchinitiative.phenol.graph;
  exports org.monarchinitiative.phenol.ontology.data;
  exports org.monarchinitiative.phenol.ontology.algo;
  exports org.monarchinitiative.phenol.ontology.scoredist;
  exports org.monarchinitiative.phenol.ontology.similarity;

  requires transitive org.jgrapht.core; // due to DefaultDirectedGraph being exposed in MinimalOntology
  requires com.google.common;
  requires org.slf4j;
}
