package org.monarchinitiative.phenol.ontology.testdata.hpo;

import org.monarchinitiative.phenol.ontology.data.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class HpoOntologyTestBase {
  protected static final Ontology ontology;
  protected static final TermId PHENOTYPIC_ABNORMALITY = TermId.of("HP:0000118"); //
  // Heart
  protected static final TermId CARDIOVASCULAR_ID = TermId.of("HP:0001626");
  protected static final TermId ABN_CARDIOVASCULAR_PHYSIOLOGY = TermId.of("HP:0011025");
  protected static final TermId ABN_HEART_SOUND =  TermId.of("HP:0031657");
  protected static final TermId HEART_MURMUR =  TermId.of("HP:0030148");
  protected static final TermId GALLOP_RHYTHM =  TermId.of("HP:0033113");
  protected static final TermId SYNCOPE =  TermId.of("HP:0001279");
  protected static final TermId VASOVAGAL_SYNCOPE =  TermId.of("HP:0012668");
  protected static final TermId ORTHOSTATIC_SYNCOPE =  TermId.of("HP:0012670");
  // EYE
  protected static final TermId EYE_ID = TermId.of("HP:0000478");
  protected static final TermId ABN_EYE_MORPHOLOGY = TermId.of("HP:0012372");
  protected static final TermId COLOBOMA = TermId.of("HP:0000589");
  protected static final TermId RETINAL_COLOBOMA = TermId.of("HP:0000480");
  protected static final TermId IRIS_COLOBOMA = TermId.of("HP:0000612");
  protected static final TermId ABN_GLOBE_LOCATION = TermId.of("HP:0100886");
  protected static final TermId PROPTOSIS = TermId.of("HP:0000520");
  protected static final TermId HYPERTELORISM = TermId.of("HP:0000316");

  protected static final List<ToyHpoAnnotation> hpoAnnotations;

  protected static final List<TermId> disease1annotations;
  protected static final List<TermId> disease2annotations;

  private static final AtomicInteger counter = new AtomicInteger(0);

  static {
    Map<TermId, Term> termMapBuilder = new HashMap<>();
    Map<Integer, Relationship> relationMapBuilder = new HashMap<>();
    termMapBuilder.put(PHENOTYPIC_ABNORMALITY, createTerm(PHENOTYPIC_ABNORMALITY, "Phenotypic abnormality"));
    termMapBuilder.put(CARDIOVASCULAR_ID, createTerm(CARDIOVASCULAR_ID, "Abnormality of the cardiovascular system"));
    termMapBuilder.put(ABN_CARDIOVASCULAR_PHYSIOLOGY, createTerm(ABN_CARDIOVASCULAR_PHYSIOLOGY, "Abnormal cardiovascular system physiology"));
    termMapBuilder.put(ABN_HEART_SOUND, createTerm(ABN_HEART_SOUND, "Abnormal heart sound"));
    termMapBuilder.put(HEART_MURMUR, createTerm(HEART_MURMUR, "Heart murmur"));
    termMapBuilder.put(GALLOP_RHYTHM, createTerm(GALLOP_RHYTHM, "Gallop rhythm"));
    termMapBuilder.put(SYNCOPE, createTerm(SYNCOPE, "Syncope"));
    termMapBuilder.put(VASOVAGAL_SYNCOPE, createTerm(VASOVAGAL_SYNCOPE, "Vasovagal syncope"));
    termMapBuilder.put(ORTHOSTATIC_SYNCOPE, createTerm(ORTHOSTATIC_SYNCOPE, "Orthostatic syncope"));
    termMapBuilder.put(EYE_ID, createTerm(EYE_ID, "Abnormality of the eye"));
    termMapBuilder.put(ABN_EYE_MORPHOLOGY, createTerm(ABN_EYE_MORPHOLOGY, "Abnormal eye morphology"));
    termMapBuilder.put(COLOBOMA, createTerm(COLOBOMA, "Coloboma"));
    termMapBuilder.put(RETINAL_COLOBOMA, createTerm(RETINAL_COLOBOMA, "Retinal coloboma"));
    termMapBuilder.put(IRIS_COLOBOMA, createTerm(IRIS_COLOBOMA, "Iris coloboma"));
    termMapBuilder.put(ABN_GLOBE_LOCATION, createTerm(ABN_GLOBE_LOCATION, "Abnormality of globe location"));
    termMapBuilder.put(PROPTOSIS, createTerm(PROPTOSIS, "Proptosis"));
    termMapBuilder.put(HYPERTELORISM, createTerm(HYPERTELORISM, "Hypertelorism"));
    Map<TermId, Term> termMap = Map.copyOf(termMapBuilder);

    createRelationship(relationMapBuilder, CARDIOVASCULAR_ID, PHENOTYPIC_ABNORMALITY);
    createRelationship(relationMapBuilder, ABN_CARDIOVASCULAR_PHYSIOLOGY, CARDIOVASCULAR_ID);
    createRelationship(relationMapBuilder, ABN_HEART_SOUND, ABN_CARDIOVASCULAR_PHYSIOLOGY);
    createRelationship(relationMapBuilder, HEART_MURMUR, ABN_HEART_SOUND);
    createRelationship(relationMapBuilder, GALLOP_RHYTHM, ABN_HEART_SOUND);
    createRelationship(relationMapBuilder, SYNCOPE, ABN_CARDIOVASCULAR_PHYSIOLOGY);
    createRelationship(relationMapBuilder, VASOVAGAL_SYNCOPE, SYNCOPE);
    createRelationship(relationMapBuilder, ORTHOSTATIC_SYNCOPE, SYNCOPE);
    // Eye
    createRelationship(relationMapBuilder, EYE_ID, PHENOTYPIC_ABNORMALITY);
    createRelationship(relationMapBuilder, ABN_EYE_MORPHOLOGY, EYE_ID);
    createRelationship(relationMapBuilder, COLOBOMA, ABN_EYE_MORPHOLOGY);
    createRelationship(relationMapBuilder, RETINAL_COLOBOMA, COLOBOMA);
    createRelationship(relationMapBuilder, IRIS_COLOBOMA, COLOBOMA);
    createRelationship(relationMapBuilder, ABN_GLOBE_LOCATION, ABN_EYE_MORPHOLOGY);
    createRelationship(relationMapBuilder, PROPTOSIS, ABN_GLOBE_LOCATION);
    createRelationship(relationMapBuilder, HYPERTELORISM, ABN_GLOBE_LOCATION);

    Map<Integer, Relationship> relationMap = Map.copyOf(relationMapBuilder);

    ontology = ImmutableOntology.builder()
      .metaInfo(Map.of())
      .terms(termMap.values())
      .relationships(relationMap.values())
      .build();

    hpoAnnotations = List.of(
      new ToyHpoAnnotation(IRIS_COLOBOMA, "disease1"),
      new ToyHpoAnnotation(IRIS_COLOBOMA, "disease2"),
      new ToyHpoAnnotation(IRIS_COLOBOMA, "disease3"),
      new ToyHpoAnnotation(IRIS_COLOBOMA, "disease4"),
      new ToyHpoAnnotation(RETINAL_COLOBOMA, "disease4"),
      new ToyHpoAnnotation(RETINAL_COLOBOMA, "disease6"),
      new ToyHpoAnnotation(HYPERTELORISM, "disease1"),
      new ToyHpoAnnotation(PROPTOSIS, "disease1"),
      new ToyHpoAnnotation(PROPTOSIS, "disease7"),
      new ToyHpoAnnotation(ORTHOSTATIC_SYNCOPE, "disease8"),
      new ToyHpoAnnotation(GALLOP_RHYTHM, "disease1"),
      new ToyHpoAnnotation(HEART_MURMUR, "disease2"),
      new ToyHpoAnnotation(HEART_MURMUR, "disease5"),
      new ToyHpoAnnotation(HEART_MURMUR, "disease7"));

    disease1annotations = List.of(IRIS_COLOBOMA, HYPERTELORISM, PROPTOSIS, GALLOP_RHYTHM);
    disease2annotations = List.of(IRIS_COLOBOMA, HEART_MURMUR);

  }


  private static void createRelationship(Map<Integer, Relationship> relationshipMap, TermId source, TermId target) {
    int id = counter.incrementAndGet();
    relationshipMap.put(id, new Relationship(source, target, id, RelationshipType.IS_A));
  }


  private static Term createTerm(TermId tid, String label) {
    return Term.builder(tid)
      .name(label)
      .definition("n/a")
      .build();
  }

}
