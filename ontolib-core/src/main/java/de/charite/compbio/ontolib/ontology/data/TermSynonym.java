package de.charite.compbio.ontolib.ontology.data;

import java.util.List;

public interface TermSynonym {
    public String getValue();
    public String getAuthor();
    public TermSynonymScope getScope();
    public String getSynonymTypeName();
    public List<TermXRef> getTermXRefs();
}
