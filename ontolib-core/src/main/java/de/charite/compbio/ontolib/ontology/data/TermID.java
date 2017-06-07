package de.charite.compbio.ontolib.ontology.data;

import java.io.Serializable;

public interface TermID extends Comparable <TermID>, Serializable {
    public TermPrefix getPrefix();
    public int getID();
}
