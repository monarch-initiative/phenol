package org.monarchinitiative.phenol.io.obo.hpo;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.*;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.ontology.data.*;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.monarchinitiative.phenol.formats.hpo.HpoModeOfInheritanceTermIds.INHERITANCE_ROOT;

/**
 * This class parses the phenotype_annotation.tab file into a collection of HpoDisease objects. Note
 * that for now this class does not correspond to the design pattern in phenol for annotation
 * parsers. It makes more sense to me to have the parser construct the desired disease models rather
 * than return a list of annotation lines. The annotation lines and their semantics are very
 * different for the various pheno-databases that it does not make much sense to try to make a
 * common data pattern--that is not what seems to be useful. THis class therefore is intended to be
 * a replacement for HpoDiseaseAnnotationParser but will almost certainly require refactoring in the
 * future.
 */
public class HpoDiseaseAnnotationParser {

  private String annotationFilePath = null;
  private final HpoOntology ontology;
  private Ontology<HpoTerm, HpoRelationship> hpoPhenotypeOntology = null;
  private Ontology<HpoTerm, HpoRelationship> inheritancePhenotypeOntology = null;

  // private static final TermPrefix HP_PREFIX = new ImmutableTermPrefix("HP");

  private Map<String, HpoDiseaseWithMetadata> diseaseMap;

  public HpoDiseaseAnnotationParser(String annotationFile, HpoOntology ontlgy) {
    this.annotationFilePath = annotationFile;
    this.ontology = ontlgy;
    this.hpoPhenotypeOntology = this.ontology.getPhenotypicAbnormalitySubOntology();
    this.inheritancePhenotypeOntology = this.ontology.subOntology(INHERITANCE_ROOT);
    this.diseaseMap = new HashMap<>();
  }

