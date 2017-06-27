package de.charite.compbio.ontolib.ontology.testdata.vegetables;

import de.charite.compbio.ontolib.ontology.data.ImmutableTermId;
import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.data.TermRelation;

public class VegetableTermRelation implements TermRelation {

  private static final long serialVersionUID = 1L;

  private ImmutableTermId source;
  private ImmutableTermId dest;
  private int id;

  public VegetableTermRelation(ImmutableTermId source, ImmutableTermId dest, int id) {
    this.source = source;
    this.dest = dest;
    this.id = id;
  }

  @Override
  public TermId getSource() {
    return source;
  }

  @Override
  public TermId getDest() {
    return dest;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public String toString() {
    return "VegetableTermRelation [source=" + source + ", dest=" + dest + ", id=" + id + "]";
  }

}
