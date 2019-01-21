package org.monarchinitiative.phenol.annotations.hpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.util.*;
import java.util.regex.Pattern;

/**
 * This class represents one disease-entity annotation consisting usually of multiple annotations lines, and using
 * the new format introduced in 2018. Colloquially, these files have been called "small files". This class
 * is meant to be used for parsing the files, and does not perform any kind of analysis. THe main use case
 * is to hold the data from one HPO Annotation file, such as {@code OMIM-100200.tab}, which in turn will be
 * use to create the aggregated file called {@code phenotype.hpoa} (the "big-file").
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * Created by peter on 1/20/2018.
 */
public class HpoAnnotationModel {
    /** The base name of the HPO Annotation file. */
    private final String basename;
    /** List of {@link HpoAnnotationEntry} objects representing the original lines of the small file */
    private final List<HpoAnnotationEntry> entryList;
    /** These are the databases currently represented in our data resource. */
    private enum Database {OMIM,DECIPHER,UNKNOWN}
    /** What is the source of the current HpoAnnotationModel? */
    private final Database database;

  /** To be used for matching n/m frequencies. */
  private final static Pattern n_of_m_pattern = Pattern.compile("^(\\d+)/(\\d+?)");

    /** @return The base name of the HPO Annotation file.*/
    public String getBasename() { return basename; }

    /** The constructor creates an immutable copy of the original list of {@link HpoAnnotationEntry} objects
     * provided by the parser
     * @param name Name of the "small file"
     * @param entries List of {@link HpoAnnotationEntry} objects -- one per line of the small file.
     */
    public HpoAnnotationModel(String name, List<HpoAnnotationEntry> entries) {
        basename=name;
        entryList = ImmutableList.copyOf(entries);
        if (basename.contains("OMIM")) this.database=Database.OMIM;
        else if (basename.contains("DECIPHER")) this.database=Database.DECIPHER;
        else this.database=Database.UNKNOWN;
    }

  /**
   * Private constructor, intended to be used by {@link #getMergedModel()}
   * @param base base name of small file
   * @param db database (OMIM, DECIPHER)
   * @param entries list of (merged) entries.
   */
    private HpoAnnotationModel(String base, Database db, List<HpoAnnotationEntry> entries) {
      this.basename=base;
      this.database=db;
      this.entryList=entries;
    }


    public boolean isOMIM(){ return this.database.equals(Database.OMIM); }
    public boolean isDECIPHER() { return this.database.equals(Database.DECIPHER);}


    /** @return the {@link HpoAnnotationEntry} objects -- one per line of the small file.*/
    public List<HpoAnnotationEntry> getEntryList() {
        return entryList;
    }

    public int getNumberOfAnnotations() { return entryList.size(); }

    private static final String EMPTY_STRING="";

    private boolean divergentNegation(List<HpoAnnotationEntry> entrylist) {
      String firstItemNegated = entrylist.get(0).getNegation();
      if (firstItemNegated==null) firstItemNegated=EMPTY_STRING;
      for (int i=1;i<entrylist.size();++i) {
        if (! firstItemNegated.equals(entrylist.get(i).getNegation()) ) {
          return true;
        }
      }
      return false; // if we get here we can still merge. Items are not divergent
    }

  private boolean divergentSex(List<HpoAnnotationEntry> entrylist) {
    String firstItemSex = entrylist.get(0).getSex();
    if (firstItemSex==null) firstItemSex=EMPTY_STRING;
    for (int i=1;i<entrylist.size();++i) {
      if (! firstItemSex.equals(entrylist.get(i).getSex()) ) {
        return true;
      }
    }
    return false; // if we get here we can still merge. Items are not divergent
  }

  private boolean divergentOnset(List<HpoAnnotationEntry> entrylist) {
    String firstItemOnsetId = entrylist.get(0).getAgeOfOnsetId();
    if (firstItemOnsetId==null) firstItemOnsetId=EMPTY_STRING;
    for (int i=1;i<entrylist.size();++i) {
      if (! firstItemOnsetId.equals(entrylist.get(i).getAgeOfOnsetId()) ) {
        return true;
      }
    }
    return false; // if we get here we can still merge. Items are not divergent
  }

  /**
   * We want to merge entries with different n-of-m frequencies. For instance, if
   * we have 2/3 and 5/7 then we would merge this to 7/10. If one of the entries
   * is not n-of-m, then for now we will not merge, but we can recosider this later
   * @param entrylist
   * @return
   */
  private Optional<String> mergeFrequencies(List<HpoAnnotationEntry> entrylist) {
    List<String> frequencyStringList = new ArrayList<>();
    for (HpoAnnotationEntry e : entrylist) {
      frequencyStringList.add(e.getFrequencyModifier());
    }

    int sum_of_numerators=0;
    int sum_of_denominators=0;
  return Optional.empty();
  }

  /**
   * If this method is called, then we have checked that Sex, Negation, AgeOfOnset are the same
   * Merge everything else, concatenating biocuration and PMID and modifier and description
   * @param entrylist
   * @return
   */
  private HpoAnnotationEntry mergeEntries(List<HpoAnnotationEntry> entrylist) {
    HpoAnnotationEntry first = entrylist.get(0);

      /*
       HpoAnnotationEntry entry = new HpoAnnotationEntry(diseaseID,
      diseaseName,
      phenotypeId,
      phenotypeName,
      ageOfOnsetId,
      ageOfOnsetName,
      frequencyString,
      sex,
      negation,
      modifier,
      description,
      publication,
      evidenceCode,
      biocuration);
       */
      return null;
  }


    public HpoAnnotationModel getMergedModel() {
      Map<TermId,List<HpoAnnotationEntry>> termId2AnnotEntryListMap=new HashMap<>();
      for (HpoAnnotationEntry entry : this.entryList) {
        termId2AnnotEntryListMap.putIfAbsent(entry.getPhenotypeId(),new ArrayList<>());
        termId2AnnotEntryListMap.get(entry.getPhenotypeId()).add(entry);
      }
      ImmutableList.Builder builder = new ImmutableList.Builder();
      for (TermId tid : termId2AnnotEntryListMap.keySet()) {
        List<HpoAnnotationEntry> entrylist = termId2AnnotEntryListMap.get(tid);
        if (entrylist.size()==1) { // No duplicate entries for this TermId
          builder.add(entrylist.get(0));
        } else {
            boolean mergable=true;
            // check for things that keep us from merging
          if (divergentNegation(entrylist)) {
            mergable=false;
          } else if (divergentSex(entrylist)) {
            mergable=false;
          } else if (divergentOnset(entrylist)) {
            mergable=false;
          }
          Optional<String> frequenyOpt = mergeFrequencies(entrylist);
          if (mergable) {
            HpoAnnotationEntry merged = mergeEntries(entrylist);
            builder.add(merged);
          } else {
            builder.addAll(entrylist); // cannot merge, add each separately
          }

        }
      }
      return null;
    }




}
