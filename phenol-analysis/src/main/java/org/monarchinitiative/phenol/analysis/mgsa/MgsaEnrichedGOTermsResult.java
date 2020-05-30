package org.monarchinitiative.phenol.analysis.mgsa;


import org.monarchinitiative.phenol.analysis.AssociationContainer;
import org.monarchinitiative.phenol.analysis.StudySet;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.util.ArrayList;
import java.util.List;


/**
 * Cares about the result of the b2g approach.
 *
 * @author Sebastian Bauer
 */
public class MgsaEnrichedGOTermsResult
{
    private MgsaScore score;

    /** A linear list containing properties for go terms */
    protected final List<MgsaGOTermProperties> list = new ArrayList<>();

    /** The GO Graph */
    protected final Ontology go;

    /** The association container */
    private final AssociationContainer associations;

    private final int populationGeneCount;

    private final StudySet studySet;


    public MgsaEnrichedGOTermsResult(Ontology go,
                                     AssociationContainer associations, StudySet studySet,
                                     int populationGeneCount)
    {
        this.go = go;
        this.associations = associations;
        this.studySet = studySet;
        this.populationGeneCount = populationGeneCount;
    }


    /**
     * @param prop Result of an MGSA calculaton for on GO term.
     */
    public void addGOTermProperties(MgsaGOTermProperties prop) {
        if (prop.getTermId() == null)
            throw new IllegalArgumentException("prop.term mustn't be null");
        list.add(prop);
    }

    public void dumpToShell() {
      System.out.println("[INFO] Terms with marginal probability above 0.1%");
      for (MgsaGOTermProperties agtp : this.list) {
        double d = agtp.getMarg();
        if (agtp.getAnnotatedStudyGenes()<2) {
          continue;
        }
        if (d <0.001) {
          continue;
        }
        List<String> items = new ArrayList<>();
        items.add(agtp.getTermId().getValue());
        items.add(go.getTermMap().get(agtp.getTermId()).getName());
        items.add(String.format("%d/%d (%.1f%%)",
          agtp.getAnnotatedStudyGenes(),
          agtp.getAnnotatedPopulationGenes(),
          100.0*(double)agtp.getAnnotatedStudyGenes()/agtp.getAnnotatedPopulationGenes()));
        items.add(String.format("%f",agtp.getMarg()));
        System.out.println(String.join("\t",items));
      }
    }

}
