package org.monarchinitiative.phenol.annotations.obo.mpo;

import org.monarchinitiative.phenol.annotations.formats.mpo.MpGeneticMarker;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.io.*;
import java.util.*;

import static org.monarchinitiative.phenol.annotations.formats.mpo.MpGeneticMarker.createMpGeneticMarker;


/**
 * Parses the MRK_List2.rpt and MGI_GenePheno.rpt files.
 * MRK_List2.rpt is List of Mouse Genetic Markers (sorted alphabetically by marker symbol, tab-delimited)
 * The List2 version excludes withdrawn symbols
 */
public class MpGeneParser {

  private MpGeneParser() {
    // should not be used by client code
  }

  /**
   * Reads the file of genetic markers. For each genetic marker, extracts the full MGI Accession ID,
   * the Marker Symbol, and Marker Type.
   * @param markerPath Path to the MRK_List2.rpt file from MGI.
   * @return ImmutableGenes object holding all the genes read from file
   */
  public static Map<TermId, MpGeneticMarker> loadMarkerMap(String markerPath) {
    Map<TermId, MpGeneticMarker> builder = new HashMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(markerPath))) {
      String line = br.readLine(); // skip header line
      while ((line = br.readLine()) != null) {
        String[] fields = line.split("\t");
        if (fields.length<11) {
          throw new PhenolRuntimeException("Malformed line of MRK_List2.rpt file with less than 11 fields:" + line);
        }
        // first field is MGI Accession ID, seventh is Marker Symbol, tenth is Marker Type
        TermId mgiId = TermId.of(fields[0]);
        builder.put(mgiId, createMpGeneticMarker(mgiId, fields[6], fields[9], fields[10]));
      }
    } catch (IOException e) {
      System.err.println("Could not read MGI Marker file" + markerPath);
    }
    return Map.copyOf(builder);
  }



}
