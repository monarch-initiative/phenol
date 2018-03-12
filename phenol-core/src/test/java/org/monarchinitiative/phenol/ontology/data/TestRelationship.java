package org.monarchinitiative.phenol.ontology.data;

public class TestRelationship implements Relationship {

  private static final long serialVersionUID = 1L;

  private ImmutableTermId source;
  private ImmutableTermId dest;
  private int id;

  public TestRelationship(ImmutableTermId source, ImmutableTermId dest, int id) {
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
    return "TestRelationship [source=" + source + ", dest=" + dest + ", id=" + id + "]";
  }
}
