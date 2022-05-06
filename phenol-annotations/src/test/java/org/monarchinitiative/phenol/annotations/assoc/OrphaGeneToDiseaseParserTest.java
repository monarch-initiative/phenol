package org.monarchinitiative.phenol.annotations.assoc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.annotations.formats.hpo.DiseaseToGeneAssociations;
import org.monarchinitiative.phenol.annotations.formats.hpo.GeneToAssociation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;
import java.util.*;

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

  private static Map<TermId, Collection<GeneIdentifier>> orphaId2GeneMultimap;

  @BeforeAll
  public static void init() throws Exception {
    Path homoSapiensGeneInfo = Path.of(OrphaGeneToDiseaseParserTest.class.getResource("/Homo_sapiens.gene_info.excerpt.gz").toURI());
    GeneIdentifiers geneIdentifiers = HumanGeneInfoLoader.loadGeneIdentifiers(homoSapiensGeneInfo, GeneInfoGeneType.DEFAULT);

    Path mim2gene = Path.of(OrphaGeneToDiseaseParserTest.class.getResource("/mim2gene_medgen.excerpt").toURI());
    DiseaseToGeneAssociations mim2Gene = Mim2GeneMedgenLoader.loadDiseaseToGeneAssociations(mim2gene, geneIdentifiers);
    Map<String, Collection<TermId>> diseaseIdToGeneId = new HashMap<>();
    for (Map.Entry<TermId, Collection<GeneToAssociation>> e : mim2Gene.diseaseIdToGeneAssociations().entrySet()) {
      List<TermId> genes = new ArrayList<>(e.getValue().size());
      for (GeneToAssociation association : e.getValue()) {
        genes.add(association.geneIdentifier().id());
      }
      diseaseIdToGeneId.put(e.getKey().getId(), genes);
    }

    Path orphanet = Path.of(OrphaGeneToDiseaseParserTest.class.getResource("/orphanet_disease2gene_en_product6_head.xml").toURI());
    orphaId2GeneMultimap = OrphaGeneToDiseaseParser.parseOrphaGeneXml(orphanet, diseaseIdToGeneId, geneIdentifiers);
  }

  /**
   *
   * There are four diseases in our test file (we get the set of disease Ids with keySet).
   */
  @Test
  public void testDiseaseCount() {
    int expected = 4;
    assertEquals(expected, orphaId2GeneMultimap.keySet().size());
  }

  @Test
  public void testMultipleEpiphysealDysplasia() {
    TermId medId = TermId.of(ORPHA_PREFIX, "166024");
    assertTrue(orphaId2GeneMultimap.containsKey(medId));
    Collection<GeneIdentifier> geneIdentifiers = orphaId2GeneMultimap.get(medId);
    assertEquals(1, geneIdentifiers.size());
    String expectedGeneSymbol = "KIF7";
    assertEquals(expectedGeneSymbol, geneIdentifiers.iterator().next().symbol());
  }

  @Test
  public void testBrachydactyly() {
    TermId brachydactylyId = TermId.of(ORPHA_PREFIX, "166035");
    assertTrue(orphaId2GeneMultimap.containsKey(brachydactylyId));
    Collection<GeneIdentifier> geneIdentifiers = orphaId2GeneMultimap.get(brachydactylyId);
    assertEquals(1, geneIdentifiers.size());
    String expectedGeneSymbol = "CWC27";
    assertEquals(expectedGeneSymbol, geneIdentifiers.iterator().next().symbol());
  }

  @Test
  public void testAspartylglucosaminuria() {
    TermId aspartylglucosaminuriaId = TermId.of(ORPHA_PREFIX, "93");
    assertTrue(orphaId2GeneMultimap.containsKey(aspartylglucosaminuriaId));
    Collection<GeneIdentifier> geneIdentifiers = orphaId2GeneMultimap.get(aspartylglucosaminuriaId);
    assertEquals(1, geneIdentifiers.size());
    String expectedGeneSymbol = "AGA";
    assertEquals(expectedGeneSymbol, geneIdentifiers.iterator().next().symbol());
  }


  @Test
  public void testFamilialSickSinusSyndrome() {
    TermId familialSSS = TermId.of(ORPHA_PREFIX, "166282");
    assertTrue(orphaId2GeneMultimap.containsKey(familialSSS));
    Collection<GeneIdentifier> geneIdentifiers = orphaId2GeneMultimap.get(familialSSS);
    assertEquals(3, geneIdentifiers.size());
    // This disease is associated with the following three genes
    assertTrue(geneIdentifiers.stream().map(GeneIdentifier::symbol).anyMatch(s -> s.equals("SCN5A")));
    assertTrue(geneIdentifiers.stream().map(GeneIdentifier::symbol).anyMatch(s -> s.equals("HCN4")));
    assertTrue(geneIdentifiers.stream().map(GeneIdentifier::symbol).anyMatch(s -> s.equals("MYH6")));
  }

}
