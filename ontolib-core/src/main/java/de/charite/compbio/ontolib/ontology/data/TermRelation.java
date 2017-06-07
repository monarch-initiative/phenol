package de.charite.compbio.ontolib.ontology.data;

public interface TermRelation {
    public TermID getSource();
    public TermID getDest();
    public int getID();
}
