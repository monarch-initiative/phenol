package org.monarchinitiative.phenol.io.annotations.hpo;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationModel;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.obo.hpo.HpOboParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;


class HpoAnnotationModelTest {
    private static HpoOntology ontology;
    private static HpoAnnotationModel v2sf=null;


    @BeforeAll
    static void init() throws PhenolException, FileNotFoundException {
        Path hpOboPath = Paths.get("src","test","resources","annotations","hp_head.obo");
        String hpOboFile=hpOboPath.toAbsolutePath().toString();
        HpOboParser oboparser = new HpOboParser(new File(hpOboFile));
        ontology = oboparser.parse();
        Path omim123456path = Paths.get("src","test","resources","annotations","OMIM-123456.tab");
        String omim123456file = omim123456path.toAbsolutePath().toString();
        HpoAnnotationFileParser parser = new HpoAnnotationFileParser(omim123456file,ontology);
        v2sf = parser.parse();
    }

    @Test
    void testParse() {
       Assertions.assertNotNull(v2sf);
    }

    @Test
    void basenameTest() {
        Assertions.assertEquals("OMIM-123456.tab", v2sf.getBasename());
    }

    @Test
    void isOmimTest() {
        Assertions.assertTrue(v2sf.isOMIM());
        Assertions.assertFalse(v2sf.isDECIPHER());
    }

    /** Our test file has three annotation lines. */
    @Test
    void numberOfAnnotationsTest() {
        Assertions.assertEquals(3,v2sf.getNumberOfAnnotations());
    }



}
