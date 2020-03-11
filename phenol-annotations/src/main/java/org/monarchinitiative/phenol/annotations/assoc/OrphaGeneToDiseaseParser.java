package org.monarchinitiative.phenol.annotations.assoc;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.monarchinitiative.phenol.annotations.formats.Gene;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class parses the Orphanet file that contains the disease to gene associations.
 * For now, we parse out the gene symbol but the Orphanet file contains a number of
 * IDs (but not, unfortunately, EntrezGene ids).
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class OrphaGeneToDiseaseParser {

  private static final String DISORDER = "Disorder";
  private static final String ORPHA_NUMBER = "OrphaNumber";
  private static final String GENE_LIST = "GeneList";
  private static final String GENE = "Gene";
  private static final String NAME = "Name";
  private static final String DISORDER_GENE_ASSOCIATION_LIST = "DisorderGeneAssociationList";
  private static final String DISORDER_GENE_ASSOCIATION = "DisorderGeneAssociation";
  private static final String SYMBOL = "Symbol";
  private static final String EXTERNAL_REFERENCE = "ExternalReference";
  private static final String REFERENCE = "Reference";
  private static final String SOURCE = "Source";

  private boolean inDisorder = false;
  private boolean inGeneList = false;
  private boolean inGene = true;
  private boolean inDisorderGeneAssociation = false;
  private boolean inDisorderGeneAssociationList = false;
  private boolean inExternalReference = false;
  private boolean inOMIMReference = false;

  /**
   * Key: e.g., ORPHA:163746 (for Peripheral demyelinating neuropathy-central dysmyelinating
   * leukodystrophy-Waardenburg syndrome-Hirschsprung disease). Value, corresponding gene symbol(s),
   * e.g., SOX10
   */
  private final Multimap<TermId, Gene> orphaDiseaseToGeneMultiMap;
  private Map<String, TermId> mimIdToGeneIdMap;

  public OrphaGeneToDiseaseParser(File orphaGeneXMLfile, File mim2geneFile) {
    orphaDiseaseToGeneMultiMap = ArrayListMultimap.create();
    parseMim2GeneMedgen(mim2geneFile);
    parseOrphaGeneXml(orphaGeneXMLfile);
  }

  public Multimap<TermId, Gene> getOrphaDiseaseToGeneSymbolMap() {
    return orphaDiseaseToGeneMultiMap;
  }

  /**
   * We want to have a map from a MIM id to an NCBIGene id, which we will store as a TermId object.
   * The mim2gene file has the following format
   * #MIM number	GeneID	type	Source	MedGenCUI	Comment
   * 610911	57514	gene	-	-	-
   * So basically, we check if the third field is "gene". If so, the first field is the MIM id of the gene,
   * and the second field is the NCBI Gene Id (just the number, so we add the prefix "NCBIGene")
   * @param mim2geneFile
   */
  private void parseMim2GeneMedgen(File mim2geneFile) {
    mimIdToGeneIdMap = new HashMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(mim2geneFile))) {
      String line;
      while ((line = br.readLine()) != null) {
        String [] fields = line.split("\t");
        if (fields.length < 3) {
          // should never happen
          System.err.printf("[ERROR] Malformed line (only %d fields): %s.\n", fields.length, line);
          continue;
        }
        if (! fields[2].equals("gene"))
          continue; // we only need to parse the gene<->OMIM-id lines.
        String mim = fields[0];
        TermId geneId = TermId.of("NCBIGene", fields[1]);
        mimIdToGeneIdMap.put(mim, geneId);
      }
    } catch (IOException e) {
      throw new PhenolRuntimeException("Could not parse mim2gene file because of I/O exception: " + e.getMessage());
    }
    System.out.printf("[INFO] Parsed %d OMIM id to NCBI Gene id mappings.\n", this.mimIdToGeneIdMap.size());
  }



  private void parseOrphaGeneXml(File orphaGeneXMLfile) {
    try {

      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      InputStream in = new FileInputStream(orphaGeneXMLfile);
      XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
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
            if (currentOrphanum != null &&
              currentDiseasename != null &&
              currentGeneSymbol != null &&
              currentOmimId != null) {
              TermId orphaId = TermId.of("ORPHA", currentOrphanum);
              TermId geneId = this.mimIdToGeneIdMap.get(currentOmimId);
              if (geneId == null) {
                System.err.printf("[ERROR] Could not find geneId for OMIM id: \"%s\".\n", currentOmimId);
              } else {
                Gene g = new Gene(geneId, currentGeneSymbol);
                this.orphaDiseaseToGeneMultiMap.put(orphaId, g);
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
    } catch (IOException | XMLStreamException e) {
      throw new PhenolRuntimeException("Could not parse orpha disease to gene xml: " + e.getMessage());
    }
  }

}
