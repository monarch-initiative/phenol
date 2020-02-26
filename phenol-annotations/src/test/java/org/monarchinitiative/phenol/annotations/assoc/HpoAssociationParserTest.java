package org.monarchinitiative.phenol.annotations.assoc;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.formats.Gene;
import org.monarchinitiative.phenol.annotations.formats.hpo.DiseaseToGeneAssociation;
import org.monarchinitiative.phenol.annotations.formats.hpo.GeneToAssociation;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.core.IsCollectionContaining.*;

class HpoAssociationParserTest {
  private HpoAssociationParser parser;

  @BeforeEach
  void init() throws PhenolException {
    System.setProperty("user.timezone", "UTC"); // Somehow setting in pom.xml does not work :(

	  ClassLoader classLoader = this.getClass().getClassLoader();

    URL mim2GeneUrl = classLoader.getResource("mim2gene_medgen.excerpt");
    URL geneInfoUrl = classLoader.getResource("Homo_sapiens.gene_info.excerpt.gz");
    URL orphanetUrl = classLoader.getResource("orphanet_disease2gene_en_product6_head.xml");
    URL phenotypeUrl = classLoader.getResource("annotations/phenotype_annotation_head.tab");

    Ontology ontology = OntologyLoader.loadOntology(classLoader.getResourceAsStream("hp_head.obo"));

    parser = new HpoAssociationParser(geneInfoUrl.getPath(), mim2GeneUrl.getPath(),
      orphanetUrl.getPath(),phenotypeUrl.getPath(),ontology);
  }

  @Test
  void testNotNull() {
    assertNotNull(parser);
  }

  /**
   * TBX5 is the only gene involved with Holt-Oram syndrome (OMIM:142900), and
   * TBX5 is not associated with other diseases. TBX5 has the EntrezGene id 6910
   */
  @Test
  void testHoltOram() {
    Map<TermId, DiseaseToGeneAssociation> diseasemap = parser.getDiseaseToAssociationsMap();
    TermId holtOramId = TermId.of("OMIM:142900");
    assertTrue(diseasemap.containsKey(holtOramId));
    DiseaseToGeneAssociation holtOramAssociation = diseasemap.get(holtOramId);
    List<Gene> geneList = holtOramAssociation.getGeneList();
    assertEquals(1, geneList.size());
    Gene gene = geneList.get(0);
    TermId tbx5Id = TermId.of("NCBIGene:6910");
    assertEquals(tbx5Id, gene.getId());
    String symbol = "TBX5";
    assertEquals(symbol, gene.getSymbol());
  }

