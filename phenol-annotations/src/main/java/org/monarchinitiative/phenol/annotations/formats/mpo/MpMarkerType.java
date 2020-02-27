package org.monarchinitiative.phenol.annotations.formats.mpo;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;

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

  private String name;


  MpMarkerType(String name) {
    this.name = name;
  }

  public static MpMarkerType string2enum(String marker)  {
    switch (marker) {
      case "BAC/YAC end": return BAC_YAC_END;
      case "Complex/Cluster/Region": return COMPLEX_CLUSTER_REGION;
      case  "Cytogenetic Marker": return  CYTOGENETIC_MARKER;
      case  "DNA Segment": return DNA_SEGMENT;
      case  "Gene": return GENE;
      case  "Other Genome Feature": return OTHER_GENOME_FEATURE;
      case  "Pseudogene": return PSEUDOGENE;
      case  "QTL": return QTL;
      case  "Transgene": return TRANSGENE;
      default:
        // should never happen
        throw new PhenolRuntimeException("MpMarkerType.string2enum: Did not recognize MpMarkerType "+marker);
    }


  }


}
