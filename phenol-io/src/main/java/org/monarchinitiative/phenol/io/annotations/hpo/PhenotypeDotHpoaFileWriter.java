package org.monarchinitiative.phenol.io.annotations.hpo;

import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationEntry;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationModel;
import org.monarchinitiative.phenol.base.HpoAnnotationModelException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.existsPath;

/**
 * This class coordinates writing out the {@code phenotype.hpoa}, the so-called "big file", which is
 * aggregated from the ca. 7000 small files.
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class PhenotypeDotHpoaFileWriter {
    /** List of all of the {@link HpoAnnotationModel} objects from our annotations (OMIM and DECIPHER). */
    private final List<HpoAnnotationModel> internalAnnotFileList;
    /** List of all of the {@link HpoAnnotationModel} objects derived from the Orphanet XML file. */
    private final List<HpoAnnotationModel> orphanetSmallFileList;
    /**Usually "phenotype.hpoa", but may also include path. */
    private final String outputFileName;
    private final static String EMPTY_STRING="";
    private static final TermId phenotypeRoot= TermId.of("HP:0000118");
    private static final TermId INHERITANCE_TERM_ID =TermId.of("HP:0000005");
    private static final TermId CLINICAL_COURSE_ID =TermId.of("HP:0031797");
    private static final TermId CLINICAL_MODIFIER_ID =TermId.of("HP:0012823");
    private final Ontology ontology;
    /** Number of annotated Orphanet entries. */
    private int n_orphanet;
    private int n_decipher;
    private int n_omim;
    /** Number of database sources that could not be identified (should be zero!). */
    private int n_unknown;

    private Map<String,String> ontologyMetaInfo;



    public PhenotypeDotHpoaFileWriter(Ontology ont, List<HpoAnnotationModel> internalAnnotFileList, List<HpoAnnotationModel> orphaList, String outpath) {
        this.ontology=ont;
        this.internalAnnotFileList =internalAnnotFileList;
        this.orphanetSmallFileList=orphaList;
        this.outputFileName =outpath;
    }


    /** In the header of the {@code phenotype.hpoa} file, we write the
     * number of OMIM, Orphanet, and DECIPHER entries. This is calculated
     * here (except for Orphanet).
     * @param n_orpha number of Orphanet entries to be included in the big file
     */
    public void setNumberOfDiseasesForHeader(int n_orpha) {
        this.n_orphanet=n_orpha;
        this.n_decipher=0;
        this.n_omim=0;
        this.n_unknown=0;
        for (HpoAnnotationModel diseaseModel : internalAnnotFileList) {
            if (diseaseModel.isOMIM()) n_omim++;
            else if (diseaseModel.isDECIPHER()) n_decipher++;
            else n_unknown++;
        }
        this.n_orphanet = orphanetSmallFileList.size();
    }




    public void setOntologyMetadata(Map<String,String> meta) { this.ontologyMetaInfo=meta;}

    private String getDate() {
        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        return ft.format(dNow);
    }

    /**
     * Output the {@code phenotype.hpoa} file on the basis of the "small files" and the Orphanet XML file.
     * @throws IOException if we cannot write to file.
     */
    public void outputBigFile() throws IOException {
        String description = String.format("#description: HPO annotations for rare diseases [%d: OMIM; %d: DECIPHER; %d ORPHANET]",n_omim,n_decipher,n_orphanet);
        if (n_unknown>0) description=String.format("%s -- warning: %d entries could not be assigned to a database",description,n_unknown);
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
        writer.write(description + "\n");
        writer.write(String.format("#date: %s\n",getDate()));
        writer.write("#tracker: https://github.com/obophenotype/human-phenotype-ontology\n");
        if (ontologyMetaInfo.containsKey("data-version")) {
            writer.write(String.format("#HPO-version: %s\n",ontologyMetaInfo.get("data-version")));
        }
        if (ontologyMetaInfo.containsKey("saved-by")) {
            writer.write(String.format("#HPO-contributors: %s\n",ontologyMetaInfo.get("saved-by")));
        }
        int n = 0;
        writer.write(getHeaderLine() + "\n");
        for (HpoAnnotationModel smallFile : this.internalAnnotFileList) {
            List<HpoAnnotationEntry> entryList = smallFile.getEntryList();
            for (HpoAnnotationEntry entry : entryList) {
                try {
                    String bigfileLine = transformEntry2BigFileLine(entry);
                    writer.write(bigfileLine + "\n");
                } catch (HpoAnnotationModelException e) {
                    System.err.println("Error encountered with entry " + entry.toString());
                    e.printStackTrace();
                }
                n++;
            }
        }
        System.out.println("We output a total of " + n + " big file lines from internal HPO Annotation files");
        int m=0;
        for (HpoAnnotationModel smallFile : this.orphanetSmallFileList) {
            List<HpoAnnotationEntry> entryList = smallFile.getEntryList();
            for (HpoAnnotationEntry entry : entryList) {
                try {
                    String bigfileLine = transformEntry2BigFileLine(entry);
                    writer.write(bigfileLine + "\n");
                } catch (HpoAnnotationModelException e) {
                    System.err.println("Error encountered with entry " + entry.toString());
                    e.printStackTrace();
                }
                m++;
            }
        }
        System.out.println("We output a total of " + m + " big file lines from internal HPO Annotation files");
        System.out.println("Total output lines was " + (n+m));
        writer.close();
    }


    /**
     * Transform one line from a Small File (represented as a {@link HpoAnnotationEntry} object)
     * into one line of the Big File.
     * @param entry Representing a line from a Small File
     * @return A string that will be one line of the Big file
     * @throws HpoAnnotationModelException if the Aspect of the line cannot be determined
     */
    String transformEntry2BigFileLine(HpoAnnotationEntry entry) throws HpoAnnotationModelException {

        String [] elems = {
                entry.getDiseaseID(), //DB_Object_ID
                entry.getDiseaseName(), // DB_Name
                entry.getNegation(), // Qualifier
                entry.getPhenotypeId().getValue(), // HPO_ID
                entry.getPublication(), // DB_Reference
                entry.getEvidenceCode(), // Evidence_Code
                entry.getAgeOfOnsetId()!=null?entry.getAgeOfOnsetId():EMPTY_STRING, // Onset
                entry.getFrequencyModifier()!=null?entry.getFrequencyModifier():EMPTY_STRING, // Frequency
                entry.getSex(), // Sex
                entry.getModifier(), // Modifier
                getAspect(entry.getPhenotypeId()), // Aspect
                entry.getBiocuration() // Biocuration
        };
        return String.join("\t",elems);
    }

    /**
     * This method calculates the aspect of a term used for an annotation.
     * The aspect depends on the location of the term in the HPO hierarchy,
     * for instance it is "I" if the term is in the inheritance subhierarchy and it
     * is "P" if the term is in the phenotype subhierarchy.
     * @param tid The term id of an HPO Term
     * @return The Aspect (P,I,C,M) of the term.
     * @throws HpoAnnotationModelException if the term cannot be identified as either P,C,I, or M.
     */
    private String getAspect(TermId tid) throws HpoAnnotationModelException {
        Term term = ontology.getTermMap().get(tid);
        if (term==null) {
            throw new HpoAnnotationModelException("Cannot compute Aspect of NULL term");
        }
        TermId primaryTid = term.getId(); // update in case term is an alt_id
        if (existsPath(ontology, primaryTid, phenotypeRoot)) {
            return "P"; // organ/phenotype abnormality
        } else if (existsPath(ontology, primaryTid, INHERITANCE_TERM_ID)) {
            return "I";
        } else if (existsPath(ontology, primaryTid, CLINICAL_COURSE_ID)) {
            return "C";
        } else if (existsPath(ontology,primaryTid,CLINICAL_MODIFIER_ID)) {
            return "M";
        } else {
            throw new HpoAnnotationModelException("Could not determine aspect of TermId "+tid.getValue() );
        }
    }
    /**
     * @return Header line for the big file (indicating column names for the data).
     */
    static String getHeaderLine() {
        String []fields={"DatabaseID",
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
        return String.join("\t",fields);
    }




}
