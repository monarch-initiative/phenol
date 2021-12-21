module org.monarchinitiative.phenol.core {
  exports org.monarchinitiative.phenol.base;
  exports org.monarchinitiative.phenol.graph;
  exports org.monarchinitiative.phenol.ontology.data;
  exports org.monarchinitiative.phenol.ontology.algo;
  exports org.monarchinitiative.phenol.ontology.scoredist;
  exports org.monarchinitiative.phenol.ontology.similarity;

  requires com.google.common;
  requires org.jgrapht.core;
  requires org.slf4j;
}
