package org.monarchinitiative.phenol.annotations.hpo;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.*;

@Deprecated(forRemoval = true)
public class OrphanetInheritanceXMLParser {
  /**
   * Path to {@code en_product9_age.xml} file.
   */
  private final String orphanetXmlPath;
  /**
   * Reference to the HPO Ontology.
   */
  private final Ontology ontology;
  /**
   * A String of the form ORPHA:orphadata[2019-01-05] that we will use as the biocuration entry.
   */
  private final String orphanetBiocurationString;
  /**
   * Key: an Orphanet disease id; value: an array list of HpoAnnotations, one for each inheritance mode
   * that is associated with the disease.
   */
  private final Map<TermId, Collection<HpoAnnotationEntry>> disease2inheritanceMultimap;
  // XML Parsing
  private static final String DISORDER = "Disorder";
  private static final String ORPHA_NUMBER = "OrphaNumber";
  private static final String TYPE_OF_INHERITANCE_LIST = "TypeOfInheritanceList";
  private static final String TYPE_OF_INHERITANCE = "TypeOfInheritance";
  private static final String NAME = "Name";
  private static final String AVERAGE_AGE_OF_ONSET_LIST = "AverageAgeOfOnsetList";
  private static final String AVERAGE_AGE_OF_DEATH_LIST = "AverageAgeOfDeathList";
  private static final String DISORDER_TYPE = "DisorderType";
  /**
   * Orphanet marks some of its intheritance entries as Not apploicable. We will just skip them.
   * This is the corresponding ID.
   */
  private static final String NOT_APPLICABLE_ID = "409941";
  /**
   * similar to above. We will skip this.
   */
  private static final String UNKNOWN_ID = "409939";
  /**
   * similar to above. We will skip this.
   */
  private static final String NO_DATA_AVAILABLE = "409940";


  private boolean inDisorder = false;
  private boolean inTypeOfInheritanceList = false;
  private boolean inTypeOfInheritance = false;
  private boolean inAverageAgeOfOnsetList = false;
  private boolean isInAverageAgeOfDeathList = false;
  private boolean inDisorderType = false;

  private final List<String> errorlist;

  public OrphanetInheritanceXMLParser(String xmlpath, Ontology onto) {
    orphanetXmlPath = xmlpath;
    this.ontology = onto;
    String todaysDate = getTodaysDate();
    errorlist = new ArrayList<>();
    orphanetBiocurationString = String.format("ORPHA:orphadata[%s]", todaysDate);
    disease2inheritanceMultimap = new HashMap<>();
    parse(new File(orphanetXmlPath));
  }


  public Map<TermId, Collection<HpoAnnotationEntry>> getDisease2inheritanceMultimap() {
    return disease2inheritanceMultimap;
  }

