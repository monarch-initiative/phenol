package org.monarchinitiative.phenol.io.annotations.hpo;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationEntry;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationModel;
import org.monarchinitiative.phenol.base.HpoAnnotationModelException;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.obo.hpo.HpOboParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;


class PhenotypeDotHpoaFileWriterTest {

    private static HpoAnnotationEntry entry;
    private static HpoOntology ontology;

    @BeforeAll
    static void init() throws PhenolException, FileNotFoundException {
        ClassLoader classLoader = PhenotypeDotHpoaFileWriterTest.class.getClassLoader();
        String hpOboPath =Objects.requireNonNull(classLoader.getResource("hp_head.obo")).getFile();
        Objects.requireNonNull(hpOboPath);
        HpOboParser oboparser = new HpOboParser(new File(hpOboPath));
        ontology = oboparser.parse();
        // Make a typical entry. All other fields are emtpy.
        String diseaseID="OMIM:123456";
        String diseaseName="MADE-UP SYNDROME";
        String hpoId= "HP:0000528";
        String hpoName="Anophthalmia";
        String age1="";
        String age2="";
        String freq="HP:0040283";
        String sex="";
        String negation="";
        String mod="";
        String description="";
        String pub="OMIM:154700";
        String evidenceCode="IEA";
        String biocuration="HPO:skoehler[2015-07-26]";
        String fields[] ={diseaseID,diseaseName,hpoId,hpoName,age1,age2,freq,sex,negation,mod,description,pub,evidenceCode,biocuration};
        String line = String.join("\t",fields);
        entry = HpoAnnotationEntry.fromLine(line,ontology);
    }

    /**
     * Test emitting a line of the V2 (2018-?) big file from a V2 small file line.
     */
    @Test
    void testV2line() throws HpoAnnotationModelException {
        String [] bigFileFields = {
                "OMIM:123456",//DiseaseID
                "MADE-UP SYNDROME", // Name
                "",//Qualifier
                "HP:0000528", // HPO_ID,
                "OMIM:154700",//DB_Reference
                "IEA", // Evidence_Code
                "",//Onset
                "HP:0040283", // Frequency HP:0040283=Occasional
                "", // Sex
                "",//Modifier
                "P",// Aspect
                "HPO:skoehler[2015-07-26]", // biocuration
        };
        String expected= String.join("\t", bigFileFields);
        List<HpoAnnotationModel> emptyList = ImmutableList.of(); // needed for testing.
        PhenotypeDotHpoaFileWriter v1b = new PhenotypeDotHpoaFileWriter(ontology, emptyList, emptyList,"");
        String line = v1b.transformEntry2BigFileLine(entry);
        assertEquals(expected,line);
    }


    /** Note that the header should NOT have a #, which is used for the
     * preceding comment lines.
     */
    @Test
    void testV2Header() {
        String expected="DatabaseID\tDiseaseName\tQualifier\tHPO_ID\tReference\tEvidence\tOnset\tFrequency\tSex\tModifier\tAspect\tBiocuration";
        assertEquals(expected,PhenotypeDotHpoaFileWriter.getHeaderLine());
    }

}