package org.monarchinitiative.phenol.annotations.assoc;

import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * This class parses the Orphanet file that contains the disease to gene associations (en_product6.xml).
 * For now, we parse out the gene symbol but the Orphanet file contains a number of
 * IDs (but not, unfortunately, EntrezGene ids).
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class OrphaGeneToDiseaseParser {
  private final static Logger LOGGER = LoggerFactory.getLogger(OrphaGeneToDiseaseParser.class);
  private static final String DISORDER = "Disorder";
  private static final String ORPHA_NUMBER = "OrphaCode";
  private static final String GENE_LIST = "GeneList";
  private static final String GENE = "Gene";
  private static final String NAME = "Name";
  private static final String DISORDER_GENE_ASSOCIATION_LIST = "DisorderGeneAssociationList";
  private static final String DISORDER_GENE_ASSOCIATION = "DisorderGeneAssociation";
  private static final String SYMBOL = "Symbol";
  private static final String EXTERNAL_REFERENCE = "ExternalReference";
  private static final String REFERENCE = "Reference";
  private static final String SOURCE = "Source";


  /**
   * @return map with key: e.g., ORPHA:163746 (for Peripheral demyelinating neuropathy-central dysmyelinating
   * leukodystrophy-Waardenburg syndrome-Hirschsprung disease).
   * value: corresponding gene symbol(s), e.g., SOX10
   */
  public static Map<TermId, Collection<GeneIdentifier>> parseOrphaGeneXml(Path orphaGeneXMLfile,
                                                                          Map<String, Collection<TermId>> mimIdToGeneIdMap,
                                                                          Map<String, GeneIdentifier> symbolToGeneId) {

    boolean inDisorder = false;
    boolean inGeneList = false;
    boolean inGene = true;
    boolean inDisorderGeneAssociation = false;
    boolean inDisorderGeneAssociationList = false;
    boolean inExternalReference = false;
    boolean inOMIMReference = false;

    try (InputStream in = Files.newInputStream(orphaGeneXMLfile)) {
      Map<TermId, Collection<GeneIdentifier>> orphaDiseaseToGeneBuilder = new HashMap<>();

      XMLEventReader eventReader = XMLInputFactory.newInstance().createXMLEventReader(in);
      String currentOrphanum = null;
      String currentDiseasename = null;
      String currentGeneSymbol = null;
      String currentOmimId = null; // use this together with mimIdToGeneIdMap to get GeneId


      while (eventReader.hasNext()) {
        XMLEvent event = eventReader.nextEvent();
        if (event.isStartElement()) {
          final String localPart = event.asStartElement().getName().getLocalPart();
          if (inGeneList) continue; // skip all of the gene list element
          if (localPart.equals(DISORDER)) {
            inDisorder = true;
          } else if (inDisorder &&
            !inGeneList &&
            !inDisorderGeneAssociationList &&
            localPart.equals(ORPHA_NUMBER)) {
            event = eventReader.nextEvent();
            currentOrphanum = event.asCharacters().getData();
          } else if (inDisorder &&
            !inGeneList &&
            !inDisorderGeneAssociationList &&
            localPart.equals(NAME)) {
            event = eventReader.nextEvent();

            currentDiseasename = event.asCharacters().getData();

          } else if (inDisorder &&
            !inGeneList &&
            !inDisorderGeneAssociationList &&
            localPart.equals(GENE_LIST)) {
            inGeneList = true;
          } else if (localPart.equals(DISORDER_GENE_ASSOCIATION_LIST)) {
            inDisorderGeneAssociationList = true;
          } else if (inDisorderGeneAssociationList &&
            localPart.equals(DISORDER_GENE_ASSOCIATION)) {
            inDisorderGeneAssociation = true;
          } else if (inDisorderGeneAssociation &&
            localPart.equals(GENE)) {
            inGene = true;
          } else if (inDisorderGeneAssociation &&
            localPart.equals(EXTERNAL_REFERENCE)) {
            inExternalReference = true;
          } else if (inExternalReference &&
            localPart.equals(SOURCE)) {
            event = eventReader.nextEvent();
            String sourceString = event.asCharacters().getData();
            if (sourceString.equals("OMIM")) {
              inOMIMReference = true;
            }
          } else if (inOMIMReference &&
            localPart.equals(REFERENCE)) {
            event = eventReader.nextEvent();
            currentOmimId = event.asCharacters().getData();
          }  else if (inDisorder &&
            inDisorderGeneAssociation &&
            inDisorderGeneAssociationList &&
            inGene &&
            localPart.equals(SYMBOL)) {
            event = eventReader.nextEvent();
            currentGeneSymbol = event.asCharacters().getData();
          }
        } else if (event.isEndElement()) {
          EndElement endElement = event.asEndElement();
          String localPart =endElement.getName().getLocalPart();
          if (localPart.equals(DISORDER)) {
            inDisorder = false;
          } else if (localPart.equals(GENE_LIST)) {
            inGeneList = false;
          } else if (localPart.equals(EXTERNAL_REFERENCE)) {
            inExternalReference = false;
            inOMIMReference = false;
          } else if (localPart.equals(DISORDER_GENE_ASSOCIATION_LIST)) {
            inDisorderGeneAssociationList = false;
            currentOrphanum = null;
            currentOmimId = null;
            currentDiseasename = null; // clean up after we are done
          } else if (localPart.equals(DISORDER_GENE_ASSOCIATION)) {
            inDisorderGeneAssociation = false;
            // if we have data then we can enter it here
            if (currentOrphanum != null && currentDiseasename != null && currentGeneSymbol != null) {
              TermId orphaId = TermId.of("ORPHA", currentOrphanum);

              Collection<TermId> geneIds = mimIdToGeneIdMap.get(currentOmimId);
              if (currentOmimId == null || geneIds == null) {
                GeneIdentifier geneId = symbolToGeneId.get(currentGeneSymbol);
                if (geneId != null) {
                  orphaDiseaseToGeneBuilder.computeIfAbsent(orphaId, k -> new HashSet<>())
                    .add(geneId);
                } else {
                  LOGGER.warn("Could not find gene id for {}", currentGeneSymbol);
                }
              } else {
                for (TermId geneId : geneIds) {
                  GeneIdentifier g = GeneIdentifier.of(geneId, currentGeneSymbol);
                  orphaDiseaseToGeneBuilder.computeIfAbsent(orphaId, k -> new HashSet<>())
                    .add(g);
                }
              }
            }
            currentGeneSymbol = null; // reset
            currentOmimId = null; // reset
            // the disease can have multiple genes, so do not reset it here!
          } else if (endElement.getName().getLocalPart().equals(GENE)) {
            inGene = false;
          }
        }
      }

      return makeUnmodifiable(orphaDiseaseToGeneBuilder);
    } catch (IOException | XMLStreamException e) {
      throw new PhenolRuntimeException("Could not parse orpha disease to gene xml: " + e.getMessage());
    }
  }

  private static Map<TermId, Collection<GeneIdentifier>> makeUnmodifiable(Map<TermId, Collection<GeneIdentifier>> orphaDiseaseToGeneBuilder) {
    Map<TermId, Collection<GeneIdentifier>> orphaDiseaseToGene = new HashMap<>(orphaDiseaseToGeneBuilder.size(), .9f);

    for (Map.Entry<TermId, Collection<GeneIdentifier>> e : orphaDiseaseToGeneBuilder.entrySet()) {
      orphaDiseaseToGene.put(e.getKey(), List.copyOf(e.getValue()));
    }

    return Collections.unmodifiableMap(orphaDiseaseToGene);
  }

  /**
   * This is a hack to identify the gene ids for Orpha XML file entries that do not have an OMIM cross reference
   * Date: March 11, 2020.
   */
  private static Map<String, TermId> initMissingGeneIdMap() {
    Map<String, TermId> symbolToGeneIdMap = new HashMap<>();
    symbolToGeneIdMap.put("ATP5F1D", TermId.of("NCBIGene:513"));
    symbolToGeneIdMap.put("CHD1", TermId.of("NCBIGene:1105"));
    symbolToGeneIdMap.put("CD55", TermId.of("NCBIGene:1604"));
    symbolToGeneIdMap.put("DLST", TermId.of("NCBIGene:1743"));
    symbolToGeneIdMap.put("EPHB4", TermId.of("NCBIGene:2050"));
    symbolToGeneIdMap.put("HLA-DPA1", TermId.of("NCBIGene:3113"));
    symbolToGeneIdMap.put("IGH", TermId.of("NCBIGene:3492"));
    symbolToGeneIdMap.put("OPA2", TermId.of("NCBIGene:4977"));
    symbolToGeneIdMap.put("PIK3C2A", TermId.of("NCBIGene:5286"));
    symbolToGeneIdMap.put("ROS1", TermId.of("NCBIGene:6098"));
    symbolToGeneIdMap.put("TRA", TermId.of("NCBIGene:6955"));
    symbolToGeneIdMap.put("TRB", TermId.of("NCBIGene:6957"));
    symbolToGeneIdMap.put("TRD", TermId.of("NCBIGene:6964"));
    symbolToGeneIdMap.put("TRG", TermId.of("NCBIGene:6965"));
    symbolToGeneIdMap.put("USH1E", TermId.of("NCBIGene:7396"));
    symbolToGeneIdMap.put("RIPK1", TermId.of("NCBIGene:8737"));
    symbolToGeneIdMap.put("PLAA", TermId.of("NCBIGene:9373"));
    symbolToGeneIdMap.put("SH3PXD2A", TermId.of("NCBIGene:9644"));
    symbolToGeneIdMap.put("CWC27", TermId.of("NCBIGene:10283"));
    symbolToGeneIdMap.put("YME1L1", TermId.of("NCBIGene:10730"));
    symbolToGeneIdMap.put("RPL35", TermId.of("NCBIGene:11224"));
    symbolToGeneIdMap.put("RRAS2", TermId.of("NCBIGene:22800"));
    symbolToGeneIdMap.put("MRAS", TermId.of("NCBIGene:22808"));
    symbolToGeneIdMap.put("DUX4L1", TermId.of("NCBIGene:22947"));
    symbolToGeneIdMap.put("NCAPD3", TermId.of("NCBIGene:23310"));
    symbolToGeneIdMap.put("IGHV4-34", TermId.of("NCBIGene:28395"));
    symbolToGeneIdMap.put("IGHV3-21", TermId.of("NCBIGene:28444"));
    symbolToGeneIdMap.put("PUS7", TermId.of("NCBIGene:54517"));
    symbolToGeneIdMap.put("TBC1D8B", TermId.of("NCBIGene:54885"));
    symbolToGeneIdMap.put("TRPV6", TermId.of("NCBIGene:55503"));
    symbolToGeneIdMap.put("RNPC3", TermId.of("NCBIGene:55599"));
    symbolToGeneIdMap.put("SPG14", TermId.of("NCBIGene:57309"));
    symbolToGeneIdMap.put("SPG16", TermId.of("NCBIGene:57760"));
    symbolToGeneIdMap.put("RINT1", TermId.of("NCBIGene:60561"));
    symbolToGeneIdMap.put("GINGF2", TermId.of("NCBIGene:64644"));
    symbolToGeneIdMap.put("MMEL1", TermId.of("NCBIGene:79258"));
    symbolToGeneIdMap.put("GREB1L", TermId.of("NCBIGene:80000"));
    symbolToGeneIdMap.put("USP45", TermId.of("NCBIGene:85015"));
    symbolToGeneIdMap.put("CFAP300", TermId.of("NCBIGene:85016"));
    symbolToGeneIdMap.put("ALKBH8", TermId.of("NCBIGene:91801"));
    symbolToGeneIdMap.put("STRADA", TermId.of("NCBIGene:92335"));
    symbolToGeneIdMap.put("TIMM50", TermId.of("NCBIGene:92609"));
    symbolToGeneIdMap.put("DYT13", TermId.of("NCBIGene:93983"));
    symbolToGeneIdMap.put("GPRASP2", TermId.of("NCBIGene:114928"));
    symbolToGeneIdMap.put("TDRD9", TermId.of("NCBIGene:122402"));
    symbolToGeneIdMap.put("NAXE", TermId.of("NCBIGene:128240"));
    symbolToGeneIdMap.put("TRIM71", TermId.of("NCBIGene:131405"));
    symbolToGeneIdMap.put("SPG19", TermId.of("NCBIGene:140907"));
    symbolToGeneIdMap.put("WDR66", TermId.of("NCBIGene:144406"));
    symbolToGeneIdMap.put("POLR3H", TermId.of("NCBIGene:171568"));
    symbolToGeneIdMap.put("CFAP221", TermId.of("NCBIGene:200373"));
    symbolToGeneIdMap.put("RNU12", TermId.of("NCBIGene:267010"));
    symbolToGeneIdMap.put("NUTM2E", TermId.of("NCBIGene:283008"));
    symbolToGeneIdMap.put("DYT15", TermId.of("NCBIGene:317714"));
    symbolToGeneIdMap.put("UBAC2", TermId.of("NCBIGene:337867"));
    symbolToGeneIdMap.put("SPG24", TermId.of("NCBIGene:338090"));
    symbolToGeneIdMap.put("SCA25", TermId.of("NCBIGene:338435"));
    symbolToGeneIdMap.put("SPG25", TermId.of("NCBIGene:387583"));
    symbolToGeneIdMap.put("LIPT2", TermId.of("NCBIGene:387787"));
    symbolToGeneIdMap.put("SCA20", TermId.of("NCBIGene:407973"));
    symbolToGeneIdMap.put("SPG27", TermId.of("NCBIGene:414886"));
    symbolToGeneIdMap.put("SPG29", TermId.of("NCBIGene:619379"));
    symbolToGeneIdMap.put("SPG32", TermId.of("NCBIGene:724107"));
    symbolToGeneIdMap.put("SPG34", TermId.of("NCBIGene:724110"));
    symbolToGeneIdMap.put("NUTM2A", TermId.of("NCBIGene:728118"));
    symbolToGeneIdMap.put("NUTM2B", TermId.of("NCBIGene:729262"));
    symbolToGeneIdMap.put("SPG36", TermId.of("NCBIGene:791228"));
    symbolToGeneIdMap.put("SPG37", TermId.of("NCBIGene:100049159"));
    symbolToGeneIdMap.put("SPG38", TermId.of("NCBIGene:100049707"));
    symbolToGeneIdMap.put("DYT17", TermId.of("NCBIGene:100216344"));
    symbolToGeneIdMap.put("USH1H", TermId.of("NCBIGene:100271837"));
    symbolToGeneIdMap.put("SCA30", TermId.of("NCBIGene:100359393"));
    symbolToGeneIdMap.put("DYT21", TermId.of("NCBIGene:100885773"));
    symbolToGeneIdMap.put("USH1K", TermId.of("NCBIGene:101180907"));
    symbolToGeneIdMap.put("IL12A-AS1", TermId.of("NCBIGene:101928376"));
    symbolToGeneIdMap.put("SCA37", TermId.of("NCBIGene:103753527"));
    return symbolToGeneIdMap;
  }

}
