package org.monarchinitiative.phenol.graph;

import org.jgrapht.graph.DefaultEdge;

/**
 * A class that represents an edge with a labeled numeric Id. Id is maintained to support
 * compatibilities with old codes from Ontolib, i.e. this Id is used as a link/key to a
 * relevant Relationship instance.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class IdLabeledEdge extends DefaultEdge {
  private static final long serialVersionUID = 1L;
  private int id;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Object getSource() {
    return super.getSource();
  }

  public Object getTarget() {
    return super.getTarget();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    Object source = super.getSource();
    Object target = super.getTarget();

    result = prime * result + ((target == null) ? 0 : target.hashCode());
    result = prime * result + id;
    result = prime * result + ((source == null) ? 0 : source.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (! ( obj instanceof IdLabeledEdge) ) return false;
    IdLabeledEdge edge = (IdLabeledEdge) obj;
    if (this.getSource() != edge.getSource()) return false;
    if (this.getTarget() != edge.getTarget()) return false;
    return this.id == edge.id;
  }
}
