package org.monarchinitiative.phenol.formats.mpo;

public enum MpMarkerType {

  BAC_YAC_END("BAC/YAC end"),
  COMPLEX_CLUSTER_REGION("Complex/Cluster/Region"),
  CYTOGENETIC_MARKER("Cytogenetic Marker"),
  DNA_SEGMENT("DNA Segment"),
  GENE("Gene"),
  OTHER_GENOME_FEATURE("Other Genome Feature"),
  PSEUDOGENE("Pseudogene"),
  QTL("QTL"),
  TRANSGENE("Transgene");

  private final String name;


   MpMarkerType(String name) {
    this.name=name;
  }

}
