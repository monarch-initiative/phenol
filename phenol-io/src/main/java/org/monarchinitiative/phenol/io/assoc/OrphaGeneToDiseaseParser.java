package org.monarchinitiative.phenol.io.assoc;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;

public class OrphaGeneToDiseaseParser {

  static final String DISORDER_LIST = "DisorderList";
  static final String DISORDER = "Disorder";
  static final String ORPHA_NUMBER = "OrphaNumber";
  static final String GENE_LIST="GeneList";
  static final String GENE="Gene";
  static final String NAME="Name";
  static final String DISORDER_GENE_ASSOCIATION_LIST="DisorderGeneAssociationList";
  static final String DISORDER_GENE_ASSOCIATION="DisorderGeneAssociation";
  static final String SYMBOL="Symbol";
  static final String DISORDER_GENE_ASSOCIATION_TYPE="DisorderGeneAssociationType";


  boolean inDisorder=false;
  boolean inGeneList=false;
  boolean inDisorderGeneAssociation=false;
  boolean inDisorderGeneAssociationList=false;
  boolean inDisorderGeneAssociationListGene=false;
  boolean inDisorderGeneAssociationListDisorderGeneAssociationType=false;

  public OrphaGeneToDiseaseParser(File file){
    parse(file);
  }




  private void parse(File file) {
    try {

      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      InputStream in = new FileInputStream(file);
      XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
        while (eventReader.hasNext()) {
          XMLEvent event = eventReader.nextEvent();

          if (event.isStartElement()) {
            StartElement startElement = event.asStartElement();
            //System.out.println(startElement.getName());
            if (!inDisorder && event.asStartElement().getName().getLocalPart().equals(DISORDER)) {
              inDisorder=true;
            } else if (inDisorder &&
              ! inGeneList &&
              ! inDisorderGeneAssociationList &&
              event.asStartElement().getName().getLocalPart().equals(ORPHA_NUMBER)) {
              event = eventReader.nextEvent();
              System.out.println("Orphanumber for disorder="+event.asCharacters().getData());
            } else if (inDisorder &&
              ! inGeneList &&
              ! inDisorderGeneAssociationList &&
              event.asStartElement().getName().getLocalPart().equals(NAME)) {
              event = eventReader.nextEvent();
              System.out.println("Name="+event.asCharacters().getData());
            }else if (inDisorder &&
              ! inGeneList &&
              ! inDisorderGeneAssociationList &&
              event.asStartElement().getName().getLocalPart().equals(GENE_LIST)) {
              inGeneList=true;
            } else if (inDisorder &&
              ! inGeneList &&
              ! inDisorderGeneAssociationList &&
              event.asStartElement().getName().getLocalPart().equals(DISORDER_GENE_ASSOCIATION_LIST)) {
              inDisorderGeneAssociationList=true;
            } else if (inDisorder &&
              ! inGeneList &&
               inDisorderGeneAssociationList &&
              event.asStartElement().getName().getLocalPart().equals(GENE)) {
              inDisorderGeneAssociationListGene=true;
            }


          } else if (event.isEndElement()) {
              EndElement endElement = event.asEndElement();
              if (endElement.getName().getLocalPart().equals(DISORDER)) {
               inDisorder=false;
              } else if (endElement.getName().getLocalPart().equals(GENE_LIST)) {
                inGeneList=false;
              } else if (endElement.getName().getLocalPart().equals(DISORDER_GENE_ASSOCIATION_LIST)) {
                inDisorderGeneAssociationList=false;
              } else if ( inDisorderGeneAssociation  &&
                endElement.getName().getLocalPart().equals(GENE)) {
                inDisorderGeneAssociationList=false;
              }
          }
        }



    } catch (IOException | XMLStreamException e){
      e.printStackTrace();
    }
  }


}
