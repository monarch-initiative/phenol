package org.monarchinitiative.phenol.io.obo.mpo;

import com.google.common.collect.ImmutableMap;
import org.monarchinitiative.phenol.formats.mpo.MpGene;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import static org.monarchinitiative.phenol.formats.mpo.MpGene.makeImmutableGene;


/**
 * Parses the MP_GENETIC_MARKERS_FILENAME file downloaded by {@code DownloadCommand}, creates an ImmutableGenes
 * object with the file contents.
 * @author Hannah Blau (blauh)
 * @version 0.0.1
 * @since 27 Dec 2017
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
     * //TODO let's put a TermId with MGI:123456 for the MpGenes (instead of string)
     */
    public Map<TermId, MpGene> parseMarkers() throws IOException {
        ImmutableMap.Builder<TermId, MpGene> bld = ImmutableMap.builder();
        BufferedReader br = new BufferedReader(new FileReader(markersPath));
        // skip over first line of file, which is a header line
        String line = br.readLine();
        while ((line=br.readLine()) != null) {
            String[] fields = line.split("\t");
            // first field is MGI Accession ID, seventh is Marker Symbol, tenth is Marker Type
            //String mgiId = fields[0];
            TermId mgiId = TermId.constructWithPrefix(fields[0]);
            bld.put(mgiId, makeImmutableGene(mgiId, fields[6], fields[9]));
        }
        br.close();
        return bld.build();
    }


}
