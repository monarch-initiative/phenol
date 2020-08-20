package org.monarchinitiative.phenol.annotations.hpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoFrequency;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents one disease-entity annotation consisting usually of multiple annotations lines, and using
 * the new format introduced in 2018. Colloquially, these files have been called "small files". This class
 * is meant to be used for parsing the files, and does not perform any kind of analysis. THe main use case
 * is to hold the data from one HPO Annotation file, such as {@code OMIM-100200.tab}, which in turn will be
 * use to create the aggregated file called {@code phenotype.hpoa} (the "big-file").
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * Created by peter on 1/20/2018.
 */
public class HpoAnnotationModel {
  /**
   * The base name of the HPO Annotation file.
   */
  private final String basename;
  /**
   * List of {@link HpoAnnotationEntry} objects representing the original lines of the small file
   */
  private List<HpoAnnotationEntry> entryList;

  /**
   * These are the databases currently represented in our data resource.
   */
  private enum Database {
    OMIM, DECIPHER, UNKNOWN
  }

  /**
   * What is the source of the current HpoAnnotationModel?
   */
  private final Database database;

  private static final String EMPTY_STRING = "";

  /**
   * To be used for matching n/m frequencies.
   */
  private final static Pattern n_of_m_pattern = Pattern.compile("^(\\d+)/(\\d+?)");

  private final static Pattern percentage_pattern = Pattern.compile("^(\\d*\\.?\\d+)%");

  private final static Pattern hpoTerm_pattern = Pattern.compile("^HP:\\d{7}$");

  /**
   * @return The base name of the HPO Annotation file.
   */
  public String getBasename() {
    return basename;
  }

  /**
   * The constructor creates an immutable copy of the original list of {@link HpoAnnotationEntry} objects
   * provided by the parser
   *
   * @param name    Name of the "small file"
   * @param entries List of {@link HpoAnnotationEntry} objects -- one per line of the small file.
   */
  public HpoAnnotationModel(String name, List<HpoAnnotationEntry> entries) {
    basename = name;
    entryList = ImmutableList.copyOf(entries);
    if (basename.contains("OMIM")) this.database = Database.OMIM;
    else if (basename.contains("DECIPHER")) this.database = Database.DECIPHER;
    else this.database = Database.UNKNOWN;
  }

  public HpoAnnotationModel mergeWithInheritanceAnnotations(Collection<HpoAnnotationEntry> inherit) {
      ImmutableList.Builder<HpoAnnotationEntry> builder = new ImmutableList.Builder<>();
      builder.addAll(this.entryList);
      builder.addAll(inherit);
      return new HpoAnnotationModel(this.basename,builder.build());
  }

  /**
   * Private constructor, intended to be used by {@link #getMergedModel()}
   *
   * @param base    base name of small file
   * @param db      database (OMIM, DECIPHER)
   * @param entries list of (merged) entries.
   */
  private HpoAnnotationModel(String base, Database db, List<HpoAnnotationEntry> entries) {
    this.basename = base;
    this.database = db;
    this.entryList = entries;
  }


  public boolean isOMIM() {
    return this.database.equals(Database.OMIM);
  }

  public boolean isDECIPHER() {
    return this.database.equals(Database.DECIPHER);
  }


  /**
   * @return the {@link HpoAnnotationEntry} objects -- one per line of the small file.
   */
  public List<HpoAnnotationEntry> getEntryList() {
    return entryList;
  }

  public int getNumberOfAnnotations() {
    return entryList.size();
  }



  private boolean divergentNegation(List<HpoAnnotationEntry> entrylist) {
    String firstItemNegated = entrylist.get(0).getNegation();
    if (firstItemNegated == null) firstItemNegated = EMPTY_STRING;
    for (int i = 1; i < entrylist.size(); ++i) {
      if (!firstItemNegated.equals(entrylist.get(i).getNegation())) {
        return true;
      }
    }
    return false; // if we get here we can still merge. Items are not divergent
  }

  private boolean divergentSex(List<HpoAnnotationEntry> entrylist) {
    String firstItemSex = entrylist.get(0).getSex();
    if (firstItemSex == null) firstItemSex = EMPTY_STRING;
    for (int i = 1; i < entrylist.size(); ++i) {
      if (!firstItemSex.equals(entrylist.get(i).getSex())) {
        return true;
      }
    }
    return false; // if we get here we can still merge. Items are not divergent
  }

