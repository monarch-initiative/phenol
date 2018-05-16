package org.monarchinitiative.phenol.ontology.testdata.vegetables;

import org.monarchinitiative.phenol.ontology.data.ImmutableTermId;
import org.monarchinitiative.phenol.ontology.data.RelationshipI;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class VegetableRelationship implements RelationshipI {

  private static final long serialVersionUID = 1L;

  private ImmutableTermId source;
  private ImmutableTermId dest;
  private int id;

  public VegetableRelationship(ImmutableTermId source, ImmutableTermId dest, int id) {
    this.source = source;
    this.dest = dest;
    this.id = id;
  }

  @Override
  public TermId getSource() {
    return source;
  }

  @Override
  public TermId getTarget() {
    return dest;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public String toString() {
    return "VegetableRelationship [source=" + source + ", dest=" + dest + ", id=" + id + "]";
  }
}
