package org.monarchinitiative.phenol.annotations.formats.mpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

import static org.monarchinitiative.phenol.annotations.formats.mpo.MpMarkerType.GENE;

/**
 * A MpGene object corresponds to a genetic marker in MGI. It contains the MGI marker accession id,
 * the marker symbol, and the marker type (gene, transgene, etc.).
 * @author Hannah Blau (blauh)
 * @version 0.0.1
 * @since 12 Dec 2017
 */
public class MpGeneticMarker {
  private final String geneSymbol;

  private final MpMarkerType markerType;
  private final TermId mgiId;

  /**
   * Private constructor for MpGeneticMarker objects.
   * @param mgiId        MGI marker accession id, e.g., MGI:97874
   * @param geneSymbol   MGI gene symbol, e.g., Rb1
   * @param markerType   MGI marker type, one of those listed below
   */
  private MpGeneticMarker(TermId mgiId, String geneSymbol, MpMarkerType markerType) {
    this.mgiId = mgiId;
    this.geneSymbol = geneSymbol;
    this.markerType = markerType;
  }

  /**
   * Factory method to construct an MpGeneticMarker object.
   * @param mgiId        MGI marker accession id, e.g., MGI:97874
   * @param geneSymbol   MGI gene symbol, e.g., Rb1
   * @param markerType   MGI marker type, one of:
   *                     BAC/YAC end; Complex/Cluster/Region; Cytogenetic Marker; DNA Segment;
   *                     Gene; Other Genome Feature; Pseudogene; QTL; Transgene
   * @return             MpGene object
   */
  public static MpGeneticMarker createMpGeneticMarker(TermId mgiId, String geneSymbol, String markerType) {
    MpMarkerType marker = MpMarkerType.string2enum(markerType);
    return new MpGeneticMarker(mgiId, geneSymbol, marker);
  }

  /**
   * Getter method for gene symbol.
   * @return   String the gene symbol
   */
  public String getGeneSymbol() {
    return geneSymbol;
  }

  /**
   * Getter method for marker type.
   * @return   String one of the 9 marker types listed above
   */
  public MpMarkerType getMarkerType() {
    return markerType;
  }

  /**
   * Getter method for MGI marker accession id.
   * @return   String the MGI id
   */
  public TermId getMgiGeneId() {
    return mgiId;
  }
  /** @return true iff this marker is a gene (see MpMarkerType for other categories). */
  public boolean isGene() { return this.markerType.equals(GENE); }

  /**
   * toString method of MpGene.
   * @return String     printable representation of this MpGene object
   */
  @Override
  public String toString() {
    return "MpGeneticMarker{" +
      "mgiId: " + mgiId +
      ", geneSymbol: " + geneSymbol +
      ", markerType: " + markerType +
      '}';
  }
}
