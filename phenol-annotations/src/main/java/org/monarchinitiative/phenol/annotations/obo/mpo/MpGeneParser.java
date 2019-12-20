package org.monarchinitiative.phenol.annotations.obo.mpo;

import com.google.common.collect.ImmutableMap;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.annotations.formats.mpo.MpGene;
import org.monarchinitiative.phenol.annotations.formats.mpo.MpGeneModel;
import org.monarchinitiative.phenol.annotations.formats.mpo.MpSimpleModel;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.io.*;
import java.util.*;

import static org.monarchinitiative.phenol.annotations.formats.mpo.MpGene.createMpGene;


/**
 * Parses the MRK_List2.rpt file.
 * The file is List of Mouse Genetic Markers (sorted alphabetically by marker symbol, tab-delimited)
 * The List2 version excludes withdrawn symbols
 * The
 */
public class MpGeneParser {
  /** Path to the MRK_List2.rpt file from MGI. */
  private final String mgiMarkerPath;
  /** Path to the MGI_GenePheno.rpt file from MGI.*/
  private final String mgiGenePhenoPath;
  /** THe MPO ontology object. */
  private final Ontology ontology;

  public MpGeneParser(String markerPath, String mgiGenePhenoPath, String ontologypath) throws PhenolException {
    this(markerPath, mgiGenePhenoPath, OntologyLoader.loadOntology(new File(ontologypath)));
  }

  public MpGeneParser(String markerPath, String mgiGenePhenoPath, Ontology mpo) {
    this.mgiMarkerPath = markerPath;
    this.mgiGenePhenoPath = mgiGenePhenoPath;
    this.ontology = mpo;
  }

  public Ontology getMpOntology() { return ontology; }

  /**
   * Reads the file of genetic markers. For each genetic marker, extracts the full MGI Accession ID,
   * the Marker Symbol, and Marker Type.
   * @throws IOException if the file cannot be read
   * @return ImmutableGenes object holding all the genes read from file
   */
  public Map<TermId, MpGene> parseMarkers() throws IOException, PhenolException {
    ImmutableMap.Builder<TermId, MpGene> bld = ImmutableMap.builder();
    BufferedReader br = new BufferedReader(new FileReader(mgiMarkerPath));
    // skip over first line of file, which is a header line
    String line = br.readLine();
    while ((line=br.readLine()) != null) {
      String[] fields = line.split("\t");
      // first field is MGI Accession ID, seventh is Marker Symbol, tenth is Marker Type
      //String mgiId = fields[0];
      TermId mgiId = TermId.of(fields[0]);
        bld.put(mgiId, createMpGene(mgiId, fields[6], fields[9]));
    }
    br.close();
    return bld.build();
  }

  public Map<TermId,MpGeneModel> parseMpGeneModels() {
    Map<TermId,List<MpSimpleModel>> gene2simpleMap=new HashMap<>();
    ImmutableMap.Builder<TermId,MpGeneModel> builder = new ImmutableMap.Builder<>();
    try {
      MpAnnotationParser annotParser = new MpAnnotationParser(this.mgiGenePhenoPath);
      if (annotParser.getParsedAnnotationCount()==0) {
        for (String e:annotParser.getParseErrors()) {
          System.err.println(e);
        }
        throw new PhenolRuntimeException("Could not parse " + mgiGenePhenoPath);
      }
      Map<TermId, MpSimpleModel> simpleModelMap = annotParser.getGenotypeAccessionToMpModelMap();
      for (MpSimpleModel simplemod : simpleModelMap.values()) {
        TermId geneId = simplemod.getMarkerId();
        gene2simpleMap.putIfAbsent(geneId,new ArrayList<>());
        List<MpSimpleModel> lst = gene2simpleMap.get(geneId);
        lst.add(simplemod);
      }
      // when we get here, the simpleModelMap has key-a gene ID, value-collection of
      // all simple models that have a knockout of the corresponding gene
      for (TermId geneId : gene2simpleMap.keySet()) {
        List<MpSimpleModel> modCollection = gene2simpleMap.get(geneId);
          MpGeneModel genemod = new MpGeneModel(geneId, modCollection);
          builder.put(geneId,genemod);
      }
    } catch (PhenolException e) {
      e.printStackTrace();
    }

    return builder.build();
  }


}
