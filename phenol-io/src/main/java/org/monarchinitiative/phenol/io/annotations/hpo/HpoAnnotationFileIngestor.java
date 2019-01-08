package org.monarchinitiative.phenol.io.annotations.hpo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.monarchinitiative.hpoannotqc.exception.HpoAnnotationModelException;
import org.monarchinitiative.hpoannotqc.smallfile.HpoAnnotationModel;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class coordinates the input of all the HPO Annotation files ("small files"). If an
 * {@code omit-list.txt} is provided by the user, then these files are
 * omitted. The output of this class is a list of {@link HpoAnnotationModel} objects
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class HpoAnnotationFileIngestor {
    private static final Logger logger = LogManager.getLogger();
    /** Reference to the HPO object. */
    private final HpoOntology ontology;
    /** The paths to all of the v2 small files. */
    private final List<String> v2smallFilePaths;
    /** List of all of the {@link HpoAnnotationModel} objects, which represent annotated diseases. */
    private final List<HpoAnnotationModel> v2SmallFileList =new ArrayList<>();
    /** Names of entries (small files) that we will omit because they do not represent diseases. */
    private final Set<String> omitEntries;

    private final List<String> parseErrors=new ArrayList<>();

    /** Total number of annotations of all of the annotation files. */
    private int n_total_annotation_lines=0;

    private int n_total_omitted_entries=0;

    private final List<String> errors = new ArrayList<>();

    public List<HpoAnnotationModel> getV2SmallFileEntries() {
        return v2SmallFileList;
    }

    public HpoAnnotationFileIngestor(String directoryPath, String omitFile, HpoOntology ontology) {
        omitEntries=getOmitEntries(omitFile);
        v2smallFilePaths=getListOfV2SmallFiles(directoryPath);
        this.ontology=ontology;
        inputHpoAnnotationFiles();
    }

    private void inputHpoAnnotationFiles() {
        logger.trace("We found " + v2smallFilePaths.size() + " small files.");
        int i=0;
        for (String path : v2smallFilePaths) {
            if (++i%1000==0) {
                logger.trace(String.format("Inputting %d-th file at %s",i,path));
            }
            HpoAnnotationFileParser parser=new HpoAnnotationFileParser(path,ontology);
            try {
                HpoAnnotationModel v2sf = parser.parse();
                n_total_annotation_lines += v2sf.getNumberOfAnnotations();
                v2SmallFileList.add(v2sf);
            } catch (HpoAnnotationModelException hafe) {
                logger.error("Errors encountered with V2 small file at {}: {}",path, hafe.getMessage());
            }
        }
        logger.info("Finished with input of {} files with {} annotations",i,n_total_annotation_lines);
        logger.info("A total of {} entries found in the small file directory were omitted.",n_total_omitted_entries);
        if (! parseErrors.isEmpty()) {
            logger.error("Parsing errors were encountered while ingesting the small files");
            int k=0;
            for (String err: parseErrors) {
                k++;
                logger.error("("+k+") "+err);
            }
        }
    }

    /**
     * This is the format of the omit-list.txt file.
     * Thus, we need to extract only the first field.
     * <pre></pre>
     * #List of OMIM entries that we want to omit from further analysis
     * #DiseaseId    Reason
     * OMIM:107850   trait
     * OMIM:147320   legacy
     * </pre>
     * @param path the path to {@code omit-list.txt}
     * @return List of entries (encoded as strings like "OMIM:600123") that should be omitted
     */
    private Set<String> getOmitEntries(String path) {
        Set<String> entrylist=new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line=br.readLine())!=null) {
                if (line.startsWith("#")) continue; // skip comment
                String A[] = line.split("\\s+");
                String id = A[0]; // the first field has items such as OMIM:500123
                entrylist.add(id);
            }
        } catch (IOException e) {
            e.printStackTrace();
            errors.add(e.getMessage());
        }
        return entrylist;
    }

    /**
     * Get the entry Curie for a certain path
     * @param path e.g., /.../rare-diseases/annotated/OMIM-600123.tab
     * @return the corresinding Curie, e.g., OMIM:600123
     */
    private String baseName(Path path) {
        String bname=path.getFileName().toString();
        bname=bname.replace('-',':').replace(".tab","");
        return bname;
    }


    private List<String> getListOfV2SmallFiles(String v2smallFileDirectory) {
        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(v2smallFileDirectory))) {
            for (Path path : directoryStream) {
                if (path.toString().endsWith(".tab")) {
                    String basename=baseName(path);
                    if (omitEntries.contains(basename)) {
                        logger.trace("Skipping annotations for omitted entry {}", basename);
                        n_total_omitted_entries++;
                        continue; // skip this one!
                    }
                    fileNames.add(path.toString());
                }
            }
        } catch (IOException ex) {
            errors.add(String.format("Could not get list of small v2smallFilePaths from %s [%s]. Terminating...",
                    v2smallFileDirectory,ex));
        }
        return fileNames;
    }

}
