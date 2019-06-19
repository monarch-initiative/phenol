package org.monarchinitiative.phenol.io.annotations.hpo;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationEntry;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationModel;
import org.monarchinitiative.phenol.base.HpoAnnotationModelException;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrphanetInheritanceXMLParser {
  /** Path to {@code en_product9_age.xml} file. */
  private final String orphanetXmlPath;
  /** Reference to the HPO Ontology. */
  private final Ontology ontology;
  /** A String of the form ORPHA:orphadata[2019-01-05] that we will use as the biocuration entry. */
  private final String orphanetBiocurationString;
  /** Key: an Orphanet disease id; value: an array list of HpoAnnotations, one for each inheritance mode
   * that is associated with the disease.
   */
  private final Multimap<TermId,HpoAnnotationEntry> disease2inheritanceMultimap;
  // XML Parsing
  private static final String DISORDER = "Disorder";
  private static final String ORPHA_NUMBER = "OrphaNumber";
  private static final String TYPE_OF_INHERITANCE_LIST = "TypeOfInheritanceList";
  private static final String TYPE_OF_INHERITANCE = "TypeOfInheritance";
  private static final String NAME = "Name";
  private static final String AVERAGE_AGE_OF_ONSET_LIST = "AverageAgeOfOnsetList";
  private static final String AVERAGE_AGE_OF_DEATH_LIST = "AverageAgeOfDeathList";
  private static final String DISORDER_TYPE = "DisorderType";
  private boolean inDisorder = false;
  private boolean inOrphaNumber = false;
  private boolean inTypeOfInheritanceList = false;
  private boolean inTypeOfInheritance = false;
  private boolean inAverageAgeOfOnsetList = false;
  private boolean isInAverageAgeOfDeathList = false;
  private boolean inDisorderType = false;

  public OrphanetInheritanceXMLParser(String xmlpath, Ontology onto) {
    orphanetXmlPath = xmlpath;
    this.ontology = onto;
    String todaysDate = getTodaysDate();
    orphanetBiocurationString=String.format("ORPHA:orphadata[%s]", todaysDate);
    disease2inheritanceMultimap =  ArrayListMultimap.create();
    parse(new File(orphanetXmlPath));
  }


  public Multimap<TermId, HpoAnnotationEntry> getDisease2inheritanceMultimap() {
    return disease2inheritanceMultimap;
  }

  private void parse(File file) {
    try {
      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      InputStream in = new FileInputStream(file);
      XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
      String currentOrphanum = null;
      String currentDiseaseName=null;
      String currentInheritanceId=null;
      String currentModeOfInheritanceLabel=null;
      ArrayList<String> currentModesOfInheritance = new ArrayList<>();
      while (eventReader.hasNext()) {
        XMLEvent event = eventReader.nextEvent();
        if (event.isStartElement()) {
          final String localPart = event.asStartElement().getName().getLocalPart();
          if (localPart.equals(DISORDER)) {
            inDisorder = true;
          } else if (inDisorder &&
            !inAverageAgeOfOnsetList &&
            ! isInAverageAgeOfDeathList &&
            ! inDisorderType &&
            ! inTypeOfInheritance &&
            localPart.equals(ORPHA_NUMBER)) {
            inOrphaNumber=true;
            event = eventReader.nextEvent(); // go to the contents of the node
            currentOrphanum = event.asCharacters().getData();
          } else if (inDisorder && localPart.equals(TYPE_OF_INHERITANCE_LIST)) {
            inTypeOfInheritanceList=true;
            currentModesOfInheritance.clear();
          } else if (inTypeOfInheritanceList && localPart.equals(TYPE_OF_INHERITANCE)) {
            inTypeOfInheritance = true;
          } else if (inDisorder &&
            ! inAverageAgeOfOnsetList &&
            ! isInAverageAgeOfDeathList &&
            ! inDisorderType &&
            ! inTypeOfInheritance &&
            localPart.equals(NAME)) {
            event = eventReader.nextEvent(); // go to the contents of the node
            currentDiseaseName = event.asCharacters().getData();
          } else if (inDisorder &&
            ! inAverageAgeOfOnsetList &&
            ! isInAverageAgeOfDeathList &&
            ! inDisorderType &&
            inTypeOfInheritance &&
            localPart.equals(NAME)) {
            event = eventReader.nextEvent(); // go to the contents of the node
            currentModeOfInheritanceLabel = event.asCharacters().getData();
            try {
              TermId disId = TermId.of(String.format("ORPHA:%s",currentOrphanum ));
              HpoAnnotationEntry entry = HpoAnnotationEntry.fromOrphaInheritanceData(disId.getValue(),
                currentDiseaseName,
                currentInheritanceId,
                currentModeOfInheritanceLabel,
                this.ontology,
                orphanetBiocurationString);
              this.disease2inheritanceMultimap.put(disId,entry);
            } catch ( HpoAnnotationModelException e) {
              System.err.println("[WARNING] Could not parse Orphanet Inheritance annotation: "+e.getMessage());
            }


          }else if (localPart.equals(AVERAGE_AGE_OF_ONSET_LIST)) {
            inAverageAgeOfOnsetList = true;
          } else if (localPart.equals(AVERAGE_AGE_OF_DEATH_LIST)) {
            isInAverageAgeOfDeathList=true;
          } else if (localPart.equals(TYPE_OF_INHERITANCE)) {
            inTypeOfInheritance = true;
          } else if (localPart.equals(DISORDER_TYPE)) {
            inDisorderType = true;
          }
          if (inTypeOfInheritance && localPart.equals(ORPHA_NUMBER)) {
            event = eventReader.nextEvent(); // go to the contents of the node
            currentInheritanceId = event.asCharacters().getData();
          }
        }  else if (event.isEndElement()) {
          EndElement endElement = event.asEndElement();
          String localPart = endElement.getName().getLocalPart();
          if (localPart.equals(DISORDER)) {
            inDisorder = false;
            System.out.println("Disorder:" + currentDiseaseName);
            for (String moi : currentModesOfInheritance) {
              System.out.println("MOI:"+currentOrphanum +": " + currentDiseaseName + ": "+ moi);
            }
          } else if (localPart.equals(ORPHA_NUMBER)) {
            inOrphaNumber = false;
          } else if (localPart.equals(TYPE_OF_INHERITANCE_LIST)) {
            inTypeOfInheritanceList = false;
          } else if (localPart.equals(TYPE_OF_INHERITANCE)) {
            inTypeOfInheritance = false;
          } else if (localPart.equals(AVERAGE_AGE_OF_ONSET_LIST)) {
            inAverageAgeOfOnsetList = false;
          } else if (localPart.equals(AVERAGE_AGE_OF_DEATH_LIST)) {
            isInAverageAgeOfDeathList=false;
          } else if (localPart.equals(DISORDER_TYPE)) {
            inDisorderType = false;
          }
        }
      }
    } catch (IOException | XMLStreamException e) {
      e.printStackTrace();
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
