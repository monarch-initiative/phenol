package org.monarchinitiative.phenol.io.annotations.hpo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationEntry;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationModel;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrphanetXML2HpoDiseaseModelParserTest {

    static private OrphanetXML2HpoDiseaseModelParser parser;

    static private TermId veryFrequent = TermId.of("HP:0040281");

    static private TermId occasional  = TermId.of("HP:0040283");

    @BeforeAll
    private static void init() throws PhenolException, FileNotFoundException {
      Path orphaXMLpath = Paths.get("src", "test", "resources", "annotations", "en_product4_HPO.small.xml");
      Path hpOboPath = Paths.get("src", "test", "resources", "annotations", "hp_head.obo");
      Ontology ontology = OntologyLoader.loadOntology(hpOboPath.toFile());
      try {
        parser = new OrphanetXML2HpoDiseaseModelParser(orphaXMLpath.toAbsolutePath().toString(), ontology, false);
      } catch (Exception e) {
        System.err.println("Could not parse Orpha " + e.getMessage());
        throw e;
      }
    }



    @Test
    void testNotNull() {
        assertNotNull(parser);
    }

    @Test
    void testTwoDiseases() {
        int expectedNumberOfDiseases=2;
        List<HpoAnnotationModel> diseaseModels = parser.getOrphanetDiseaseModels();
        assertEquals(expectedNumberOfDiseases,diseaseModels.size());
    }

    /** consult the XML file en_product4_HPO.small.xml for the source of the ground truth here. */
    @Test
    void  testPhenotypesAndFrequenciesOfDisease1() {
        List<HpoAnnotationModel> diseaseModels = parser.getOrphanetDiseaseModels();
        HpoAnnotationModel file = diseaseModels.get(0);
        List<HpoAnnotationEntry> entrylist = file.getEntryList();
        // the first disease has three annotations
        int expectNumberOfAnnotations=3;
        assertEquals(expectNumberOfAnnotations,entrylist.size());
        // first HPO is HP:0100886</HPOId>
        //                        <HPOTerm>Abnormality of globe location
        TermId AbnGlobeLoc = TermId.of("HP:0100886");
        HpoAnnotationEntry entry1 = entrylist.get(0);
        assertEquals(AbnGlobeLoc,entry1.getPhenotypeId());
        assertEquals(veryFrequent.getValue(),entry1.getFrequencyModifier());

        HpoAnnotationEntry entry2 = entrylist.get(1);
        TermId Anophthalmia = TermId.of("HP:0000528");
        assertEquals(Anophthalmia,entry2.getPhenotypeId());
        assertEquals(veryFrequent.getValue(),entry2.getFrequencyModifier());

        HpoAnnotationEntry entry3 = entrylist.get(2);
        TermId LacrimationAbnormality = TermId.of("HP:0000632");
        assertEquals(LacrimationAbnormality,entry3.getPhenotypeId());
        assertEquals(veryFrequent.getValue(),entry3.getFrequencyModifier());
    }

    /** consult the XML file en_product4_HPO.small.xml for the source of the ground truth here. */
    @Test
    void  testPhenotypesAndFrequenciesOfDisease2() {
        List<HpoAnnotationModel> diseaseModels = parser.getOrphanetDiseaseModels();
        HpoAnnotationModel file = diseaseModels.get(1);
        List<HpoAnnotationEntry> entrylist = file.getEntryList();
        // the first disease has three annotations
        int expectNumberOfAnnotations = 2;
        HpoAnnotationEntry entry1 = entrylist.get(0);
        TermId Anophthalmia = TermId.of("HP:0000528");
        assertEquals(Anophthalmia,entry1.getPhenotypeId());
        assertEquals(occasional.getValue(),entry1.getFrequencyModifier());
        HpoAnnotationEntry entry2 = entrylist.get(1);
        TermId AbnormalPupillaryFunction = TermId.of("HP:0007686");
        assertEquals(AbnormalPupillaryFunction,entry2.getPhenotypeId());
        assertEquals(occasional.getValue(),entry1.getFrequencyModifier());

    }


}
