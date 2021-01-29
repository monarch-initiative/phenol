package org.monarchinitiative.phenol.annotations.assoc;

import com.google.common.collect.*;
import org.monarchinitiative.phenol.annotations.formats.Gene;
import org.monarchinitiative.phenol.annotations.formats.hpo.*;
import org.monarchinitiative.phenol.annotations.obo.hpo.HpoDiseaseAnnotationParser;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>This class parses the files {@code mim2gene_medgen}, available from
 * <a href="ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/mim2gene_medgen">ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/mim2gene_medgen</a>
 * as well as the file {@code Homo_sapiens_gene_info.gz}, available from
 * <a href="ftp://ftp.ncbi.nih.gov/gene/DATA/GENE_INFO/Mammalia/Homo_sapiens.gene_info.gz">
 *     ftp://ftp.ncbi.nih.gov/gene/DATA/GENE_INFO/Mammalia/Homo_sapiens.gene_info.gz</a>.
 * mim2gene_medgen contains the MIM number of diseases and EntrezGene number of genes associated with the disease;
 * The relevant lines of the file are marked with "phenotype". The Homo_sapiens_gene_info.gz file contains the  entrez gene
 * number of genes as well as their gene symbol. </p>
 * <p>The goal of this class is to provide associations <br><br>
 *
 *   geneIdToSymbol - Key: EntrezGeneId [{@link TermId}] , Value: EntrezGeneSymbol-String <br>
 *   associationMap - Key: DiseaseId [{@link TermId}] , Value: GeneToAssociation [{@link GeneToAssociation}]<br>
 *   diseaseToGeneMap - Key: DiseaseId [{@link TermId}] , Value: GeneId [{@link TermId}]<br>
 *   geneToDiseaseMap - Key: GeneId [{@link TermId}] , Value: Disease [{@link TermId}]<br>
 *   diseaseToAssociations - Key: DiseaseId [{@link TermId}] , Value: List of genes [{@link DiseaseToGeneAssociation}],<br>
 *   termToDisease - Key: phenotypeId {@link TermId} , Value: Disease [{@link HpoDisease}]  FROM {@link HpoDiseaseAnnotationParser}<br>
 *   phenotypeToGeneList - List of {@link HpoGeneAnnotation} generated from linking termToDisease, diseaseToGene<br>
 *   associationList - List of all {@link DiseaseToGeneAssociation}<br>
 *
 * </p>
 * <a href="mailto:michael.gargano@jax.org">Michael Gargano</a>
 * <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class HpoAssociationParser {

  private final Ontology hpoOntology;
  /** Path to Homo_sapiens.gene_info.gz */
  private final File homoSapiensGeneInfoFile;
  /** Path to mim2gene_medgen. */
  private final File mim2geneMedgenFile;
  /** File representing Orphanet's gene file, en_product6.xml.*/
  private final File orphaToGeneFile;
  /** File representing the phenotype.hpoa file */
  private final File phenotypeDotHpoaFile;
  /** Key--an EntrezGene id; value--the corresponding symbol. */
  private Map<TermId, String> geneIdToSymbolMap;
  /** Key: an OMIM curie (e.g., OMIM:600100); value--corresponding GeneToAssociation object). For instance,
   * MICROVASCULAR COMPLICATIONS OF DIABETES, SUSCEPTIBILITY TO, 1; is associated to the gene VEGF as POLYGENIC,
   * and MARFAN SYNDROME is associated to the gene FBN1 as MENDELIAN.*/
  //private ImmutableMultimap<TermId,GeneToAssociation> associationMap;
  /** Key: a disease Id; Value: a geneId */
  private ImmutableMultimap<TermId,TermId> diseaseToGeneMap;
  /** Key: a gene Id; Value: a diseaseId */
  private ImmutableMultimap<TermId,TermId> geneToDiseaseMap;
  /** Key: a disease Id; Value:  disease obj, all gene associations. */
  private ImmutableMap<TermId, DiseaseToGeneAssociation> diseaseToAssociationsMap;
  /** Key: a phenotype Id; Value: geneId */
  private ImmutableList<HpoGeneAnnotation> phenotypeToGeneList;
  /** List of all associations */
  private List<DiseaseToGeneAssociation> associationList;

  public HpoAssociationParser(String geneInfoPath,
                              String mim2geneMedgenPath,
                              String orphaToGenePath,
                              String phenotypeHpoaPath,
                              Ontology hpoOntology){
    this.hpoOntology = hpoOntology;
    this.homoSapiensGeneInfoFile = new File(geneInfoPath);
    this.mim2geneMedgenFile = new File(mim2geneMedgenPath);
    this.orphaToGeneFile = new File(orphaToGenePath);
    this.phenotypeDotHpoaFile = new File(phenotypeHpoaPath);

    ingestDisease2GeneAssociations();
    ingestPhenotypeHpoaFile();
  }

  /** Parse everything except the Orphanet data!.*/
  public HpoAssociationParser(String geneInfoPath, String mim2geneMedgenPath, Ontology hpoOntology){
    this.hpoOntology = hpoOntology;
    this.homoSapiensGeneInfoFile = new File(geneInfoPath);
    this.mim2geneMedgenFile = new File(mim2geneMedgenPath);
    this.orphaToGeneFile = null;
    this.phenotypeDotHpoaFile = null;
    ingestDisease2GeneAssociations();
  }

  /**
   * Use this constructor to parse everything except the Orphanet to gene file.
   * @param geneInfoFile
   * @param mim2geneMedgenFile
   * @param phenotypeHpoaFile
   * @param hpoOntology
   */
  public HpoAssociationParser(File geneInfoFile,
                              File mim2geneMedgenFile,
                              File phenotypeHpoaFile,
                              Ontology hpoOntology){
    this.hpoOntology = hpoOntology;
    this.homoSapiensGeneInfoFile = geneInfoFile;
    this.mim2geneMedgenFile = mim2geneMedgenFile;
    this.orphaToGeneFile = null;
    this.phenotypeDotHpoaFile = phenotypeHpoaFile;
    // The following skips the orphaToGeneFile to gene file because it is null
    // TODO this class is not elegant, it needs refactoring.
    ingestDisease2GeneAssociations();
    ingestPhenotypeHpoaFile();
  }

  public HpoAssociationParser(File geneInfoFile,
                              File mim2geneMedgenFile,
                              File orphaToGeneFile,
                              File phenotypeHpoaFile,
                              Ontology hpoOntology){
    this.hpoOntology = hpoOntology;
    this.homoSapiensGeneInfoFile = geneInfoFile;
    this.mim2geneMedgenFile = mim2geneMedgenFile;
    this.orphaToGeneFile = orphaToGeneFile;
    this.phenotypeDotHpoaFile = phenotypeHpoaFile;

    ingestDisease2GeneAssociations();
    ingestPhenotypeHpoaFile();
  }

  /**
   * Ingest the phenotype.hpoa file. This will population {@link #phenotypeToGeneList}.
   */
  private void ingestPhenotypeHpoaFile() {
    if (! phenotypeDotHpoaFile.exists()) {
      throw new PhenolRuntimeException("Cannot find phenotype.hpoa file");
    }
    List<String> desiredDatabasePrefixes=ImmutableList.of("OMIM");
    Map<TermId, HpoDisease> diseaseMap = HpoDiseaseAnnotationParser.loadDiseaseMap(this.phenotypeDotHpoaFile.getAbsolutePath(),
      hpoOntology,
      desiredDatabasePrefixes);
    Multimap<TermId, TermId> phenotypeToDisease = ArrayListMultimap.create();
    for (Map.Entry<TermId,HpoDisease> entry : diseaseMap.entrySet()) {
      for (HpoAnnotation hpoAnnot : entry.getValue().getPhenotypicAbnormalities()) {
        TermId hpoId = hpoAnnot.getTermId();
        phenotypeToDisease.put(hpoId,entry.getKey()); // diseaseId to HPO id multimpa
      }
    }
    setTermToGene(phenotypeToDisease);
  }



  public Map<TermId,DiseaseToGeneAssociation> getDiseaseToAssociationsMap() { return this.diseaseToAssociationsMap; }

  public Map<TermId,String> getGeneIdToSymbolMap() { return this.geneIdToSymbolMap;}

  public Multimap<TermId,TermId> getDiseaseToGeneIdMap() { return this.diseaseToGeneMap; }

  public Multimap<TermId,TermId> getGeneToDiseaseIdMap() { return this.geneToDiseaseMap; }

  public List<HpoGeneAnnotation> getPhenotypeToGene() { return this.phenotypeToGeneList; }

  /*
   * Builds a list of HpoGeneAnnotations, which are just an object that represents a relationship
   * from Gene to HP Term.
   *  @param phenotypeToDisease: Map of PhenotypeID's to DiseaseID's
   */
  public void setTermToGene(Multimap<TermId, TermId> phenotypeToDisease) {

    if(this.diseaseToGeneMap.isEmpty()){
      throw new PhenolRuntimeException("Error: Associations not parsed. Please call parse then set the term to gene mapping.");
    }

    ImmutableList.Builder<HpoGeneAnnotation> builderGeneAnnotationList = new ImmutableList.Builder<>();

    for(TermId phenotype : phenotypeToDisease.keySet()){
     Set<TermId> mappedGenes = new HashSet<>();
     phenotypeToDisease.get(phenotype).stream()
        .flatMap(disease -> this.diseaseToGeneMap.get(disease).stream()).collect(Collectors.toList()).forEach((gene) -> {
          try {
            int entrezId = Integer.parseInt(gene.getId());
            if(!mappedGenes.contains(gene)){
              String entrezGeneSymbol = this.geneIdToSymbolMap.get(gene);
              if(entrezGeneSymbol == null){
                entrezGeneSymbol = "-";
              }
              String hpoTermName = hpoOntology.getTermMap().get(phenotype).getName();
              HpoGeneAnnotation geneAnnotation = new HpoGeneAnnotation(entrezId, entrezGeneSymbol, hpoTermName, phenotype);
              builderGeneAnnotationList.add(geneAnnotation);
              mappedGenes.add(gene);
            }
          }catch(Exception e){
            System.err.println("[ERROR] setTermToGene encountered an exception: " + e.getMessage() +
              " for gene: " + gene.toString());
          }
        });
    }
    this.phenotypeToGeneList = builderGeneAnnotationList.build();
  }

  /*
      Generate and set all the bla32 maps
      Disease -> Gene Entire Object
      Multimap DiseaseId to GeneId
      Multimap GeneId -> DiseaseID
   */
  private void setAssociationMaps(){
    Multimap<TermId, TermId> geneToDisease = ArrayListMultimap.create();
    ImmutableMap.Builder<TermId,DiseaseToGeneAssociation> builderDiseasetoAssociation = new ImmutableMap.Builder<>();

    for (DiseaseToGeneAssociation g2p : associationList) {
      TermId diseaseId = g2p.getDiseaseId();
      List<Gene> geneList = g2p.getGeneList();
      builderDiseasetoAssociation.put(diseaseId, g2p);
      for (Gene g: geneList) {
        TermId geneId = g.getId();
        if(!geneToDisease.containsEntry(geneId, diseaseId)){
          geneToDisease.put(geneId, diseaseId);
        }
      }
    }

    ImmutableMultimap.Builder<TermId,TermId> builderGeneToDisease = new ImmutableMultimap.Builder<>();
    builderGeneToDisease.putAll(geneToDisease);
    this.geneToDiseaseMap = builderGeneToDisease.build();
    this.diseaseToGeneMap = builderGeneToDisease.build().inverse();
    this.diseaseToAssociationsMap = builderDiseasetoAssociation.build();
  }

  /**
   * Parse data from mim2gene_medgen, Gene Info, and Orphanet to get a list of gene to symbol and
   * gene to disease annotations.
   */
  private void ingestDisease2GeneAssociations() {
    Gene2DiseaseAssociationParser parser;
    if (this.orphaToGeneFile != null) {
      parser = new Gene2DiseaseAssociationParser(this.homoSapiensGeneInfoFile,
        this.mim2geneMedgenFile,
        this.orphaToGeneFile);
    } else {
      parser = new Gene2DiseaseAssociationParser(this.homoSapiensGeneInfoFile,
        this.mim2geneMedgenFile);
    }
    Multimap<TermId, GeneToAssociation> associationMap = parser.getAssociationMap();
    ImmutableList.Builder<DiseaseToGeneAssociation> builder = new ImmutableList.Builder<>();
    for (TermId omimCurie : associationMap.keySet()) {
      Collection<GeneToAssociation> g2aList = associationMap.get(omimCurie);
      DiseaseToGeneAssociation g2p = new DiseaseToGeneAssociation(omimCurie, ImmutableList.copyOf(g2aList));
      builder.add(g2p);
    }
    this.associationList = builder.build();
    this.geneIdToSymbolMap = parser.getGeneIdToSymbolMap();
    this.setAssociationMaps();
  }
}
