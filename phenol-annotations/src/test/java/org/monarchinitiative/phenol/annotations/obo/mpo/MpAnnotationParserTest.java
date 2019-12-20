package org.monarchinitiative.phenol.annotations.obo.mpo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.annotations.formats.mpo.MpAnnotation;
import org.monarchinitiative.phenol.annotations.formats.mpo.MpSimpleModel;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MpAnnotationParserTest {

  private static String genePhenoPath;

  private static String phenoSexPath;

  @BeforeAll
  static void setUp() throws IOException {
    ClassLoader classLoader = MpGeneParserTest.class.getClassLoader();
    URL url = classLoader.getResource("mgi/MGI_GenePheno.rpt.excerpt");
    if (url == null) {
      throw new IOException("MGI_GenePheno.rpt.excerpt");
    }
    genePhenoPath=url.getPath();
    url = classLoader.getResource("mgi/MGI_Pheno_Sex.rpt.excerpt");
    if (url == null) {
      throw new IOException("MGI_Pheno_Sex.rpt.excerpt");
    }
    phenoSexPath=url.getPath();
  }


  /**
   * We have the following models in the MGI_GenePheno.rpt.excerpt file.
   * MGI:2166359; MGI:2167486; MGI:5306347,MGI:5433360,MGI:2169820 thus we expect to see five models
   */
  @Test
  void testRetrieveCorrectNumberOfModels() throws PhenolException{
    MpAnnotationParser parser = new MpAnnotationParser(genePhenoPath);
    Map<TermId, MpSimpleModel> modelmap=parser.getGenotypeAccessionToMpModelMap();
    int expected_number_of_models=5;
    assertEquals(expected_number_of_models,modelmap.size());
  }

  @Test
  void testSexSpecificParser() throws PhenolException{
    MpAnnotationParser parser = new MpAnnotationParser(genePhenoPath,phenoSexPath);
    Map<TermId, MpSimpleModel> modelmap=parser.getGenotypeAccessionToMpModelMap();
    TermId kit = TermId.of("MGI:2167486");
    MpSimpleModel model = modelmap.get(kit);
    assertNotNull(model);
    assertTrue(model.hasSexSpecificAnnotation());
  }

  /* This model should have 5 abnormalities. In the excerpt, none of them are listed as sexSpecific specific. */
  @Test
  void testParseModelMgi5306347() throws PhenolException{
    MpAnnotationParser parser = new MpAnnotationParser(genePhenoPath);
    Map<TermId, MpSimpleModel> modelmap=parser.getGenotypeAccessionToMpModelMap();
    TermId kit = TermId.of("MGI:5306347");
    MpSimpleModel model = modelmap.get(kit);
    assertNotNull(model);
    List<MpAnnotation> annots = model.getPhenotypicAbnormalities();
    int expected_number_of_MP_terms=5;
    assertEquals(expected_number_of_MP_terms,annots.size());
    assertFalse(model.hasSexSpecificAnnotation());
  }

  /** 	MGI:5433360 has here two annotation with two PMIDs each. */
  @Test
  void getMultiplePmid() throws PhenolException{
    MpAnnotationParser parser = new MpAnnotationParser(genePhenoPath);
    Map<TermId, MpSimpleModel> modelmap=parser.getGenotypeAccessionToMpModelMap();
    TermId kit = TermId.of("MGI:5433360");
    MpSimpleModel model = modelmap.get(kit);
    assertNotNull(model);
    List<MpAnnotation> annots = model.getPhenotypicAbnormalities();
    int expected_number_of_MP_terms=2;
    assertEquals(expected_number_of_MP_terms,annots.size());
    assertFalse(model.hasSexSpecificAnnotation());
    // The following annotation should have two PMIDs: 2383655|22773809
    MpAnnotation annot = annots.get(0);
    Set<String> pmids = annot.getPmidSet();
    assertEquals(2,pmids.size());

  }


  @Test
  void testGetCorrectMarker()throws PhenolException {
    MpAnnotationParser parser = new MpAnnotationParser(genePhenoPath);
    Map<TermId, MpSimpleModel> modelmap=parser.getGenotypeAccessionToMpModelMap();
    TermId kitlGenotype = TermId.of("MGI:5306347");
    MpSimpleModel model = modelmap.get(kitlGenotype);
    assertNotNull(model);
    // the MGI id for the kit ligand (kitl) gene is MGI:96974
    TermId Kitl = TermId.of("MGI:96974");
    assertEquals(Kitl,model.getMarkerId());
  }


}
