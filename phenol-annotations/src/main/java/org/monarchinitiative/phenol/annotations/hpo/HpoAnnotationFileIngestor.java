package org.monarchinitiative.phenol.annotations.hpo;


import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.BufferedReader;
import java.io.File;
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
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
@Deprecated(forRemoval = true)
public class HpoAnnotationFileIngestor {
  /**
   * Reference to the HPO object.
   */
  private final Ontology ontology;
  /**
   * The paths to all of the small files, e.g., OMIM-600301.tab.
   */
  private final List<File> smallFilePaths;
  /**
   * List of all of the {@link HpoAnnotationModel} objects, which represent annotated diseases.
   */
  private final List<HpoAnnotationModel> smallFileList = new ArrayList<>();
  /**
   * Names of entries (small files) that we will omit because they do not represent diseases.
   */
  private final Set<String> omitEntries;
  /**
   * Total number of annotations of all of the annotation files.
   */
  private int n_total_annotation_lines = 0;

  private int n_total_omitted_entries = 0;
  /**
   * Merge entries with the same phenotype-disease association but different metadata for the big file.
   */
  private boolean mergeEntries = false;

  private final List<String> errors = new ArrayList<>();

  List<HpoAnnotationModel> getSmallFileEntries() {
    return smallFileList;
  }

  /**
   * Default constructor. Will NOT merge annotation lines to same HPO term.
   *
   * @param directoryPath path to the directory with HPO annotation "small files"
   * @param omitFile      path to the {@code omit-list.txt} file with non-disease entries to be omitted
   * @param ontology      reference to HPO ontology object
   */
  public HpoAnnotationFileIngestor(String directoryPath, String omitFile, Ontology ontology) {
    this(directoryPath, omitFile, ontology, false);
  }

  HpoAnnotationFileIngestor(String directoryPath, Ontology ontology) {
    this(directoryPath,
      String.format("%s%s%s", directoryPath, File.separator, "omit-list.txt"),
      ontology,
      false);
  }
  public HpoAnnotationFileIngestor(String directoryPath, Ontology ontology, boolean merge_fr) {
    this(directoryPath,
      String.format("%s%s%s", directoryPath, File.separator, "omit-list.txt"),
      ontology,
      merge_fr);
  }

  /**
   * @param directoryPath path to the directory with HPO annotation "small files"
   * @param omitFile      path to the {@code omit-list.txt} file with non-disease entries to be omitted
   * @param ontology      reference to HPO ontologt object
   * @param merge         Should we merge small file lines with the same HPO but different metadata?
   */
  public HpoAnnotationFileIngestor(String directoryPath, String omitFile, Ontology ontology, boolean merge) {
    omitEntries = getOmitEntries(omitFile);
    this.mergeEntries = merge;
    smallFilePaths = getListOfV2SmallFiles(directoryPath);
    this.ontology = ontology;
    inputHpoAnnotationFiles();
  }

  private void inputHpoAnnotationFiles() {
    int i = 0;
    for (File file : smallFilePaths) {
      HpoAnnotationFileParser parser = new HpoAnnotationFileParser(file.getAbsolutePath(), ontology);
      try {
        HpoAnnotationModel smallFile = parser.parse(true);
        if (mergeEntries) {
          smallFile = smallFile.getMergedModel();
        }
        n_total_annotation_lines += smallFile.getNumberOfAnnotations();
        smallFileList.add(smallFile);
      } catch (HpoAnnotationModelException hafe) {
        System.err.printf("[ERROR] %s: (%s)\n", file.getName(), hafe.getMessage());
      } catch (PhenolRuntimeException pre) {
        System.err.printf("[ERROR] PhenolRuntimeException: with file %s: %s", file, pre.getMessage());
        throw pre;

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
   * If this file is not passed (is null or empty), then we return the empty set.
   *
   * @param path the path to {@code omit-list.txt}
   * @return List of entries (encoded as strings like "OMIM:600123") that should be omitted
   */
  private Set<String> getOmitEntries(String path) {
    if (path == null || path.isEmpty())
      return Set.of();

    Set<String> entrylist = new HashSet<>();
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      String line;
      while ((line = br.readLine()) != null) {
        if (line.startsWith("#")) continue; // skip comment
        String[] A = line.split("\\s+");
        String id = A[0]; // the first field has items such as OMIM:500123
        entrylist.add(id);
      }
    } catch (IOException e) {
      e.printStackTrace();
      errors.add(e.getMessage());
    }
    return entrylist;
  }

  int get_omitted_entry_count() {
    return this.n_total_omitted_entries;
  }

  int get_valid_smallfile_count() {
    return this.smallFilePaths.size();
  }

  /**
   * Get the entry Curie for a certain path
   *
   * @param path e.g., /.../rare-diseases/annotated/OMIM-600123.tab
   * @return the corresinding Curie, e.g., OMIM:600123
   */
  private String baseName(Path path) {
    String bname = path.getFileName().toString();
    bname = bname.replace('-', ':').replace(".tab", "");
    return bname;
  }


  private List<File> getListOfV2SmallFiles(String smallFileDirectory) {
    List<File> fileNames = new ArrayList<>();
    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(smallFileDirectory))) {
      for (Path path : directoryStream) {
        if (path.toString().endsWith(".tab")) {
          String basename = baseName(path);
          if (omitEntries.contains(basename)) {
            n_total_omitted_entries++;
            continue; // skip this one!
          }
          fileNames.add(new File(path.toString()));
        }
      }
    } catch (IOException ex) {
      errors.add(String.format("Could not get list of small smallFilePaths from %s [%s]. Terminating...",
        smallFileDirectory, ex));
    }
    return fileNames;
  }

}