  private boolean divergentOnset(List<HpoAnnotationEntry> entrylist) {
    String firstItemOnsetId = entrylist.get(0).getAgeOfOnsetId();
    if (firstItemOnsetId == null) firstItemOnsetId = EMPTY_STRING;
    for (int i = 1; i < entrylist.size(); ++i) {
      if (!firstItemOnsetId.equals(entrylist.get(i).getAgeOfOnsetId())) {
        return true;
      }
    }
    return false; // if we get here we can still merge. Items are not divergent
  }

  /**
   * We want to merge entries with different n-of-m frequencies. For instance, if
   * we have 2/3 and 5/7 then we would merge this to 7/10. If one of the entries
   * is not n-of-m, then we will transform it as if the percentage or ontology term
   * represents 10 observations. If the field is empty, then we will assume it is
   * 100%, i.e., 10/10
   *
   * @param entrylist List of frequency strings
   * @return merged frequency string
   */
  private String mergeFrequencies(final List<HpoAnnotationEntry> entrylist) {
    int sum_of_numerators = 0;
    int sum_of_denominators = 0;
    final int DEFAULT_NUMBER_OF_OBSERVATIONS = 10;

    for (HpoAnnotationEntry e : entrylist) {
      String q = e.getFrequencyModifier();
      Matcher matcher = n_of_m_pattern.matcher(q);
      Matcher percentageMatcher = percentage_pattern.matcher(q);
      Matcher termMatcher = hpoTerm_pattern.matcher(q);
      if ( q.isEmpty()) {
        // 1) No frequency entry available. Assume 100%, i.e., 10/10
        sum_of_numerators += DEFAULT_NUMBER_OF_OBSERVATIONS;
        sum_of_denominators += DEFAULT_NUMBER_OF_OBSERVATIONS;
      } else if (matcher.matches()){
        // 2) The frequency string is of the form n/m
        String n_str=matcher.group(1);
        String m_str=matcher.group(2);
        // if we match the regex, the following "must" work.
        int n=Integer.parseInt(n_str);
        int m=Integer.parseInt(m_str);
        sum_of_numerators += n;
        sum_of_denominators += m;
      } else if (percentageMatcher.matches()) {
        String p_str=percentageMatcher.group(1);
        // If we match the regex, the following "must" work
        double d = Double.parseDouble(p_str);
        int n = (int)Math.round(d/10.0);
        sum_of_numerators += n;
        sum_of_denominators += DEFAULT_NUMBER_OF_OBSERVATIONS;
      } else if (termMatcher.matches()){
        TermId freqid = TermId.of(q);
        HpoFrequency hpofreq=HpoFrequency.fromTermId(freqid);
        double proportion = hpofreq.mean();
        int n=(int)Math.round(proportion*10.0);
        sum_of_numerators += n;
        sum_of_denominators += DEFAULT_NUMBER_OF_OBSERVATIONS;
      } else {
        // should never happen but if it does we want to know right away
        throw new PhenolRuntimeException("Could not parse frequency entry: \"" + q+"\"");
      }
    }
    return String.format("%d/%d",sum_of_numerators,sum_of_denominators);
  }

  private String mergeModifiers(final List<HpoAnnotationEntry> entrylist) {
    List<String> modifiers=new ArrayList<>();
    for (HpoAnnotationEntry entry : entrylist) {
      String mod = entry.getModifier();
      if (mod!=null && !mod.isEmpty()) {
        modifiers.add(mod);
      }
    }
    if (modifiers.isEmpty()) {
      return ""; // no modifiers, return empty string
    } else {
      return String.join(";",modifiers);
    }
  }

  private String mergeDescriptions(final List<HpoAnnotationEntry> entrylist) {
    List<String> descriptions=new ArrayList<>();
    for (HpoAnnotationEntry entry : entrylist) {
      String mod = entry.getDescription();
      if (mod!=null && mod.isEmpty()) {
        descriptions.add(mod);
      }
    }
    if (descriptions.isEmpty()) {
      return ""; // no modifiers, return empty string
    } else {
      return String.join(";",descriptions);
    }
  }

  private String mergePublications(final List<HpoAnnotationEntry> entrylist) {
    Set<String> pubs=new HashSet<>();
    for (HpoAnnotationEntry entry : entrylist) {
      pubs.add(entry.getPublication());
    }
    return String.join(";",pubs);
  }

