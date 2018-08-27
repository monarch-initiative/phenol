package org.monarchinitiative.phenol.io.obo.mpo;

import com.google.common.collect.ImmutableMap;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.mpo.MpGene;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

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
  private final String markersPath;

  public MpGeneParser(String path) {
    markersPath = path;
    //logger.trace("Genetic markers path = " + path);
  }

  /**
   * Reads the file of genetic markers. For each genetic marker, extracts the full MGI Accession ID,
   * the Marker Symbol, and Marker Type.
   * @throws IOException if the file cannot be read
   * @return ImmutableGenes object holding all the genes read from file
   */
  public Map<TermId, MpGene> parseMarkers() throws IOException, PhenolException {
    ImmutableMap.Builder<TermId, MpGene> bld = ImmutableMap.builder();
    BufferedReader br = new BufferedReader(new FileReader(markersPath));
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


}
