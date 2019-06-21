package org.monarchinitiative.phenol.io.annotations.hpo;

import com.google.common.collect.Multimap;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationEntry;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationModel;
import org.monarchinitiative.phenol.base.HpoAnnotationModelException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.existsPath;

/**
 * This class coordinates writing out the {@code phenotype.hpoa}, the so-called "big file", which is
 * aggregated from the ca. 7000 small files.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class PhenotypeDotHpoaFileWriter {
  /**
   * List of all of the {@link HpoAnnotationModel} objects from our annotations (OMIM and DECIPHER).
   */
  private final List<HpoAnnotationModel> internalAnnotFileList;
  /**
   * List of all of the {@link HpoAnnotationModel} objects derived from the Orphanet XML file.
   */
  private final List<HpoAnnotationModel> orphanetSmallFileList;

  private final Multimap<TermId, HpoAnnotationEntry> inheritanceMultiMap;

  /**
   * Usually "phenotype.hpoa", but may also include path.
   */
  private final String outputFileName;
  private final static String EMPTY_STRING = "";
  private final Ontology ontology;
  /**
   * Number of annotated Orphanet entries.
   */
  private int n_orphanet;
  private int n_decipher;
  private int n_omim;
  /**
   * Number of database sources that could not be identified (should be zero!).
   */
  private int n_unknown;

  private Map<String, String> ontologyMetaInfo;

  private final File smallFileDirectory;
  private final File orphaPhenotypeXMLfile;
  private final File orphaInheritanceXMLfile;

  private boolean tolerant = true;

  /**
   * @param ont                     reference to HPO ontology
   * @param smallFileDirectoryPath  List of annotation models for data from the HPO small files
   * @param orphaPhenotypeXMLpath,  path to
   * @param orphaInheritanceXMLpath List of inheritance annotations for Orphanet data
   * @param outpath                 path of the outfile (usually {@code phenotype.hpoa})
   */
  public static PhenotypeDotHpoaFileWriter factory(Ontology ont,
                                                   String smallFileDirectoryPath,
                                                   String orphaPhenotypeXMLpath,
                                                   String orphaInheritanceXMLpath,
                                                   String outpath) {

    PhenotypeDotHpoaFileWriter pwriter = new PhenotypeDotHpoaFileWriter(ont,
      smallFileDirectoryPath,
      orphaPhenotypeXMLpath,
      orphaInheritanceXMLpath,
      outpath);
    return pwriter;

  }
  /**
   * @param ont                     reference to HPO ontology
   * @param smallFileDirectoryPath  List of annotation models for data from the HPO small files
   * @param orphaPhenotypeXMLpath,  path to
   * @param orphaInheritanceXMLpath List of inheritance annotations for Orphanet data
   * @param outpath                 path of the outfile (usually {@code phenotype.hpoa})
   */
  private PhenotypeDotHpoaFileWriter(Ontology ont,
                                     String smallFileDirectoryPath,
                                     String orphaPhenotypeXMLpath,
                                     String orphaInheritanceXMLpath,
                                     String outpath) {
    Objects.requireNonNull(ont);
    this.ontology = ont;
    Objects.requireNonNull(outpath);
    this.outputFileName = outpath;
    smallFileDirectory = new File(smallFileDirectoryPath);
    if (!smallFileDirectory.exists()) {
      throw new RuntimeException("Could not find " + smallFileDirectoryPath
        + " (We were expecting the directory with the HPO disease annotation files");
    }
    if (!smallFileDirectory.isDirectory()) {
      throw new RuntimeException(smallFileDirectoryPath
        + " is not a directory (We were expecting the directory with the HPO disease annotation files");
    }
    orphaPhenotypeXMLfile = new File(orphaPhenotypeXMLpath);
    if (!orphaPhenotypeXMLfile.exists()) {
      throw new RuntimeException("Could not find " + orphaPhenotypeXMLfile
        + " (We were expecting the path to en_product4_HPO.xml");
    }
    orphaInheritanceXMLfile = new File(orphaInheritanceXMLpath);
    if (!orphaInheritanceXMLfile.exists()) {
      throw new RuntimeException("Could not find " + orphaInheritanceXMLpath
        + " (We were expecting the path to en_product9_ages.xml");
    }
    HpoAnnotationFileIngestor annotationFileIngestor =
      new HpoAnnotationFileIngestor(smallFileDirectory.getAbsolutePath(), ont);
    this.internalAnnotFileList = annotationFileIngestor.getV2SmallFileEntries();
    // 2. Get the Orphanet Inheritance Annotations
    OrphanetInheritanceXMLParser inheritanceXMLParser =
      new OrphanetInheritanceXMLParser(orphaInheritanceXMLfile.getAbsolutePath(), ontology);
    this.inheritanceMultiMap = inheritanceXMLParser.getDisease2inheritanceMultimap();

    OrphanetXML2HpoDiseaseModelParser orphaParser =
      new OrphanetXML2HpoDiseaseModelParser(this.orphaPhenotypeXMLfile.getAbsolutePath(), ontology, tolerant);
    List<HpoAnnotationModel> prelimOrphaDiseaseList = orphaParser.getOrphanetDiseaseModels();

    for (HpoAnnotationModel model : prelimOrphaDiseaseList) {
      TermId diseaseID = model.getDiseaseId();
      if (this.inheritanceMultiMap.containsKey(diseaseID)) {
        Collection<HpoAnnotationEntry> inheritanceEntryList = this.inheritanceMultiMap.get(diseaseID);
        HpoAnnotationEntry mergedModel = model.mergeWithInheritanceAnnotations(inheritanceEntryList);
        model.addInheritanceEntryCollection(inheritanceEntryList);
      }
    }
    setOntologyMetadata(ont.getMetaInfo());
    setNumberOfDiseasesForHeader();
  }

  /**
   * In the header of the {@code phenotype.hpoa} file, we write the
   * number of OMIM, Orphanet, and DECIPHER entries. This is calculated
   * here (except for Orphanet).
   */
  public void setNumberOfDiseasesForHeader() {

    this.n_decipher = 0;
    this.n_omim = 0;
    this.n_unknown = 0;
    for (HpoAnnotationModel diseaseModel : internalAnnotFileList) {
      if (diseaseModel.isOMIM()) n_omim++;
      else if (diseaseModel.isDECIPHER()) n_decipher++;
      else n_unknown++;
    }
    this.n_orphanet = orphanetSmallFileList.size();
  }


  private void setOntologyMetadata(Map<String, String> meta) {
    this.ontologyMetaInfo = meta;
  }

  private String getDate() {
    Date dNow = new Date();
    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
    return ft.format(dNow);
  }

  /**
   * Output the {@code phenotype.hpoa} file on the basis of the "small files" and the Orphanet XML file.
   *
   * @throws IOException if we cannot write to file.
   */
  public void outputBigFile() throws IOException {
    String description = String.format("#description: HPO annotations for rare diseases [%d: OMIM; %d: DECIPHER; %d ORPHANET]", n_omim, n_decipher, n_orphanet);
    if (n_unknown > 0)
      description = String.format("%s -- warning: %d entries could not be assigned to a database", description, n_unknown);
    BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
    writer.write(description + "\n");
    writer.write(String.format("#date: %s\n", getDate()));
    writer.write("#tracker: https://github.com/obophenotype/human-phenotype-ontology\n");
    if (ontologyMetaInfo.containsKey("data-version")) {
      writer.write(String.format("#HPO-version: %s\n", ontologyMetaInfo.get("data-version")));
    }
    if (ontologyMetaInfo.containsKey("saved-by")) {
      writer.write(String.format("#HPO-contributors: %s\n", ontologyMetaInfo.get("saved-by")));
    }
    int n = 0;
    writer.write(getHeaderLine() + "\n");
    for (HpoAnnotationModel smallFile : this.internalAnnotFileList) {
      List<HpoAnnotationEntry> entryList = smallFile.getEntryList();
      for (HpoAnnotationEntry entry : entryList) {
        try {
          String bigfileLine = entry.toBigFileLine(ontology);
          writer.write(bigfileLine + "\n");
        } catch (HpoAnnotationModelException e) {
          System.err.println("Error encountered with entry " + entry.toString());
          e.printStackTrace();
        }
        n++;
      }
    }
    System.out.println("We output a total of " + n + " big file lines from internal HPO Annotation files");
    int m = 0;
    Set<TermId> seenInheritance = new HashSet<>();
    for (HpoAnnotationModel smallFile : this.orphanetSmallFileList) {
      List<HpoAnnotationEntry> entryList = smallFile.getEntryList();
      for (HpoAnnotationEntry entry : entryList) {
        try {
          TermId diseaseId = TermId.of(entry.getDB_Object_ID());
          if (this.inheritanceMultiMap.containsKey(diseaseId)  // just output the first time around
            && !seenInheritance.contains(diseaseId)) {
            seenInheritance.add(diseaseId);
            List<HpoAnnotationEntry> inheritanceEntryList =
              new ArrayList<>(inheritanceMultiMap.get(diseaseId));
            for (HpoAnnotationEntry inhEntry : inheritanceEntryList) {
              String bigFileInheritanceLine = inhEntry.toBigFileLine(ontology);
              writer.write(bigFileInheritanceLine + "\n");
              System.out.println("Writing inheritance line\n\t " + bigFileInheritanceLine);
            }
          }
          String bigfileLine = entry.toBigFileLine(ontology);
          writer.write(bigfileLine + "\n");
        } catch (HpoAnnotationModelException e) {
          System.err.println("Error encountered with entry " + entry.toString());
          e.printStackTrace();
        }
        m++;
      }
    }
    System.out.println("We output a total of " + m + " big file lines from internal HPO Annotation files");
    System.out.println("Total output lines was " + (n + m));
    writer.close();
  }

  /**
   * This method calculates the aspect of a term used for an annotation.
   * The aspect depends on the location of the term in the HPO hierarchy,
   * for instance it is "I" if the term is in the inheritance subhierarchy and it
   * is "P" if the term is in the phenotype subhierarchy.
   *
   * @param tid The term id of an HPO Term
   * @return The Aspect (P,I,C,M) of the term.
   * @throws HpoAnnotationModelException if the term cannot be identified as either P,C,I, or M.
   */


  /**
   * @return Header line for the big file (indicating column names for the data).
   */
  static String getHeaderLine() {
    String[] fields = {"DatabaseID",
      "DiseaseName",
      "Qualifier",
      "HPO_ID",
      "Reference",
      "Evidence",
      "Onset",
      "Frequency",
      "Sex",
      "Modifier",
      "Aspect",
      "Biocuration"};
    return String.join("\t", fields);
  }


}
