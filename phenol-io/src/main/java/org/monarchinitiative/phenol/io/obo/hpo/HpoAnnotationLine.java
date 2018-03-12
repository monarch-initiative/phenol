package org.monarchinitiative.phenol.io.obo.hpo;

import org.monarchinitiative.phenol.formats.hpo.HpoDiseaseWithMetadata;
import org.monarchinitiative.phenol.formats.hpo.HpoFrequency;
import org.monarchinitiative.phenol.formats.hpo.HpoOnset;
import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * This class represents one line of the V2 (post 2018) HPO annotation line from the
 * "big file" (phenotype.hpoa). It is intended to be use as part of the processing
 * of the big file. A convenience class that will allow us to collect the annotation
 * lines for each disease that we want to
 * parse; from these data, we will construct the {@link HpoDiseaseWithMetadata}
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class HpoAnnotationLine {


    String database;
    String DBObjectId;
    String DbObjectName;
    boolean NOT=false;
    TermId hpoId;
    HpoOnset onsetModifier;
    HpoFrequency freqeuncyModifier;

    public HpoAnnotationLine(String DB, String objectId,String name,boolean isNot, TermId termId, HpoOnset onset, HpoFrequency freq) {
      database=DB;
      DBObjectId=objectId;
      DbObjectName=name;
      NOT=isNot;
      hpoId=termId;
      onsetModifier=onset;
      freqeuncyModifier=freq;
    }



}
