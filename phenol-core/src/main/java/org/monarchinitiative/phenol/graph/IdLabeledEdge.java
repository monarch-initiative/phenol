package org.monarchinitiative.phenol.graph;

import org.jgrapht.graph.DefaultEdge;

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