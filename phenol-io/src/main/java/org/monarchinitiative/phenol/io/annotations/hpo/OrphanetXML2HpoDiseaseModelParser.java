package org.monarchinitiative.phenol.io.annotations.hpo;

import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationEntry;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationModel;
import org.monarchinitiative.phenol.base.HpoAnnotationModelException;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.formats.hpo.HpoFrequencyTermIds;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 * @author Peter Robinson
 */
public class OrphanetXML2HpoDiseaseModelParser {
    /** Path to {@code en_product4_HPO.xml} file. */
    private final String orphanetXmlPath;
    /** Reference to the HPO Ontology. */
    private final Ontology ontology;
    //private int n_could_not_find_orphanet_HpoId=0;
    //private int n_updatedTermId=0;
    //private int n_updatedTermLabel=0;

    /** A String of the form ORPHA:orphadata[2019-01-05] that we will use as the biocuration entry. */
     private final String orphanetBiocurationString;
    /** A list of diseases parsed from Orphanet. */
    private final List<HpoAnnotationModel> orphanetDiseaseList=new ArrayList<>();
    /** If true, replace obsolete term ids without throwing Exception. */
    private boolean replaceObsoleteTermId;





    public OrphanetXML2HpoDiseaseModelParser(String xmlpath, Ontology onto, boolean tolerant) {
        orphanetXmlPath = xmlpath;
        this.ontology = onto;
        this.replaceObsoleteTermId=tolerant;
        String todaysDate = getTodaysDate();
        orphanetBiocurationString=String.format("ORPHA:orphadata[%s]", todaysDate);
        try {
            parse();
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }
    }


    public List<HpoAnnotationModel> getOrphanetDiseaseModels() { return this.orphanetDiseaseList;}



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
     * @param fstring An Orphanet id (attribute in XML file) corresponding to a frequency category
     * @return corresponding HPO Frequency TermId
     */
    private TermId string2frequency(String fstring) throws PhenolRuntimeException {
        switch (fstring) {
            case "28405": return HpoFrequencyTermIds.ALWAYS_PRESENT;// Obligate
            case "28412": return HpoFrequencyTermIds.VERY_FREQUENT;
            case "28419": return HpoFrequencyTermIds.FREQUENT;
            case "28426": return HpoFrequencyTermIds.OCCASIONAL;
            case "28433": return HpoFrequencyTermIds.VERY_RARE;
            case "28440": return HpoFrequencyTermIds.EXCLUDED;
        }
        // the following should never happen, actually!
        throw new PhenolRuntimeException("[ERROR] Could not find TermId for Orphanet frequency {}. "+
          "This indicates a serious and unexpected error, please report to the developers"+ fstring);
    }


    /**
     * This method performs the XML parse of the Orphanet file
     * @throws XMLStreamException If there is an XML stream issue
     * @throws IOException If the file cannot be opened
     */
    @SuppressWarnings("ConstantConditions")
    private void parse() throws XMLStreamException , IOException {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(orphanetXmlPath));

        boolean inFrequency = false;
        boolean inDiagnosticCriterion = false;
        String currentHpoId=null;
        String currentHpoTermLabel=null;
        TermId currentFrequencyTermId=null;
        String currentOrphanumber=null;
        String currentDiseaseName=null;
        List<HpoAnnotationEntry> currentAnnotationEntryList=new ArrayList<>();
        while (xmlEventReader.hasNext()) {
            XMLEvent xmlEvent = xmlEventReader.nextEvent();
            if (xmlEvent.isStartElement()) {
                StartElement startElement = xmlEvent.asStartElement();
                switch (startElement.getName().getLocalPart()) {
                    case "Disorder":
                        // no op
                        break;
                    case "OrphaNumber":
                        if (inFrequency || inDiagnosticCriterion) {
                            continue;
                        } // Orphanumbers are used for the Disorder but also for the Frequency nodes
                        xmlEvent = xmlEventReader.nextEvent();
                        currentOrphanumber = xmlEvent.asCharacters().getData();
                        break;
                    case "Name":
                        if (inFrequency || inDiagnosticCriterion) {
                            continue;
                        } // skip, we have no need to parse the name of the frequency element
                        // since we get the class from the attribute "id"
                        xmlEvent = xmlEventReader.nextEvent();
                        currentDiseaseName = xmlEvent.asCharacters().getData();
                        break;
                    case "HPOId":
                        xmlEvent = xmlEventReader.nextEvent();
                        currentHpoId = xmlEvent.asCharacters().getData();
                        break;
                    case "HPOTerm":
                        xmlEvent = xmlEventReader.nextEvent();
                        currentHpoTermLabel =  xmlEvent.asCharacters().getData();
                        break;
                    case "HPOFrequency":
                        // if we are here, then we can grab the frequency from the id attribute.
                        Attribute idAttr = startElement.getAttributeByName(new QName("id"));
                        if (idAttr != null) {
                            currentFrequencyTermId = string2frequency(idAttr.getValue());
                        }
                        inFrequency = true;
                        break;
                    case "DiagnosticCriteria":
                        inDiagnosticCriterion=true;
                        break;
                    case "HPO":
                        // no-op, no need to get the id attribute from this node
                        break;
                    case "JDBOR":
                        // no-op, no need to do anything for the very top level node
                        break;
                    default:
                        // no-op, no need to do anything for many node types!
                        break;
                }
            } else if (xmlEvent.isEndElement()) {
                EndElement endElement = xmlEvent.asEndElement();
                String endElementName = endElement.getName().getLocalPart();
                if ( endElementName.equals("HPOFrequency")) {
                    inFrequency = false;
                } else if ( endElementName.equals("DiagnosticCriteria")) {
                    inDiagnosticCriterion = false;
                } else if (endElementName.equals("HPODisorderAssociation")) {
                    // We should have data for HPO Id, HPo Label, and a Frequency term
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
                        System.err.println(String.format("Parse error for %s [ORPHA:%s] HPOid: %s (%s)",
                                currentDiseaseName != null ? currentDiseaseName : "n/a",
                                currentOrphanumber != null ? currentOrphanumber : "n/a",
                                currentHpoId != null ? currentHpoId : "n/a",
                                e.getMessage())
                                );
                    }
                } else if (endElementName.equals("Disorder")) {
                    HpoAnnotationModel file = new HpoAnnotationModel(String.format("ORPHA:%s", currentOrphanumber),
                            currentAnnotationEntryList);
                    orphanetDiseaseList.add(file);
                    currentOrphanumber=null;
                    currentDiseaseName=null;
                    currentAnnotationEntryList.clear();
                } else if (endElementName.equals("JDBOR")) {
                   // no-op all done
                }
            }
        }
    }



    /** We are using this to supply a date created value for the Orphanet annotations.
     * After some research, no better way of getting the current date was found.
     * @return A String such as 2018-02-22
     */
    private String getTodaysDate() {
        Date date = new Date();
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }


}
