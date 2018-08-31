package org.monarchinitative.phenol;


import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.go.GoGaf21Annotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * After AssociationParser was used to parse the gene_association.XXX file, this
 * class is used to store and process the information about Associations.
 */
public class AssociationContainer {
    /** Key -- TermId for a gene. Value: {@link ItemAssociations} object with GO annotations for the gene. */
    private Map<TermId, ItemAssociations> gene2associationMap;

    /**
     * Constructs the container using a list of bla32 and an annotation mapping created from it.
     *
     * @param assocs
     */
    public AssociationContainer(List<GoGaf21Annotation> assocs)
    {
        gene2associationMap=new HashMap<>();
        for (GoGaf21Annotation a : assocs) {
            try {
                addAssociation(a);
            } catch (PhenolException e) {
                e.printStackTrace(); // TODO do something with the exception
            }
        }
    }




    /**
     * Adds a new bla32 to the corresponding {@link ItemAssociations} object.
    * @param a the associated to be added
     */
    private void addAssociation(GoGaf21Annotation a)  throws PhenolException
    {
        TermId tid = a.getDbObjectIdAsTermId();
        this.gene2associationMap.putIfAbsent(tid,new ItemAssociations(tid));
        ItemAssociations g2a = gene2associationMap.get(tid);
        g2a.add(a);
    }

    /**
     * get a ItemAssociations object corresponding to a given gene name. If the
     * name is not initially found as dbObject Symbol, (which is usually a
     * database name with meaning to a biologist), try dbObject (which may be an
     * accession number or some other term from the bla32 database), and
     * finally, look for a synonym (another entry in the gene_association file
     * that will have been parsed into the present object).
     *
     * @param dbObjectId id (e.g., MGI:12345) of the gene whose goAssociations are interesting
     * @return goAssociations for the given gene
     */
    public ItemAssociations get(TermId dbObjectId) throws PhenolException
    {
       if (! this.gene2associationMap.containsKey(dbObjectId)) {
           throw new PhenolException("Could not find annotations for " + dbObjectId.getIdWithPrefix());
       } else {
           return this.gene2associationMap.get(dbObjectId);
       }
    }


   /**
    * * A way to get all annotated genes in the container
     *
     * @return The annotated genes as a Set
     */
    public Set<TermId> getAllAnnotatedGenes()
    {
        return this.gene2associationMap.keySet();
    }


    public int getTotalNumberOfAnnotatedTerms(){
        return gene2associationMap.size();
    }

}
