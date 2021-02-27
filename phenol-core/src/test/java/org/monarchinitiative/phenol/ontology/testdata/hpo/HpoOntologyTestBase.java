package org.monarchinitiative.phenol.ontology.testdata.hpo;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import org.monarchinitiative.phenol.ontology.data.*;

import java.util.AbstractMap;
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

  protected static final ImmutableMap.Builder<TermId, Term> termMapBuilder = ImmutableMap.builder();

  protected static final ImmutableMap.Builder<Integer, Relationship> relationMapBuilder = ImmutableMap.builder();

  protected static final List<ToyHpoAnnotation> hpoAnnotations;

  protected static final AtomicInteger counter = new AtomicInteger(0);

  static {
    termMapBuilder.put(createTerm(PHENOTYPIC_ABNORMALITY, "Phenotypic abnormality"));
    termMapBuilder.put(createTerm(CARDIOVASCULAR_ID, "Abnormality of the cardiovascular system"));
    termMapBuilder.put(createTerm(ABN_CARDIOVASCULAR_PHYSIOLOGY,"Abnormal cardiovascular system physiology"));
    termMapBuilder.put(createTerm(ABN_HEART_SOUND, "Abnormal heart sound"));
    termMapBuilder.put(createTerm(HEART_MURMUR, "Heart murmur"));
    termMapBuilder.put(createTerm(GALLOP_RHYTHM, "Gallop rhythm"));
    termMapBuilder.put(createTerm(SYNCOPE, "Syncope"));
    termMapBuilder.put(createTerm(VASOVAGAL_SYNCOPE, "Vasovagal syncope"));
    termMapBuilder.put(createTerm(ORTHOSTATIC_SYNCOPE, "Orthostatic syncope"));
    termMapBuilder.put(createTerm(EYE_ID, "Abnormality of the eye"));
    termMapBuilder.put(createTerm(ABN_EYE_MORPHOLOGY, "Abnormal eye morphology"));
    termMapBuilder.put(createTerm(COLOBOMA, "Coloboma"));
    termMapBuilder.put(createTerm(RETINAL_COLOBOMA, "Retinal coloboma"));
    termMapBuilder.put(createTerm(IRIS_COLOBOMA, "Iris coloboma"));
    termMapBuilder.put(createTerm(ABN_GLOBE_LOCATION, "Abnormality of globe location"));
    termMapBuilder.put(createTerm(PROPTOSIS, "Proptosis"));
    termMapBuilder.put(createTerm(HYPERTELORISM, "Hypertelorism"));
    ImmutableMap<TermId, Term> termMap = termMapBuilder.build();

    relationMapBuilder.put(createRelationship(CARDIOVASCULAR_ID, PHENOTYPIC_ABNORMALITY));
    relationMapBuilder.put(createRelationship(ABN_CARDIOVASCULAR_PHYSIOLOGY, CARDIOVASCULAR_ID));
    relationMapBuilder.put(createRelationship(ABN_HEART_SOUND, ABN_CARDIOVASCULAR_PHYSIOLOGY));
    relationMapBuilder.put(createRelationship(HEART_MURMUR,ABN_HEART_SOUND));
    relationMapBuilder.put(createRelationship(GALLOP_RHYTHM,ABN_HEART_SOUND));
    relationMapBuilder.put(createRelationship(SYNCOPE, ABN_CARDIOVASCULAR_PHYSIOLOGY));
    relationMapBuilder.put(createRelationship(VASOVAGAL_SYNCOPE, SYNCOPE));
    relationMapBuilder.put(createRelationship(ORTHOSTATIC_SYNCOPE, SYNCOPE));
    // Eye
    relationMapBuilder.put(createRelationship(EYE_ID, PHENOTYPIC_ABNORMALITY));
    relationMapBuilder.put(createRelationship(ABN_EYE_MORPHOLOGY, EYE_ID));
    relationMapBuilder.put(createRelationship(COLOBOMA, ABN_EYE_MORPHOLOGY));
    relationMapBuilder.put(createRelationship(RETINAL_COLOBOMA, COLOBOMA));
    relationMapBuilder.put(createRelationship(IRIS_COLOBOMA, COLOBOMA));
    relationMapBuilder.put(createRelationship(ABN_GLOBE_LOCATION, ABN_EYE_MORPHOLOGY));
    relationMapBuilder.put(createRelationship(PROPTOSIS, ABN_GLOBE_LOCATION));
    relationMapBuilder.put(createRelationship(HYPERTELORISM, ABN_GLOBE_LOCATION));

    ImmutableMap<Integer, Relationship> relationMap = relationMapBuilder.build();

    ontology = ImmutableOntology.builder()
      .metaInfo(ImmutableSortedMap.of())
      .terms(termMap.values())
      .relationships(relationMap.values())
      .build();

    hpoAnnotations =
      Lists.newArrayList(
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

  }



  private static Map.Entry<Integer, Relationship> createRelationship(TermId source, TermId target) {
    int id = counter.incrementAndGet();
    return new AbstractMap.SimpleEntry<>(id, Relationship.IS_A(source, target,id));
  }


  private static Map.Entry<TermId, Term> createTerm(TermId tid, String label) {
    final String definition = "n/a";
    Term t = Term.builder()
        .id(tid)
        .name(label)
        .definition(definition)
        .build();
    return new AbstractMap.SimpleEntry<>(tid,t);
  }

}
