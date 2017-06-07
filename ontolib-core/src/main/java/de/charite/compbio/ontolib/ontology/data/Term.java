package de.charite.compbio.ontolib.ontology.data;

import java.util.List;

public interface Term {
    public TermID getID();

    public List<TermID> getAltTermIDs();

    public String getName();

    public String getDefinition();

    public String getComment();

    public String getSubset();

    public List<TermSynonym> getSynonyms();

    public boolean isObsolete();

    public String getCreatedBy();

    public String getCreationDate();
}