  /** */
  public Map<String, HpoDiseaseWithMetadata> parse() throws PhenolException {
    // First stage of parsing is to get the lines parsed and sorted according to disease.
    Map<String, List<HpoAnnotationLine>> disease2AnnotLineMap = new HashMap<>();

    try {
      BufferedReader br = new BufferedReader(new FileReader(this.annotationFilePath));
      String line = br.readLine();
      if (!HpoAnnotationLine.isValidHeaderLine(line)) {
        br.close();
        throw new PhenolException(
            String.format(
                "Annotation file at %s has invalid header (%s)", annotationFilePath, line));
      }
      while ((line = br.readLine()) != null) {
        //System.out.println(line);
        HpoAnnotationLine aline = new HpoAnnotationLine(line);
        List<HpoAnnotationLine> annots;
        if (disease2AnnotLineMap.containsKey(aline.getDiseaseId())) {
          annots = disease2AnnotLineMap.get(aline.getDiseaseId());
        } else {
          annots = new ArrayList<>();
          disease2AnnotLineMap.put(aline.getDiseaseId(), annots);
        }
        annots.add(aline);
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // When we get down here, we have added all of the disease annotations to the disease2AnnotLineMap
    // Now we want to transform them into HpoDisease objects
    for (String diseaseId : disease2AnnotLineMap.keySet()) {
      List<HpoAnnotationLine> annots = disease2AnnotLineMap.get(diseaseId);
      final ImmutableList.Builder<HpoTermId> phenoListBuilder = ImmutableList.builder();
      final ImmutableList.Builder<TermId> inheritanceListBuilder = ImmutableList.builder();
      final ImmutableList.Builder<TermId> negativeTermListBuilder = ImmutableList.builder();
      String diseaseName = null;
      String database = null;
      for (HpoAnnotationLine line : annots) {
        if (isInheritanceTerm(line.getPhenotypeId())) {
          inheritanceListBuilder.add(line.getPhenotypeId());
        } else if (line.isNOT()) {
          negativeTermListBuilder.add(line.getPhenotypeId());
        } else {
          double frequency = getFrequency(line.getFrequency());
          List<TermId> modifiers = getModifiers(line.getModifierList());
          HpoOnset onset = getOnset(line.getOnsetId());
          HpoTermId tidm =
              new ImmutableHpoTermId(line.getPhenotypeId(), frequency, onset, modifiers);
          phenoListBuilder.add(tidm);
        }
        if (line.getDbObjectName() != null) diseaseName = line.getDbObjectName();
        if (line.getDatabase() != null) database = line.getDatabase();
      }
      HpoDiseaseWithMetadata hpoDisease =
          new HpoDiseaseWithMetadata(
              diseaseName,
              database,
              diseaseId,
              phenoListBuilder.build(),
              inheritanceListBuilder.build(),
              negativeTermListBuilder.build());
      this.diseaseMap.put(hpoDisease.getDiseaseDatabaseId(), hpoDisease);
    }
    return diseaseMap;
  }

  /**
   * Check whether a term is a member of the inheritance subontology. ToDo implement this with the
   * termmap once we are using the new ontolib version
   *
   * @param tid A term to be checked
   * @return true of tid is an inheritance term
   */
  private boolean isInheritanceTerm(TermId tid) {
    return inheritancePhenotypeOntology.getAncestorTermIds(tid) != null
        && inheritancePhenotypeOntology.getAncestorTermIds(tid).contains(INHERITANCE_ROOT);
  }

  /**
   * @param lst List of semicolon separated HPO term ids from the modifier subontology
   * @return Immutable List of {@link TermId} objects
   */
  private List<TermId> getModifiers(String lst) {
    ImmutableList.Builder<TermId> builder = new ImmutableList.Builder<>();
    if (lst == null || lst.isEmpty()) return builder.build(); //return empty list
    String modifierTermStrings[] = lst.split(";");
    for (String mt : modifierTermStrings) {
      TermId mtid = ImmutableTermId.constructWithPrefix(mt.trim());
      builder.add(mtid);
    }
    return builder.build();
  }

  /**
   * Go from HP:0000123 to the corresponding TermId
   *
   * @param hp String version of an HPO term id
   * @return corresponding {@link TermId} object
   */
  private TermId string2TermId(String hp) {
    if (!hp.startsWith("HP:")) {
      return null;
    } else {
      TermId tid = ImmutableTermId.constructWithPrefix(hp);
      if (hpoPhenotypeOntology.getTermMap().containsKey(tid)) {
        return hpoPhenotypeOntology
            .getTermMap()
            .get(tid)
            .getId(); // replace alt_id with current if if necessary
      } else if (inheritancePhenotypeOntology.getTermMap().containsKey(tid)) {
        return inheritancePhenotypeOntology.getTermMap().get(tid).getId();
      } else {
        return null;
      }
    }
  }

  /**
   * Extract the {@link HpoFrequency} object that corresponds to the frequency modifier in an
   * annotation line. If we find nothing or there is some parsing error, return the default
   * frequency (obligate, 100%).
   *
   * @param freq The representation of the frequency, if any, in the {@code
   *     phenotype_annotation.tab} file
   * @return the corresponding {@link HpoFrequency} object or the default {@link HpoFrequency}
   *     object (100%).
   */
  private double getFrequency(String freq) {
    if (freq == null || freq.isEmpty()) HpoFrequency.ALWAYS_PRESENT.mean();
    int i = freq.indexOf("%");
    if (i > 0) {
      return 0.01 * Double.parseDouble(freq.substring(0, i));
    }
    i = freq.indexOf("/");
    if (i > 0 && freq.length() > (i + 1)) {
      int n = Integer.parseInt(freq.substring(0, i));
      int m = Integer.parseInt(freq.substring(i + 1));
      return (double) n / (double) m;
    }
    try {
      TermId tid = string2TermId(freq);
      if (tid != null) return HpoFrequency.fromTermId(tid).mean();
    } catch (Exception e) {
      e.printStackTrace();
    }
    // if we get here we could not parse the Frequency, return the default 100%
    return HpoFrequency.ALWAYS_PRESENT.mean();
  }

  private HpoOnset getOnset(TermId ons) {
    if (ons == null) return null;
    return HpoOnset.fromTermId(ons);
  }
}
