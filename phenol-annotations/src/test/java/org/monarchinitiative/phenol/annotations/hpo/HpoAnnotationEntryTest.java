package org.monarchinitiative.phenol.annotations.hpo;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.formats.hpo.category.HpoCategoryMapTest;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationEntry;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationModelException;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;


class HpoAnnotationEntryTest {

    private static Ontology ontology;


    @BeforeAll
    static void init() throws IOException {
        final String hpOboPath = "hp_head.obo";
        ClassLoader classLoader = HpoCategoryMapTest.class.getClassLoader();
        URL hpOboURL = classLoader.getResource(hpOboPath);
        if (hpOboURL == null) {
            throw new IOException("Could not find hpOboPath at " + hpOboPath);
        }
        File file = new File(hpOboURL.getFile());
        ontology = OntologyLoader.loadOntology(file);
    }

    @Test
    void testEvidenceCodeNotEmpty() throws PhenolException {

        String[] fields={
                "OMIM:216300",
                "CLEFT PALATE, DEAFNESS, AND OLIGODONTIA",
                "HP:0000007",
                "Autosomal recessive inheritance",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "OMIM:216300",
                "IEA",
                "HPO:probinson[2013-01-09]"};
        String line = String.join("\t",fields);//Arrays.stream(fields).collect(Collectors.joining("\t"))

        HpoAnnotationEntry entry= HpoAnnotationEntry.fromLine(line,ontology);

        Assertions.assertEquals("IEA",entry.getEvidenceCode());
    }

    @Test
    void testEvidenceCodeValid() {
        String[] fields={
                "OMIM:123456",
                "MADE-UP SYNDROME",
                "HP:0000528",
                "Anophthalmia",
                "",
                "",
                "76.3%",
                "FEMALE",
                "",
                "",
                "",
                "PMID:9843983",
                "PC",
                "HPO:probinson[2013-01-09]"};
        String line = String.join("\t",fields);//Arrays.stream(fields).collect(Collectors.joining("\t"));
        Assertions.assertThrows(PhenolException.class, () -> {
            HpoAnnotationEntry entry = HpoAnnotationEntry.fromLine(line,ontology);
        });
    }

    /** Throws an exception becase the disease name is missing. */
    @Test
    void testMissingDiseaseName() {
        String[] fields={
                "OMIM:123456",
                "",
                "HP:0000528",
                "Anophthalmia",
                "",
                "",
                "76.3%",
                "FEMALE",
                "",
                "",
                "",
                "PMID:9843983",
                "PCS",
                "HPO:probinson[2013-01-09]"};
        String line = String.join("\t",fields);
        Assertions.assertThrows(HpoAnnotationModelException.class, () -> {
            HpoAnnotationEntry entry = HpoAnnotationEntry.fromLine(line, ontology);
        });
    }



    /** Test that an exception is thrown because HP:1230528 does not exist.*/
    @Test
    void testRecognizeBadHPOId() {
        String[] fields={
                "123456",
                "MADE-UP SYNDROME",
                "HP:1230528",
                "Anophthalmia",
                "",
                "",
                "76.3%",
                "FEMALE",
                "",
                "",
                "",
                "PMID:9843983",
                "PCS",
                "HPO:probinson[2013-01-09]"};
        String line = String.join("\t",fields);
        Assertions.assertThrows(PhenolException.class, () -> {
            HpoAnnotationEntry entry = HpoAnnotationEntry.fromLine(line,ontology);
        });
    }

    /** This should go through with no problems. */
    @Test
    void testFrequency() throws PhenolException {
        String[] fields={
                "OMIM:123456",
                "MADE-UP SYNDROME",
                "HP:0000528",
                "Anophthalmia",
                "",
                "",
                "96.7%",
                "FEMALE",
                "",
                "",
                "",
                "PMID:9843983",
                "PCS",
                "HPO:probinson[2013-01-09]"};
        String line = String.join("\t",fields);
        HpoAnnotationEntry entry = HpoAnnotationEntry.fromLine(line,ontology);
        Assertions.assertEquals("96.7%",entry.getFrequencyModifier());
    }


    /** The first entry should be OMIM:123456 and not just 123456.*/
    @Test
    void testRecognizeBadDatabaseId() {
        String[] fields={
                "123456",
                "MADE-UP SYNDROME",
                "HP:0000528",
                "Anophthalmia",
                "",
                "",
                "76.3%",
                "FEMALE",
                "",
                "",
                "",
                "PMID:9843983",
                "PCS",
                "HPO:probinson[2013-01-09]"};
        String line = String.join("\t",fields);
        Assertions.assertThrows(HpoAnnotationModelException.class, () -> {
            HpoAnnotationEntry entry = HpoAnnotationEntry.fromLine(line,ontology);
        });
    }

    /** This should go through with no problems */
    @Test
    void testFreq1() throws PhenolException {
        String[] fields={
                "OMIM:123456",
                "MADE-UP SYNDROME",
                "HP:0000528",
                "Anophthalmia",
                "",
                "",
                "7/42",
                "FEMALE",
                "",
                "",
                "",
                "PMID:9843983",
                "PCS",
                "HPO:probinson[2013-01-09]"};
        String line = String.join("\t",fields);
        HpoAnnotationEntry entry = HpoAnnotationEntry.fromLine(line,ontology);
        Assertions.assertEquals("7/42",entry.getFrequencyModifier());
    }




}
