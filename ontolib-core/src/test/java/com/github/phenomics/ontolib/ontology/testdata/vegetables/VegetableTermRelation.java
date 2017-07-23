package com.github.phenomics.ontolib.ontology.testdata.vegetables;

import com.github.phenomics.ontolib.ontology.data.ImmutableTermId;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.github.phenomics.ontolib.ontology.data.TermRelation;

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
