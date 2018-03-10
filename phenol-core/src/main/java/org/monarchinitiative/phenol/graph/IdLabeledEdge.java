package org.monarchinitiative.phenol.graph;

import org.jgrapht.graph.DefaultEdge;

/**
 * A class that represents an edge with a labeled numeric Id. Id is not really required, but exists
 * for maintaining compatibilities with old codes from Ontolib, i.e. this Id is used as a link to a
 * relevant {@link Relationship} instance.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class IdLabeledEdge extends DefaultEdge {
  private static final long serialVersionUID = -7062460276552763271L;
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
}
