package org.monarchinitiative.phenol.annotations.assoc;

import com.google.common.collect.Multimap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.formats.Gene;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrphaGeneToDiseaseParserTest {

  // Get XML file from
  //http://www.orphadata.org/data/xml/en_product6.xml
  // Our test file has the first three entries
  // <OrphaNumber>166024</OrphaNumber>
  //      <Name lang="en">Multiple epiphyseal dysplasia, Al-Gazali type</Name>
  //<Gene id="20160">
  //            <OrphaNumber>268061</OrphaNumber>
  //            <Name lang="en">kinesin family member 7</Name>
  //            <Symbol>KIF7</Symbol>
  // <Disorder id="17604">
  //      <OrphaNumber>166035</OrphaNumber>
  //      <Name lang="en">Brachydactyly-short stature-retinitis pigmentosa syndrome</Name>
  //<Gene id="26792">
  //            <OrphaNumber>510666</OrphaNumber>
  //            <Name lang="en">CWC27 spliceosome associated protein homolog</Name>
  //            <Symbol>CWC27</Symbol>
  // <Disorder id="5">
  //      <OrphaNumber>93</OrphaNumber>
  //      <Name lang="en">Aspartylglucosaminuria</Name>
  //<Symbol>AGA</Symbol>
  // and
  //  <Disorder id="17628">
  //      <OrphaNumber>166282</OrphaNumber>
  //      <Name lang="en">Familial sick sinus syndrome</Name>
  //      <GeneList count="3">
  // with SCN5A, HCN4 and MYH6
  private static final String ORPHA_PREFIX = "ORPHA";

  private static Multimap<TermId, Gene> orphaId2GeneMultimap;

  @BeforeAll
  private static void init() throws IOException {
    final String mim2genePath = "mim2gene_medgen.excerpt";
    ClassLoader classLoader = Gene2DiseaseAsssociationParserTest.class.getClassLoader();
    URL mim2geneURL = classLoader.getResource(mim2genePath);
    if (mim2geneURL == null) {
      throw new IOException("Could not find mim2gene_medgen at " + mim2genePath);
    }
    final String orphanetPath = "orphanet_disease2gene_en_product6_head.xml";
    URL orphanetURL = classLoader.getResource(orphanetPath);
    if (orphanetURL == null) {
      throw new IOException("Could not find orphanet_disease2gene_en_product6_head.xml at " + orphanetPath);
    }
    OrphaGeneToDiseaseParser parser = new OrphaGeneToDiseaseParser(new File(orphanetURL.getFile()),
            new File(mim2geneURL.getFile()));
    orphaId2GeneMultimap = parser.getOrphaDiseaseToGeneSymbolMap();
  }

  /**
   *
   * There are four diseases in our test file (we get the set of disease Ids with keySet).
   */
  @Test
  void testDiseaseCount() {
    int expected = 4;
    assertEquals(expected, orphaId2GeneMultimap.keySet().size());
  }

  @Test
  void testMultipleEpiphysealDysplasia() {
    TermId medId = TermId.of(ORPHA_PREFIX, "166024");
    assertTrue(orphaId2GeneMultimap.containsKey(medId));
    Collection<Gene> genes = orphaId2GeneMultimap.get(medId);
    assertEquals(1, genes.size());
    String expectedGeneSymbol = "KIF7";
    assertEquals(expectedGeneSymbol, genes.iterator().next().getSymbol());
  }

  @Test
  void testBrachydactyly() {
    TermId brachydactylyId = TermId.of(ORPHA_PREFIX, "166035");
    assertTrue(orphaId2GeneMultimap.containsKey(brachydactylyId));
    Collection<Gene> genes = orphaId2GeneMultimap.get(brachydactylyId);
    assertEquals(1, genes.size());
    String expectedGeneSymbol = "CWC27";
    assertEquals(expectedGeneSymbol, genes.iterator().next().getSymbol());
  }

  @Test
  void testAspartylglucosaminuria() {
    TermId aspartylglucosaminuriaId = TermId.of(ORPHA_PREFIX, "93");
    assertTrue(orphaId2GeneMultimap.containsKey(aspartylglucosaminuriaId));
    Collection<Gene> genes = orphaId2GeneMultimap.get(aspartylglucosaminuriaId);
    assertEquals(1, genes.size());
    String expectedGeneSymbol = "AGA";
    assertEquals(expectedGeneSymbol, genes.iterator().next().getSymbol());
  }


  @Test
  void testFamilialSickSinusSyndrome() {
    TermId familialSSS = TermId.of(ORPHA_PREFIX, "166282");
    assertTrue(orphaId2GeneMultimap.containsKey(familialSSS));
    Collection<Gene> genes = orphaId2GeneMultimap.get(familialSSS);
    assertEquals(3, genes.size());
    // This disease is associated with the following three genes
    assertTrue(genes.stream().map(Gene::getSymbol).anyMatch(s -> s.equals("SCN5A")));
    assertTrue(genes.stream().map(Gene::getSymbol).anyMatch(s -> s.equals("HCN4")));
    assertTrue(genes.stream().map(Gene::getSymbol).anyMatch(s -> s.equals("MYH6")));
  }

}
