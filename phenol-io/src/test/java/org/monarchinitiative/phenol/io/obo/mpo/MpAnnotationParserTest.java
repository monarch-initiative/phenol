package org.monarchinitiative.phenol.io.obo.mpo;

import org.junit.BeforeClass;
import org.junit.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.mpo.MpAnnotation;
import org.monarchinitiative.phenol.formats.mpo.MpModel;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MpAnnotationParserTest {

  private static String genePhenoPath;

  private static String phenoSexPath;

  @BeforeClass
  public static void setUp() throws IOException {
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
   * MGI:2166359; MGI:2167486; MGI:5306347,MGI:5433360 thus we expect to see four models
   */
  @Test
  public void testRetrieveCorrectNumberOfModels() throws PhenolException{
    MpAnnotationParser parser = new MpAnnotationParser(genePhenoPath);
    Map<TermId, MpModel> modelmap=parser.getGenotypeAccessionToMpModelMap();
    int expected_number_of_models=4;
    assertEquals(expected_number_of_models,modelmap.size());
  }

  @Test
  public void testSexSpecificParser() throws PhenolException{
    MpAnnotationParser parser = new MpAnnotationParser(genePhenoPath,phenoSexPath);
    Map<TermId, MpModel> modelmap=parser.getGenotypeAccessionToMpModelMap();
    TermId kit = TermId.constructWithPrefix("MGI:2167486");
    MpModel model = modelmap.get(kit);
    assertNotNull(model);
    assertTrue(model.hasSexSpecificAnnotation());
  }

  /* This model should have 5 abnormalities. In the excerpt, none of them are listed as sex specific. */
  @Test
  public void testParseModelMgi5306347() throws PhenolException{
    MpAnnotationParser parser = new MpAnnotationParser(genePhenoPath);
    Map<TermId, MpModel> modelmap=parser.getGenotypeAccessionToMpModelMap();
    TermId kit = TermId.constructWithPrefix("MGI:5306347");
    MpModel model = modelmap.get(kit);
    assertNotNull(model);
    List<MpAnnotation> annots = model.getPhenotypicAbnormalities();
    int expected_number_of_MP_terms=5;
    assertEquals(expected_number_of_MP_terms,annots.size());
    assertFalse(model.hasSexSpecificAnnotation());
  }

  /** 	MGI:5433360 has here two annotation with two PMIDs each. */
  @Test
  public void getMultiplePmid() throws PhenolException{
    MpAnnotationParser parser = new MpAnnotationParser(genePhenoPath);
    Map<TermId, MpModel> modelmap=parser.getGenotypeAccessionToMpModelMap();
    TermId kit = TermId.constructWithPrefix("MGI:5433360");
    MpModel model = modelmap.get(kit);
    assertNotNull(model);
    List<MpAnnotation> annots = model.getPhenotypicAbnormalities();
    int expected_number_of_MP_terms=2;
    assertEquals(expected_number_of_MP_terms,annots.size());
    assertFalse(model.hasSexSpecificAnnotation());
    // The following annotation should have two PMIDs: 2383655|22773809
    MpAnnotation annot = annots.get(0);
    List<String> pmids = annot.getPmidList();
    assertEquals(2,pmids.size());

  }


}