  private void parse(File file) {

    try (InputStream in = new FileInputStream(file)) {
      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
      String currentOrphanum = null;
      String currentDiseaseName = null;
      String currentInheritanceId = null;
      String currentModeOfInheritanceLabel = null;
      while (eventReader.hasNext()) {
        XMLEvent event = eventReader.nextEvent();
        if (event.isStartElement()) {
          final String localPart = event.asStartElement().getName().getLocalPart();
          if (localPart.equals(DISORDER)) {
            inDisorder = true;
          } else if (inDisorder &&
            !inAverageAgeOfOnsetList &&
            !isInAverageAgeOfDeathList &&
            !inDisorderType &&
            !inTypeOfInheritance &&
            localPart.equals(ORPHA_NUMBER)) {
            event = eventReader.nextEvent(); // go to the contents of the node
            currentOrphanum = event.asCharacters().getData();
          } else if (inDisorder && localPart.equals(TYPE_OF_INHERITANCE_LIST)) {
            inTypeOfInheritanceList = true;
          } else if (inTypeOfInheritanceList && localPart.equals(TYPE_OF_INHERITANCE)) {
            inTypeOfInheritance = true;
          } else if (inDisorder &&
            !inAverageAgeOfOnsetList &&
            !isInAverageAgeOfDeathList &&
            !inDisorderType &&
            !inTypeOfInheritance &&
            localPart.equals(NAME)) {

            event = eventReader.nextEvent(); // go to the contents of the node
            currentDiseaseName = event.asCharacters().getData();
          } else if (inDisorder &&
            !inAverageAgeOfOnsetList &&
            !isInAverageAgeOfDeathList &&
            !inDisorderType &&
            inTypeOfInheritance &&
            localPart.equals(NAME)) {
            event = eventReader.nextEvent(); // go to the contents of the node
            currentModeOfInheritanceLabel = event.asCharacters().getData();
            if (currentInheritanceId==null || currentInheritanceId.equals(NOT_APPLICABLE_ID) ||
              currentInheritanceId.equals(UNKNOWN_ID) ||
              currentInheritanceId.equals(NO_DATA_AVAILABLE)) {
              continue;
            }
            TermId disId = TermId.of(String.format("ORPHA:%s", currentOrphanum));
            TermId hpoInheritanceId = getHpoInheritanceTermId(currentInheritanceId, currentModeOfInheritanceLabel);
            if (hpoInheritanceId == null) {
              continue;
            }
            if (!ontology.getTermMap().containsKey(hpoInheritanceId)) {
              this.errorlist.add("[WARNING] Could not find HPO label for Orphanet inheritance term" + hpoInheritanceId.getValue());
              continue;
            }
            String hpoLabel = ontology.getTermMap().get(hpoInheritanceId).getName();
            HpoAnnotationEntry entry = HpoAnnotationEntry.fromOrphaInheritanceData(disId.getValue(),
              currentDiseaseName,
              hpoInheritanceId,
              hpoLabel,
              orphanetBiocurationString);
            disease2inheritanceMultimap.computeIfAbsent(disId, key -> new HashSet<>())
              .add(entry);
          } else if (localPart.equals(AVERAGE_AGE_OF_ONSET_LIST)) {
            inAverageAgeOfOnsetList = true;
          } else if (localPart.equals(AVERAGE_AGE_OF_DEATH_LIST)) {
            isInAverageAgeOfDeathList = true;
          } else if (localPart.equals(TYPE_OF_INHERITANCE)) {
            inTypeOfInheritance = true;
          } else if (localPart.equals(DISORDER_TYPE)) {
            inDisorderType = true;
          }
          if (inTypeOfInheritance && localPart.equals(ORPHA_NUMBER)) {
            event = eventReader.nextEvent(); // go to the contents of the node
            currentInheritanceId = event.asCharacters().getData();
          }
        } else if (event.isEndElement()) {
          EndElement endElement = event.asEndElement();
          String localPart = endElement.getName().getLocalPart();
          if (localPart.equals(DISORDER)) {
            inDisorder = false;
          } else if (localPart.equals(TYPE_OF_INHERITANCE_LIST)) {
            inTypeOfInheritanceList = false;
          } else if (localPart.equals(TYPE_OF_INHERITANCE)) {
            inTypeOfInheritance = false;
          } else if (localPart.equals(AVERAGE_AGE_OF_ONSET_LIST)) {
            inAverageAgeOfOnsetList = false;
          } else if (localPart.equals(AVERAGE_AGE_OF_DEATH_LIST)) {
            isInAverageAgeOfDeathList = false;
          } else if (localPart.equals(DISORDER_TYPE)) {
            inDisorderType = false;
          }
        }
      }
    } catch (IOException | XMLStreamException e) {
      e.printStackTrace();
    }
  }


  private TermId getHpoInheritanceTermId(String orphaInheritanceId, String orphaLabel) {
    if (orphaInheritanceId.equals("409930") && orphaLabel.equals("Autosomal recessive")) {
      return AUTOSOMAL_RECESSIVE;
    } else if (orphaInheritanceId.equals("409929") && orphaLabel.equals("Autosomal dominant")) {
      return AUTOSOMAL_DOMINANT;
    } else if (orphaInheritanceId.equals("409931") && orphaLabel.equals("Multigenic/multifactorial")) {
      return MULTIFACTORIAL;
    } else if (orphaInheritanceId.equals("409932") && orphaLabel.equals("X-linked recessive")) {
      return X_LINKED_RECESSIVE;
    } else if (orphaInheritanceId.equals("409933") && orphaLabel.equals("Mitochondrial inheritance")) {
      return MITOCHONDRIAL;
    } else if (orphaInheritanceId.equals("409934") && orphaLabel.equals("X-linked dominant")) {
      return X_LINKED_DOMINANT;
    } else if (orphaInheritanceId.equals("409938") && orphaLabel.equals("Y-linked")) {
      return Y_LINKED;
    } else if (orphaInheritanceId.equals("409937") && orphaLabel.equals("Semi-dominant")) {
      return INHERITANCE_ROOT; //TODO SEMIDOMINANT
    } else if (orphaInheritanceId.equals("409936") && orphaLabel.equals("Oligogenic")) {
      return OLIGOGENIC;
    } else {
      this.errorlist.add("[WARNING] Could not find HPO id for Orphanet inheritence entry: " + orphaLabel + "(" + orphaInheritanceId + ")");
      return null; // could not find correct id.
    }
  }

  public boolean hasError() {
    return this.errorlist.size() > 0;
  }

  public List<String> getErrorlist() {
    return errorlist;
  }

  /**
   * We are using this to supply a date created value for the Orphanet annotations.
   * After some research, no better way of getting the current date was found.
   *
   * @return A String such as 2018-02-22
   */
  private String getTodaysDate() {
    Date date = new Date();
    return new SimpleDateFormat("yyyy-MM-dd").format(date);
  }
}
