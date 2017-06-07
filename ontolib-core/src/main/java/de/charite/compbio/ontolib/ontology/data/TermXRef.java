package de.charite.compbio.ontolib.ontology.data;

import java.io.Serializable;

public interface TermXRef extends Serializable {
    public String getDatabase();
    public String getXrefID();
    public String getXrefName();
}
