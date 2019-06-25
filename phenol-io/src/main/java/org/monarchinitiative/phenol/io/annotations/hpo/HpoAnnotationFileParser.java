package org.monarchinitiative.phenol.io.annotations.hpo;

import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationEntry;
import org.monarchinitiative.phenol.annotations.hpo.HpoAnnotationModel;
import org.monarchinitiative.phenol.base.HpoAnnotationModelException;
import org.monarchinitiative.phenol.base.ObsoleteTermIdException;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Parse of a single HPO Annotation File into a {@link HpoAnnotationModel} object. The HPO project uses a single
 * tab-separated file with 14 fields (see {@link #expectedFields}) to store information about individual
 * diseases. Colloquially, we have called these files "small-files" to distinguish them from the
 * {@code phenotype.hpoa} file that is created by combining the information from all ca. 7000 small files
 * (and which colloquially we have called the "big-file").
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * Created by peter on 2/05/2018.
 */
public class HpoAnnotationFileParser {
    /** A reference to the HPO Ontology object. */
    private final Ontology ontology;
    /** Path to a file such as "OMIM-600123.tab" containing data about the phenotypes of a disease. */
    private final String pathToHpoAnnotationFile;
    /** The column names of the small file. */
    private static final String[] expectedFields = {
            "#diseaseID",
            "diseaseName",
            "phenotypeID",
            "phenotypeName",
            "onsetID",
            "onsetName",
            "frequency",
            "sex",
            "negation",
            "modifier",
            "description",
            "publication",
            "evidence",
            "biocuration"};
    /** Number of tab-separated fields in a valid small file. */
    private static final int NUMBER_OF_FIELDS=expectedFields.length;
    /** A list of all erroneous Small File lines encountered during parsing */
    private List<String> parseErrors;



    /**
     * Set up parser for an individual HPO Annotation file ("small file")
     * @param path Path to the HPO annotation file
     * @param ontology reference to HPO Ontology object
     */
    public HpoAnnotationFileParser(String path, Ontology ontology) {
        pathToHpoAnnotationFile =path;
        this.ontology=ontology;
    }


    /**
     * Parse a single HPO Annotation file. If {@code faultTolerant} is set to true, then we will parse as
     * much as we can of an annotation file and return the {@link HpoAnnotationModel} object, even if one or more
     * parse errors occured. Otherwise, an {@link HpoAnnotationModelException} will be thrown
     * @param faultTolerant If true, report errors to STDERR but do not throw an exception
     * @return A {@link HpoAnnotationModel} object corresponding to the data in the HPO Annotation file
     * @throws HpoAnnotationModelException if faultTolerant is false, parse errors are not thrown, rather only IO exceptions are thrown
     */
    public HpoAnnotationModel parse(boolean faultTolerant) throws HpoAnnotationModelException {
        String basename=(new File(pathToHpoAnnotationFile).getName());
        List<HpoAnnotationEntry> entryList=new ArrayList<>();
        this.parseErrors=new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(pathToHpoAnnotationFile));
            String line=br.readLine();
            qcHeaderLine(line);
            while ((line=br.readLine())!=null) {
                try {
                    HpoAnnotationEntry entry = HpoAnnotationEntry.fromLine(line, ontology);
                    entryList.add(entry);
                } catch (ObsoleteTermIdException obsE) {
                    // try to rescue obsolete termid!
                    Optional<HpoAnnotationEntry> entryOpt = HpoAnnotationEntry.fromLineReplaceObsoletePhenotypeData(line, ontology);
                    entryOpt.ifPresent(entryList::add);
                } catch (PhenolException e) {
                    parseErrors.add(String.format("%s:%s", pathToHpoAnnotationFile,e.getMessage()));
                }
            }
            br.close();
            if (parseErrors.size()>0) {
              String errstr= String.join("\n",parseErrors);
              if (faultTolerant) {
                  System.err.println(String.format("234Errors encountered while parsing HPO Annotation file at %s.\n%s",
                          pathToHpoAnnotationFile,errstr));
                  System.err.println(errstr);
              } else {
                  throw new HpoAnnotationModelException(String.format("Errors encountered while parsing HPO Annotation file at %s.\n%s",
                          pathToHpoAnnotationFile, errstr));
              }
            }
            return new HpoAnnotationModel(basename,entryList);
        } catch (IOException e) {
            throw new HpoAnnotationModelException(String.format("Error parsing %s: %s", pathToHpoAnnotationFile, e.getMessage()));
        }
    }

    /**
     * Parse a single HPO Annotation file with the default setting of no fault-tolerance, i.e. if even a single parse
     * error is encountered, throw an {@link HpoAnnotationModelException}.
     * @throws HpoAnnotationModelException if any parse error of IO problem is encountered.
     */
    public HpoAnnotationModel parse() throws HpoAnnotationModelException {
        return parse(false);
    }

    /**
     * Can be used with fault-tolerant parsing to determine if parse errors were encountered.
     * @return true if one or more parse errors occured
     */
    public boolean hasErrors() { return parseErrors.size()>0; }

    /**
     * @return A slit of strings describing all parse errors (can be empty but not null)
     */
    public List<String> getParseErrors() { return parseErrors; }


     /**
      * This method checks that the nead has the expected number and order of lines.
     * If it doesn't, then a serious error has occured somewhere and it is better to
     * die and figure out what is wrong than to attempt error correction
     * @param line a header line of a V2 small file
     * @throws HpoAnnotationModelException if the number of fields in the head is not equal to {@link #NUMBER_OF_FIELDS} or if a column header is incorrect
     */
    private void qcHeaderLine(String line) throws HpoAnnotationModelException {
      String[] fields = line.split("\t");
        if (fields.length != NUMBER_OF_FIELDS) {
            String msg = String.format("Malformed header line\n"+line+
            "\nExpecting %d fields but got %d", NUMBER_OF_FIELDS, fields.length);
            throw new HpoAnnotationModelException(msg);
        }
        for (int i=0;i<fields.length;i++) {
            if (! fields[i].equals(expectedFields[i])) {
                throw new HpoAnnotationModelException(String.format("Malformed field %d. Expected %s but got %s",
                        i,expectedFields[i],fields[i]));
            }
        }
        // if we get here, all is good
    }


}
