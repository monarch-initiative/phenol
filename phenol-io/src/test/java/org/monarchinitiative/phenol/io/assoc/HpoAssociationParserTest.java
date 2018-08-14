package org.monarchinitiative.phenol.io.assoc;


import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.Gene;
import org.monarchinitiative.phenol.formats.hpo.DiseaseToGeneAssociation;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.obo.hpo.HpOboParser;
import org.monarchinitiative.phenol.ontology.data.TermId;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class HpoAssociationParserTest {
  private HpoAssociationParser parser;

  @Before
  public void init() throws IOException, PhenolException {
    System.setProperty("user.timezone", "UTC"); // Somehow setting in pom.xml does not work :(

	ClassLoader classLoader = this.getClass().getClassLoader();

    URL mim2GeneUrl = classLoader.getResource("mim2gene_medgen.excerpt");
    URL geneInfoUrl = classLoader.getResource("Homo_sapiens.gene_info.excerpt.gz");
 
    final HpOboParser oboParser = new HpOboParser(classLoader.getResourceAsStream("hp_head.obo"), true);
    final HpoOntology ontology = oboParser.parse();

    parser = new HpoAssociationParser(geneInfoUrl.getPath(), mim2GeneUrl.getPath(), ontology);
    parser.parse();
  }

  @Test
  public void testNotNull() {
    assertNotNull(parser);
  }

  /**
   * TBX5 is the only gene involved with Holt-Oram syndrome (OMIM:142900), and
   * TBX5 is not associated with other diseases. TBX5 has the EntrezGene id 6910
   */
  @Test
  public void testHoltOram() {
    Map<TermId, DiseaseToGeneAssociation> diseasemap = parser.getDiseaseToAssociationsMap();
    TermId holtOramId = TermId.constructWithPrefix("OMIM:142900");
    assertTrue(diseasemap.containsKey(holtOramId));
    DiseaseToGeneAssociation holtOramAssociation = diseasemap.get(holtOramId);
    List<Gene> geneList = holtOramAssociation.getGeneList();
    assertEquals(1, geneList.size());
    Gene gene = geneList.get(0);
    TermId tbx5Id = TermId.constructWithPrefix("NCBIGene:6910");
    assertEquals(tbx5Id, gene.getId());
    String symbol = "TBX5";
    assertEquals(symbol, gene.getSymbol());
  }

  /**
   * ARHGAP31 is the only gene assicuated with Adams-Oliver syndrome type 1 (OMIM:100300)
   * ARHGAP31 is not associated with other diseases and it has the EntrezGene id 57514
   */
  @Test
  public void testAdamsOliver() {
    Map<TermId, DiseaseToGeneAssociation> diseasemap = parser.getDiseaseToAssociationsMap();
    TermId adamsOliver1Id = TermId.constructWithPrefix("OMIM:100300");
    assertTrue(diseasemap.containsKey(adamsOliver1Id));
    DiseaseToGeneAssociation holtOramAssociation = diseasemap.get(adamsOliver1Id);
    List<Gene> geneList = holtOramAssociation.getGeneList();
    assertEquals(1, geneList.size());
    Gene gene = geneList.get(0);
    TermId tbx5Id = TermId.constructWithPrefix("NCBIGene:57514");
    assertEquals(tbx5Id, gene.getId());
    String symbol = "ARHGAP31";
    assertEquals(symbol, gene.getSymbol());
  }

  /**
   * There are several disease associated with FBN1.
   * 1. Acromicric dysplasia 	OMIM:102370
   * 2. Ectopia lentis, familial 	OMIM:129600
   * 3. Geleophysic dysplasia 2 	OMIM:614185
   * 4. Marfan lipodystrophy syndrome 	OMIM:616914
   * 5. Marfan syndrome 	OMIM:154700
   * 6. MASS syndrome 	OMIM:604308 		3
   * 7. Stiff skin syndrome 	OMIM:184900
   * 8. Weill-Marchesani syndrome 2, dominant 	OMIM:608328
   */
  @Test
  public void testFbn1() {
    Multimap<TermId, TermId> mmap = parser.getGeneToDiseaseIdMap();
    TermId Fbn1Id = TermId.constructWithPrefix("NCBIGene:2200");
    assertTrue(mmap.containsKey(Fbn1Id));
    Collection<TermId> diseaseIdCollection = mmap.get(Fbn1Id);
    assertEquals(8, diseaseIdCollection.size());
    TermId acromicricDysplasia = TermId.constructWithPrefix("OMIM:102370");
    assertTrue(diseaseIdCollection.contains(acromicricDysplasia));
    TermId ectopiaLentis = TermId.constructWithPrefix("OMIM:129600");
    assertTrue(diseaseIdCollection.contains(ectopiaLentis));
    TermId geleophysicDysplasia2 = TermId.constructWithPrefix("OMIM:614185");
    assertTrue(diseaseIdCollection.contains(geleophysicDysplasia2));
    TermId marfanLipodystrophySyndrome = TermId.constructWithPrefix("OMIM:616914");
    assertTrue(diseaseIdCollection.contains(marfanLipodystrophySyndrome));
    TermId marfanSyndrome = TermId.constructWithPrefix("OMIM:154700");
    assertTrue(diseaseIdCollection.contains(marfanSyndrome));
    TermId massSyndrome = TermId.constructWithPrefix("OMIM:604308");
    assertTrue(diseaseIdCollection.contains(massSyndrome));
    TermId stiffSkinSyndrome = TermId.constructWithPrefix("OMIM:184900");
    assertTrue(diseaseIdCollection.contains(stiffSkinSyndrome));
    TermId weillMarchesani2 = TermId.constructWithPrefix("OMIM:608328");
    assertTrue(diseaseIdCollection.contains(weillMarchesani2));
  }


  /**
   * OMIM:143890 is HYPERCHOLESTEROLEMIA, FAMILIAL
   * It is associated with a number of genes:
   * APOA2 (336)
   * ITIH4 (3700)
   * GHR (2690)
   * PPP1R17 (10842; aka	GSBS) 	604088
   * EPHX2 (2053) 	132811
   * ABCA1 (19)
   * LDLR (3949)
   * <p>
   * <p>
   * <p>
   * 143890	10842	phenotype	 GeneMap	C0020445	susceptibility
   * 143890	19	phenotype	 GeneMap	C0020445	susceptibility
   * 143890	2053	phenotype	 GeneMap	C0020445	susceptibility; modifier
   * 143890	2690	phenotype	 GeneMap	C0020445	susceptibility; modifier
   * 143890	336	phenotype	 GeneMap	C0020445	susceptibility; modifier
   * 143890	3700	phenotype	 GeneMap	C0020445	susceptibility
   */
  @Test
  public void testSusceptibilityGenes() {
    Map<TermId, DiseaseToGeneAssociation> diseasemap = parser.getDiseaseToAssociationsMap();
    TermId familialHypercholesterolemia = TermId.constructWithPrefix("OMIM:143890");
    assertTrue(diseasemap.containsKey(familialHypercholesterolemia));
    DiseaseToGeneAssociation hypercholesterolemiaAssociation = diseasemap.get(familialHypercholesterolemia);
    List<Gene> geneList = hypercholesterolemiaAssociation.getGeneList();
    assertEquals(7, geneList.size());
    Gene APOA2 = new Gene(TermId.constructWithPrefix("NCBIGene:336"), "APOA2");
    assertTrue(geneList.contains(APOA2));
    Gene ITIH4 = new Gene(TermId.constructWithPrefix("NCBIGene:3700"), "ITIH4");
    assertTrue(geneList.contains(ITIH4));
    Gene GHR = new Gene(TermId.constructWithPrefix("NCBIGene:2690"), "GHR");
    assertTrue(geneList.contains(GHR));
    Gene PPP1R17 = new Gene(TermId.constructWithPrefix("NCBIGene:10842"), "PPP1R17");
    assertTrue(geneList.contains(PPP1R17));
    Gene EPHX2 = new Gene(TermId.constructWithPrefix("NCBIGene:2053"), "EPHX2");
    assertTrue(geneList.contains(EPHX2));
    Gene ABCA1 = new Gene(TermId.constructWithPrefix("NCBIGene:19"), "ABCA1");
    assertTrue(geneList.contains(ABCA1));
    Gene LDLR = new Gene(TermId.constructWithPrefix("NCBIGene:3949"), "LDLR");
    assertTrue(geneList.contains(LDLR));
  }

  @Test
  public void testTermToGene() throws PhenolException {
    // This test map should come from {@link HpoDiseaseAnnotationParser}
    TermId familialHypercholesterolemia = TermId.constructWithPrefix("OMIM:143890");
    TermId fakePhenotype = TermId.constructWithPrefix("HP:0000118");
    Multimap<TermId, TermId> testMap = ArrayListMultimap.create();
    testMap.put(fakePhenotype, familialHypercholesterolemia);
    parser.setTermToGene(testMap);
    assertEquals(parser.getPhenotypeToGene().size(), 7);
  }

  @Test
  public void testDiseasetoGene() {
    Multimap<TermId, TermId> diseaseMap = parser.getDiseaseToGeneIdMap();
    Collection<TermId> genes = diseaseMap.get(TermId.constructWithPrefix("OMIM:143890"));
    assertEquals(genes.size(), 7);
  }


  @Test
  public void testGeneIdToGeneSymbol(){
    Map<TermId, String> geneMap = parser.getGeneIdToSymbolMap();
    assertEquals(geneMap.get(TermId.constructWithPrefix("NCBIGene:2690")),"GHR");
    assertEquals(geneMap.get(TermId.constructWithPrefix("NCBIGene:2200")),"FBN1");
  }
}
