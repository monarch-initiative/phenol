package org.monarchinitiative.phenol.io.assoc;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermPrefix;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;

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

  private boolean inDisorder = false;
  private boolean inGeneList = false;
  private boolean inGene = true;
  private boolean inDisorderGeneAssociation = false;
  private boolean inDisorderGeneAssociationList = false;


  private final static TermPrefix ORPHA_PREFIX = new TermPrefix("ORPHA");

  /**
   * Key: e.g., ORPHA:163746 (for Peripheral demyelinating neuropathy-central dysmyelinating
   * leukodystrophy-Waardenburg syndrome-Hirschsprung disease). Value, corresponding gene symbol(s),
   * e.g., SOX10
   */
  private final Multimap<TermId, String> orphaDiseaseToGeneSymbol;

  public OrphaGeneToDiseaseParser(File file) throws PhenolException {
    orphaDiseaseToGeneSymbol = ArrayListMultimap.create();
    parse(file);
  }

  public Multimap<TermId, String> getOrphaDiseaseToGeneSymbolMap() {
    return orphaDiseaseToGeneSymbol;
  }


  private void parse(File file) throws PhenolException {
    try {

      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      InputStream in = new FileInputStream(file);
      XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
      String currentOrphanum = null;
      String currentDiseasename = null;
      String currentGeneSymbol = null;


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
          } else if (localPart.equals(DISORDER_GENE_ASSOCIATION_LIST)) {
            inDisorderGeneAssociationList = false;
            currentOrphanum = null;
            currentDiseasename = null; // clean up after we are done
          } else if (localPart.equals(DISORDER_GENE_ASSOCIATION)) {
            inDisorderGeneAssociation = false;
            // if we have data then we can enter it here
            if (currentOrphanum != null &&
              currentDiseasename != null &&
              currentGeneSymbol != null) {
              TermId orphaId = new TermId(ORPHA_PREFIX, currentOrphanum);
              this.orphaDiseaseToGeneSymbol.put(orphaId, currentGeneSymbol);
              currentGeneSymbol = null; // reset
            }
          } else if (endElement.getName().getLocalPart().equals(GENE)) {
            inGene = false;
          }
        }
      }
    } catch (IOException | XMLStreamException e) {
      throw new PhenolException("Could not parse orpha disease to gene xml.");
    }
  }

}