  /**
   * ARHGAP31 is the only gene assicuated with Adams-Oliver syndrome type 1 (OMIM:100300)
   * ARHGAP31 is not associated with other diseases and it has the EntrezGene id 57514
   */
  @Test
  void testAdamsOliver() {
    Map<TermId, DiseaseToGeneAssociation> diseasemap = parser.getDiseaseToAssociationsMap();
    TermId adamsOliver1Id = TermId.of("OMIM:100300");
    assertTrue(diseasemap.containsKey(adamsOliver1Id));
    DiseaseToGeneAssociation holtOramAssociation = diseasemap.get(adamsOliver1Id);
    List<Gene> geneList = holtOramAssociation.getGeneList();
    assertEquals(1, geneList.size());
    Gene gene = geneList.get(0);
    TermId tbx5Id = TermId.of("NCBIGene:57514");
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
  void testFbn1() {
    Multimap<TermId, TermId> mmap = parser.getGeneToDiseaseIdMap();
    TermId Fbn1Id = TermId.of("NCBIGene:2200");
    assertTrue(mmap.containsKey(Fbn1Id));
    Collection<TermId> diseaseIdCollection = mmap.get(Fbn1Id);
    assertEquals(8, diseaseIdCollection.size());
    TermId acromicricDysplasia = TermId.of("OMIM:102370");
    assertTrue(diseaseIdCollection.contains(acromicricDysplasia));
    TermId ectopiaLentis = TermId.of("OMIM:129600");
    assertTrue(diseaseIdCollection.contains(ectopiaLentis));
    TermId geleophysicDysplasia2 = TermId.of("OMIM:614185");
    assertTrue(diseaseIdCollection.contains(geleophysicDysplasia2));
    TermId marfanLipodystrophySyndrome = TermId.of("OMIM:616914");
    assertTrue(diseaseIdCollection.contains(marfanLipodystrophySyndrome));
    TermId marfanSyndrome = TermId.of("OMIM:154700");
    assertTrue(diseaseIdCollection.contains(marfanSyndrome));
    TermId massSyndrome = TermId.of("OMIM:604308");
    assertTrue(diseaseIdCollection.contains(massSyndrome));
    TermId stiffSkinSyndrome = TermId.of("OMIM:184900");
    assertTrue(diseaseIdCollection.contains(stiffSkinSyndrome));
    TermId weillMarchesani2 = TermId.of("OMIM:608328");
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
  void testSusceptibilityGenes() {
    Map<TermId, DiseaseToGeneAssociation> diseasemap = parser.getDiseaseToAssociationsMap();
    TermId familialHypercholesterolemia = TermId.of("OMIM:143890");
    assertTrue(diseasemap.containsKey(familialHypercholesterolemia));
    DiseaseToGeneAssociation hypercholesterolemiaAssociation = diseasemap.get(familialHypercholesterolemia);
    List<Gene> geneList = hypercholesterolemiaAssociation.getGeneList();
    assertEquals(7, geneList.size());
    Gene APOA2 = new Gene(TermId.of("NCBIGene:336"), "APOA2");
    assertTrue(geneList.contains(APOA2));
    Gene ITIH4 = new Gene(TermId.of("NCBIGene:3700"), "ITIH4");
    assertTrue(geneList.contains(ITIH4));
    Gene GHR = new Gene(TermId.of("NCBIGene:2690"), "GHR");
    assertTrue(geneList.contains(GHR));
    Gene PPP1R17 = new Gene(TermId.of("NCBIGene:10842"), "PPP1R17");
    assertTrue(geneList.contains(PPP1R17));
    Gene EPHX2 = new Gene(TermId.of("NCBIGene:2053"), "EPHX2");
    assertTrue(geneList.contains(EPHX2));
    Gene ABCA1 = new Gene(TermId.of("NCBIGene:19"), "ABCA1");
    assertTrue(geneList.contains(ABCA1));
    Gene LDLR = new Gene(TermId.of("NCBIGene:3949"), "LDLR");
    assertTrue(geneList.contains(LDLR));
  }

  @Test
  void testTermToGene() throws PhenolException {
    // This test map should come from {@link HpoDiseaseAnnotationParser}
    TermId familialHypercholesterolemia = TermId.of("OMIM:143890");
    TermId fakePhenotype = TermId.of("HP:0000118");
    Multimap<TermId, TermId> testMap = ArrayListMultimap.create();
    testMap.put(fakePhenotype, familialHypercholesterolemia);
    parser.setTermToGene(testMap);
    assertEquals(parser.getPhenotypeToGene().size(), 7);
  }

  @Test
  void testDiseasetoGene() {
    Multimap<TermId, TermId> diseaseMap = parser.getDiseaseToGeneIdMap();
    Collection<TermId> genes = diseaseMap.get(TermId.of("OMIM:143890"));
    assertEquals(genes.size(), 7);
  }


  @Test
  void testGeneIdToGeneSymbol(){
    Map<TermId, String> geneMap = parser.getGeneIdToSymbolMap();
    assertEquals(geneMap.get(TermId.of("NCBIGene:2690")),"GHR");
    assertEquals(geneMap.get(TermId.of("NCBIGene:2200")),"FBN1");
  }



}
