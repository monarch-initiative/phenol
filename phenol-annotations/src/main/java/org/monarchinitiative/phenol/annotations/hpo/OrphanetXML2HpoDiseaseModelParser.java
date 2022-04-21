package org.monarchinitiative.phenol.annotations.hpo;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.constants.hpo.HpoFrequencyTermIds;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * This class is an XML parser for the Orphanet file with HPO-based disease annotations
 * ({@code en_product4_HPO.xml} (see http://www.orphadata.org/). Note that the section of the
 * XML that denotes an individual HPO annotation with frequency is like this
 * <pre>
 *     <HPODisorderAssociation id="2603">
 *        <HPO id="58">
 *          <HPOId>HP:0010535</HPOId>
 *          <HPOTerm>Sleep apnea</HPOTerm>
 *         </HPO>
 *         <HPOFrequency id="28426">
 *            <OrphaNumber>453313</OrphaNumber>
 *             <Name lang="en">Occasional (29-5%)</Name>
 *        </HPOFrequency>
 *       <DiagnosticCriteria/>
 *    </HPODisorderAssociation>
 * </pre>
 * That is, one line of the annotation is contained with an {@code HPODisorderAssociation} node.
 * Each disease begins like this
 * <pre>
 *      <Disorder id="160">
 *       <OrphaNumber>437</OrphaNumber>
 *       <Name lang="en">Hypophosphatemic rickets</Name>
 *       <HPODisorderAssociationList count="11">
 *         <HPODisorderAssociation id="2574">
 *             (...)
 * </pre>
 * That is, we extract the Orphanumber and the name and then there is a list of annotations.
 *
 * @author Peter Robinson
 */
public class OrphanetXML2HpoDiseaseModelParser {
  private final static Logger logger = LoggerFactory.getLogger(OrphanetXML2HpoDiseaseModelParser.class);
  /**
   * Path to {@code en_product4_HPO.xml} file.
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
   * A map of diseases parsed from Orphanet.
   */
  private final Map<TermId, HpoAnnotationModel> orphanetDiseaseMap = new HashMap<>();
  /**
   * If true, replace obsolete term ids without throwing Exception.
   */
  private final boolean replaceObsoleteTermId;

  private static final String DISORDER = "Disorder";
  private static final String ORPHA_NUMBER = "OrphaNumber";
  private static final String ORPHA_CODE = "OrphaCode";

  private static final String NAME = "Name";
  private static final String HPO_DISORDER_ASSOCIATION = "HPODisorderAssociation";
  private static final String DIAGNOSTIC_CRITERIA = "DiagnosticCriteria";
  private static final String DISORDER_TYPE = "DisorderType";
  private static final String DISORDER_GROUP = "DisorderGroup";
  private static final String EXPERT_LINK = "ExpertLink";

  private static final String HPO_DISORDER_ASSOCIATION_LIST = "HPODisorderAssociationList";
  private static final String HPO_DISORDER_SET_STATUS = "HPODisorderSetStatus";
  private static final String HPO_DISORDER_SET_STATUS_LIST = "HPODisorderSetStatusList";
  private static final String HPO_FREQUENCY = "HPOFrequency";
  private static final String HPO = "HPO";
  private static final String HPO_ID = "HPOId";
  private static final String HPO_TERM = "HPOTerm";
  private static final String JDBOR = "JDBOR";
  private static final String AVAILABILITY = "Availability";
  private static final String FULL_NAME = "FullName";
  private static final String SHORT_IDENTIFIER = "ShortIdentifier";
  private static final String LEGAL_CODE = "LegalCode";
  private static final String LICENSE = "Licence";
  private static final String SOURCE = "Source";
  private static final String VALIDATION_DATE = "ValidationDate";
  private static final String VALIDATION_STATUS = "ValidationStatus";
  private static final String ONLINE = "Online";


  /**
   * These are the local names of the Orphanet product4.xml file.
   */
  private final Set<String> allowableXmlNodeNames = Stream.of(AVAILABILITY,
    DIAGNOSTIC_CRITERIA,
    DISORDER,
    DISORDER_GROUP,
    DISORDER_TYPE,
    EXPERT_LINK,
    FULL_NAME,
    HPO_DISORDER_ASSOCIATION,
    HPO_DISORDER_ASSOCIATION_LIST,
    HPO_DISORDER_SET_STATUS,
    HPO_DISORDER_SET_STATUS_LIST,
    HPO_FREQUENCY,
    HPO_ID,
    HPO,
    HPO_TERM,
    JDBOR,
    LEGAL_CODE,
    LICENSE,
    NAME,
    ONLINE,
    ORPHA_CODE,
    ORPHA_NUMBER,
    SOURCE,
    SHORT_IDENTIFIER,
    VALIDATION_DATE,
    VALIDATION_STATUS)
    .collect(Collectors.toCollection(HashSet::new));


  public OrphanetXML2HpoDiseaseModelParser(String xmlpath, Ontology onto, boolean tolerant) {
    orphanetXmlPath = xmlpath;
    this.ontology = onto;
    this.replaceObsoleteTermId = tolerant;
    String todaysDate = getTodaysDate();
    orphanetBiocurationString = String.format("ORPHA:orphadata[%s]", todaysDate);
    try {
      parse();
    } catch (XMLStreamException | IOException e) {
      e.printStackTrace();
    }
  }

  public Map<TermId, HpoAnnotationModel> getOrphanetDiseaseMap() {
    return this.orphanetDiseaseMap;
  }

  /**
   * Transform the Orphanet codes into HPO Frequency TermId's.
   * <p>
   * The frequency ids are
   * </p>
   * <ul><li>Excluded (0%): Orphanet id 28440</li>
   * <li>Frequent (79-30%): Orphanet id: 28419</li>
   * <li>Obligate (100%): Orphanet id: 28405</li>
   * <li>Occasional (29-5%): Orphanet id: 28426</li>
   * <li>Very frequent (99-80%): Orphanet id 28412</li>
   * <li>Very rare : Orphanet id 28433</li>
   * </ul>
   *
   * @param fstring An Orphanet id (attribute in XML file) corresponding to a frequency category
   * @return corresponding HPO Frequency TermId
   */
  private TermId string2frequency(String fstring) throws PhenolRuntimeException {
    switch (fstring) {
      case "28405":
        return HpoFrequencyTermIds.OBLIGATE;
      case "28412":
        return HpoFrequencyTermIds.VERY_FREQUENT;
      case "28419":
        return HpoFrequencyTermIds.FREQUENT;
      case "28426":
        return HpoFrequencyTermIds.OCCASIONAL;
      case "28433":
        return HpoFrequencyTermIds.VERY_RARE;
      case "28440":
        return HpoFrequencyTermIds.EXCLUDED;
    }
    // the following should never happen, actually!
    throw new PhenolRuntimeException("[ERROR] Could not find TermId for Orphanet frequency {}. " +
      "This indicates a serious and unexpected error, please report to the developers" + fstring);
  }


  /**
   * This method performs the XML parse of the Orphanet file
   *
   * @throws XMLStreamException If there is an XML stream issue
   * @throws IOException        If the file cannot be opened
   */
  @SuppressWarnings("ConstantConditions")
  private void parse() throws XMLStreamException, IOException {
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(orphanetXmlPath));

    boolean inFrequency = false;
    boolean inDiagnosticCriterion = false;
    boolean inDisorderType = false;
    boolean inDisorderGroup = false;
    String currentHpoId = null;
    String currentHpoTermLabel = null;
    TermId currentFrequencyTermId = null;
    String currentOrphanumber = null;
    String currentDiseaseName = null;
    List<HpoAnnotationEntry> currentAnnotationEntryList = new ArrayList<>();
    while (xmlEventReader.hasNext()) {
      XMLEvent xmlEvent = xmlEventReader.nextEvent();
      if (xmlEvent.isStartElement()) {
        StartElement startElement = xmlEvent.asStartElement();
        String localName = startElement.getName().getLocalPart();
        if (!allowableXmlNodeNames.contains(localName)) {
          throw new PhenolRuntimeException("Unexpected XML Node in Orphanet product_4 XML: " + localName);
        }
        switch (localName) {
          case DISORDER_TYPE:
            inDisorderType = true;
            break;
          case ORPHA_CODE:
            if (inFrequency || inDiagnosticCriterion) {
              continue;
            } // Orphanumbers are used for the Disorder but also for the Frequency nodes
            xmlEvent = xmlEventReader.nextEvent();
            currentOrphanumber = xmlEvent.asCharacters().getData();
            break;
          case NAME:
            if (inFrequency || inDiagnosticCriterion || inDisorderGroup || inDisorderType) {
              continue;
            }

              // skip, we have no need to parse the name of the frequency element
              // since we get the class from the attribute "id"
              xmlEvent = xmlEventReader.nextEvent();
              currentDiseaseName = xmlEvent.asCharacters().getData();

            break;
          case DISORDER_GROUP:
            inDisorderGroup = true;
            break;
          case HPO_ID:
            xmlEvent = xmlEventReader.nextEvent();
            currentHpoId = xmlEvent.asCharacters().getData();
            break;
          case HPO_TERM:
            xmlEvent = xmlEventReader.nextEvent();
            currentHpoTermLabel = xmlEvent.asCharacters().getData();
            break;
          case HPO_FREQUENCY:
            // if we are here, then we can grab the frequency from the id attribute.
            Attribute idAttr = startElement.getAttributeByName(new QName("id"));
            if (idAttr != null) {
              currentFrequencyTermId = string2frequency(idAttr.getValue());
            }
            inFrequency = true;
            break;
          case DIAGNOSTIC_CRITERIA:
            inDiagnosticCriterion = true;
            break;
          case HPO:
            // no-op, no need to get the id attribute from this node
            break;
          case JDBOR:
            // no-op, no need to do anything for the very top level node
            break;
          default:
            // no-op, no need to do anything for many node types!
            break;
        }
      } else if (xmlEvent.isEndElement()) {
        EndElement endElement = xmlEvent.asEndElement();
        String localPart = endElement.getName().getLocalPart();
        switch (localPart) {
          case HPO_FREQUENCY:
            inFrequency = false;
            break;
          case DIAGNOSTIC_CRITERIA:
            inDiagnosticCriterion = false;
            break;
          case HPO_DISORDER_ASSOCIATION:
            try {
              HpoAnnotationEntry entry = HpoAnnotationEntry.fromOrphaData(
                String.format("ORPHA:%s", currentOrphanumber),
                currentDiseaseName,
                currentHpoId,
                currentHpoTermLabel,
                currentFrequencyTermId,
                ontology,
                orphanetBiocurationString,
                replaceObsoleteTermId);
              currentHpoId = null;
              currentHpoTermLabel = null;
              currentFrequencyTermId = null;// reset
              currentAnnotationEntryList.add(entry);
            } catch (HpoAnnotationModelException e) {
              logger.warn(String.format("Parse error for %s [ORPHA:%s] HPOid: %s (%s)",
                currentDiseaseName != null ? currentDiseaseName : "n/a",
                currentOrphanumber != null ? currentOrphanumber : "n/a",
                currentHpoId != null ? currentHpoId : "n/a",
                e.getMessage())
              );
            }
            break;
          case DISORDER_GROUP:
            inDisorderGroup = false;
            break;
          case DISORDER_TYPE:
            inDisorderType = false;
            break;
          case DISORDER:
            TermId orphaDiseaseId = TermId.of(String.format("ORPHA:%s", currentOrphanumber));
            HpoAnnotationModel model = new HpoAnnotationModel(String.format("ORPHA:%s", currentOrphanumber),
              currentAnnotationEntryList);
            orphanetDiseaseMap.put(orphaDiseaseId, model);
            inDisorderType = false;
            currentOrphanumber = null;
            currentDiseaseName = null;
            currentAnnotationEntryList.clear();
        }
      }
    }
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
