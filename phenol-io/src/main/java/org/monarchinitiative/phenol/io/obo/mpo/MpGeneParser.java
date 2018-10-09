package org.monarchinitiative.phenol.io.obo.mpo;

import com.google.common.collect.ImmutableMap;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.mpo.MpGene;
import org.monarchinitiative.phenol.formats.mpo.MpGeneModel;
import org.monarchinitiative.phenol.formats.mpo.MpSimpleModel;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.monarchinitiative.phenol.formats.mpo.MpGene.createMpGene;


/**
 * Parses the MRK_List2.rpt file.
 * The file is List of Mouse Genetic Markers (sorted alphabetically by marker symbol, tab-delimited)
 * The List2 version excludes withdrawn symbols
 * @author Hannah Blau (blauh)
 * @version 0.0.2
 * @since 27 Aug 2018
 */
public class MpGeneParser {
  //private static final Logger logger = LogManager.getLogger();
  private final String mgiMarkerPath;

  private final String mgiGenePhenoPath;
  /** THe MPO ontology object. */
  private final Ontology ontology;

  public MpGeneParser(String markerPath, String MGI_GenePhenoPath, String ontologypath) {
    mgiMarkerPath = markerPath;
    mgiGenePhenoPath=MGI_GenePhenoPath;
    this.ontology=parseMpo(ontologypath);
    //logger.trace("Genetic markers path = " + path);
  }

  public MpGeneParser(String markerPath, String MGI_GenePhenoPath,Ontology mpo) {
    this.mgiMarkerPath=markerPath;
    this.mgiGenePhenoPath=MGI_GenePhenoPath;
    this.ontology=mpo;
  }

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
      TermId mgiId = TermId.constructWithPrefix(fields[0]);
        bld.put(mgiId, createMpGene(mgiId, fields[6], fields[9]));
    }
    br.close();
    return bld.build();
  }

  private Ontology parseMpo(String path) {
    Ontology mpo=null;
    try {
      MpOboParser parser = new MpOboParser(path);
      mpo=parser.parse();
    } catch (FileNotFoundException | PhenolException e) {
      e.printStackTrace();
    }
    return mpo;
  }

  public Map<TermId,MpGeneModel> parseMpGeneModels() {
    Map<TermId,List<MpSimpleModel>> gene2simpleMap=new HashMap<>();
    ImmutableMap.Builder<TermId,MpGeneModel> builder = new ImmutableMap.Builder<>();
    try {
      MpAnnotationParser annotParser = new MpAnnotationParser(this.mgiGenePhenoPath);
      Map<TermId, MpSimpleModel> simpleModelMap = annotParser.getGenotypeAccessionToMpModelMap();
      for (MpSimpleModel simplemod : simpleModelMap.values()) {
        TermId geneId = simplemod.getGenotypeId();
        gene2simpleMap.putIfAbsent(geneId,new ArrayList<>());
        List<MpSimpleModel> lst = gene2simpleMap.get(geneId);
        lst.add(simplemod);
      }
      // when we get here, the simpleModelMap has key-a gene ID, value-collection of
      // all simple models that have a knockout of the corresponding gene
      for (TermId geneId : gene2simpleMap.keySet()) {
        List<MpSimpleModel> modCollection = gene2simpleMap.get(geneId);
          MpGeneModel genemod = new MpGeneModel(geneId,ontology,modCollection);
          builder.put(geneId,genemod);
      }
    } catch (PhenolException e) {
      e.printStackTrace();
    }

    return builder.build();
  }


}