  private String getHighestEvidenceCode(final List<HpoAnnotationEntry> entrylist) {
    String evi="IEA";//default
    for (HpoAnnotationEntry entry : entrylist) {
      if (entry.getEvidenceCode().equals("PCS")) {
        return "PCS";
      } else if (entry.getEvidenceCode().equals("TAS")) {
        evi="TAS"; // better than IEA
      }
    }
    return evi;
  }

  private String mergeBiocuration(final List<HpoAnnotationEntry> entrylist) {
    Set<String> biocuration=new HashSet<>();
    for (HpoAnnotationEntry entry : entrylist) {
      biocuration.add(entry.getBiocuration());
    }
    return String.join(";",biocuration);
  }




  /**
   * If this method is called, then we have checked that Sex, Negation, AgeOfOnset are the same
   * Merge everything else, concatenating biocuration and PMID and modifier and description
   *
   * @param entrylist List of annotation lines to the same HPO term that we will merge
   * @return a merged entry
   */
  private HpoAnnotationEntry mergeEntries(List<HpoAnnotationEntry> entrylist) {
    HpoAnnotationEntry first = entrylist.get(0);
    String diseaseId=first.getDiseaseID();
    String diseaseName=first.getDiseaseName();
    TermId phenoId=first.getPhenotypeId();
    String phenoName=first.getPhenotypeLabel();
    String onsetId=first.getAgeOfOnsetId();
    String onsetName=first.getAgeOfOnsetLabel();
    String mergedFrequency = mergeFrequencies(entrylist);
    String sex=first.getSex();
    String negation=first.getNegation();
    String mergedModifiers=mergeModifiers(entrylist);
    String mergedDescriptions=mergeDescriptions(entrylist);
    String mergedPublications=mergePublications(entrylist);
    String evidence=getHighestEvidenceCode(entrylist);
    String mergedBiocuration=mergeBiocuration(entrylist);
    return new HpoAnnotationEntry(diseaseId,
      diseaseName,
      phenoId,
      phenoName,
      onsetId,
      onsetName,
      mergedFrequency,
      sex,
      negation,
      mergedModifiers,
      mergedDescriptions,
      mergedPublications,
      evidence,
      mergedBiocuration);
  }


  public HpoAnnotationModel getMergedModel() {
    Map<TermId, List<HpoAnnotationEntry>> termId2AnnotEntryListMap = new HashMap<>();
    for (HpoAnnotationEntry entry : this.entryList) {
      termId2AnnotEntryListMap.putIfAbsent(entry.getPhenotypeId(), new ArrayList<>());
      termId2AnnotEntryListMap.get(entry.getPhenotypeId()).add(entry);
    }
    ImmutableList.Builder<HpoAnnotationEntry> builder = new ImmutableList.Builder<>();
    for (TermId tid : termId2AnnotEntryListMap.keySet()) {
      List<HpoAnnotationEntry> entrylist = termId2AnnotEntryListMap.get(tid);
      if (entrylist.size() == 1) { // No duplicate entries for this TermId
        builder.add(entrylist.get(0));
      } else {
        boolean mergable = true;
        // check for things that keep us from merging
        if (divergentNegation(entrylist)) {
          mergable = false;
        } else if (divergentSex(entrylist)) {
          mergable = false;
        } else if (divergentOnset(entrylist)) {
          mergable = false;
        }
        if (mergable) {
          HpoAnnotationEntry merged = mergeEntries(entrylist);
          builder.add(merged);
        } else {
          builder.addAll(entrylist); // cannot merge, add each separately
        }
      }
    }
    return new HpoAnnotationModel(this.basename,builder.build());
  }

  /**
   * By construction, the disease ID field of each of the entries in this object must be the same
   * Therefore, we return the first one. Also by construction, there must be at least one entry
   * in ({@link #entryList} for this object to have been created
   * @return The diseaseID of this model
   */
  public TermId getDiseaseId() {
    HpoAnnotationEntry entry = entryList.iterator().next();
    return TermId.of(entry.getDiseaseID());
  }

  public String getDiseaseName() {
    return entryList
      .stream()
      .map(HpoAnnotationEntry::getDiseaseName)
      .findAny()
      .orElse("n/a");
  }


  public void addInheritanceEntryCollection(Collection<HpoAnnotationEntry> entries) {
      ImmutableList.Builder<HpoAnnotationEntry> builder = new ImmutableList.Builder<>();
      builder.addAll(this.entryList);
      builder.addAll(entries);
      this.entryList = builder.build();
  }


}
