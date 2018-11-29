package org.monarchinitiative.phenol.io.obo.hpo;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.io.obo.DbXrefList;
import org.monarchinitiative.phenol.ontology.data.*;
import org.obolibrary.oboformat.model.*;
import org.obolibrary.oboformat.parser.OBOFormatConstants;
import org.obolibrary.oboformat.parser.OBOFormatConstants.OboFormatTag;
import org.obolibrary.oboformat.parser.OBOFormatParser;

public class HpOboParserTest {

  private HpoOntology ontology;

  @Before
  public void setUp() throws PhenolException {
    ClassLoader classLoader = this.getClass().getClassLoader();
    final HpOboParser parser = new HpOboParser(classLoader.getResourceAsStream("hp_head.obo"));
    ontology = parser.parse();
  }

  @Test
  public void testParseHpoHead() {
    final DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();
    assertNotNull(graph);
  }

  @Test
  public void testGetRightNumberOfTerms() {
    int expectedTermCount = 22; // there are 22 non-obsolete terms in hp_head.obo
    assertEquals(expectedTermCount, ontology.countAllTerms());
  }

  @Test
  public void testGetRootTerm() {
    TermId rootId = TermId.of("HP:0000001");
    assertEquals(rootId, ontology.getRootTermId());
  }

  @Test
  public void testGetNonRootTerms() {
    // outside of the root these four terms are in the hp_head file
    TermId tid1 = TermId.of("HP:0000005");
    TermId tid2 = TermId.of("HP:0000006");
    TermId tid3 = TermId.of("HP:0000007");
    TermId tid4 = TermId.of("HP:0000118");
    Map<TermId, Term> termMap = ontology.getTermMap();
    assertTrue(termMap.containsKey(tid1));
    assertTrue(termMap.containsKey(tid2));
    assertTrue(termMap.containsKey(tid3));
    assertTrue(termMap.containsKey(tid4));
    Term term1 = termMap.get(tid1);
    System.out.println(term1);
  }


  @Ignore
  @Test
  public void testParseFullHpo() throws Exception {
//    for (int i = 0; i < 10; i++) {
      HpOboParser parser = new HpOboParser(Paths.get("src/test/resources/hp.obo").toAbsolutePath().toFile());
      System.out.println("Starting full HPO load");
      Instant start = Instant.now();
      HpoOntology ontology = parser.parse();
      Instant end = Instant.now();
      System.out.println("Finished in " + Duration.between(start, end).toMillis() + " ms");
//      ontology.getTerms().stream().limit(1240).forEach(System.out::println);
//    }
  }

  @Ignore
  @Test
  public void testParseFullHpoWithOboFormatParser() throws Exception {
//    for (int i = 0; i < 10; i++) {
    System.out.println("Starting full HPO load");
    Instant start = Instant.now();
    OBOFormatParser oboFormatParser = new OBOFormatParser();
    OBODoc oboDoc = oboFormatParser.parse(Paths.get("src/test/resources/hp.obo").toAbsolutePath().toFile());

    // Term ids of non-obsolete Terms
    Collection<TermId> nonObsoleteTermIds = Sets.newHashSet();
    // Term ids of obsolete Terms
    Collection<TermId> obsoleteTermIds = Sets.newHashSet();
    // Key: a TermId; value: corresponding Term object
    Map<TermId, Term> termsMap = Maps.newTreeMap();

    // convert header
//    Map<String, String> metaInfo =
    oboDoc.getHeaderFrame();
    List<Term> terms = oboDoc.getTermFrames().stream().map(toTerm()).collect(Collectors.toList());

    // create
    Map<Integer, Relationship> relationshipMap = Maps.newHashMap();
    AtomicInteger edgeId = new AtomicInteger(1);
    List<Relationship> relationships = oboDoc.getTermFrames().stream().map(toRelationship(edgeId)).flatMap(Collection::stream).collect(Collectors.toList());

    System.out.println(relationships);

    Instant end = Instant.now();
    System.out.println("Finished in " + Duration.between(start, end).toMillis() + " ms");
//    }
  }

  private Function<Frame, List<Relationship>> toRelationship(AtomicInteger edgeId) {
    return frame -> {
      List<Relationship> relationships = new ArrayList<>();
      TermId subjectId = null;

      for (Clause clause : frame.getClauses()) {
        Object value = clause.getValue();
        OboFormatTag tag = OBOFormatConstants.getTag(clause.getTag());
        if (tag == null) {
          continue;
        }
        switch (tag) {
          case TAG_ID:
            //use curieUtil
            subjectId = TermId.of((String) value);
            break;
          case TAG_IS_A:
            relationships.add(createRelationship(edgeId, subjectId, (String) value, RelationshipType.IS_A));
            break;
          case TAG_INTERSECTION_OF:
            relationships.add(createRelationship(edgeId, subjectId, (String) value, RelationshipType.INTERSECTION_OF));
            break;
          case TAG_UNION_OF:
            relationships.add(createRelationship(edgeId, subjectId, (String) value, RelationshipType.UNION_OF));
            break;
          case TAG_EQUIVALENT_TO:
            // ironically, there is no phenol equivalent
            break;
          case TAG_DISJOINT_FROM:
            relationships.add(createRelationship(edgeId, subjectId, (String) value, RelationshipType.DISJOINT_FROM));
            break;
          case TAG_INVERSE_OF:
            relationships.add(createRelationship(edgeId, subjectId, (String) value, RelationshipType.INVERSE_OF));
            break;
          case TAG_TRANSITIVE_OVER:
            // No phenol equivalent
            break;
          case TAG_DISJOINT_OVER:
            // No phenol equivalent
            break;
          default:
            break;
        }
      }
      return relationships;
    };
  }

  private Relationship createRelationship(AtomicInteger edgeId, TermId subjectId, String value, RelationshipType relationshipType) {
    //use curieUtil
    TermId objectId = TermId.of(value);
    return new Relationship(subjectId, objectId, edgeId.incrementAndGet(), relationshipType);
  }

  private Function<Frame, Term> toTerm() {
    return frame -> {
      ImmutableList.Builder<TermSynonym> synonyms = new ImmutableList.Builder<>();
      ImmutableList.Builder<Dbxref> xrefs = new ImmutableList.Builder<>();
      ImmutableList.Builder<SimpleXref> simpleXrefs = new ImmutableList.Builder<>();
      ImmutableList.Builder<TermId> altIds = new ImmutableList.Builder<>();
      ImmutableList.Builder<String> subsets = new ImmutableList.Builder<>();

      Term.Builder termBuilder = Term.builder();
//      System.out.println();
      for (Clause clause : frame.getClauses()) {
//        System.out.printf("%s=%s%n", clause.getTag(), clause.getValue());
        // roll on Java 12!
        Object value = clause.getValue();
        OboFormatTag tag = OBOFormatConstants.getTag(clause.getTag());
        if (tag == null) {
          continue;
        }
        switch (tag) {
          case TAG_ID:
            //use curieUtil
            termBuilder.id(TermId.of((String) value));
            break;
          case TAG_NAME:
            termBuilder.name((String) value);
            break;
          case TAG_IS_OBSELETE:
            termBuilder.obsolete(true);
            break;
          case TAG_ALT_ID:
            altIds.add(TermId.of((String) value));
            break;
          case TAG_DEF:
            termBuilder.definition((String) value);
            break;
          case TAG_SUBSET:
            subsets.add((String) value);
            break;
          case TAG_SYNONYM:
            synonyms.add(mapToTermSynonym(clause));
            break;
          case TAG_COMMENT:
            termBuilder.comment((String) value);
            break;
          case TAG_XREF:
            Xref xref = (Xref) value;
            xrefs.add(new Dbxref(xref.getIdref(), xref.getAnnotation(), ImmutableMap.of()));
            break;
          //TODO: is_a: HP:0005916 ?
          case TAG_DATE:
            try{
              Date creationDate = Date.from(Instant.parse((String) value));
              termBuilder.creationDate(creationDate);
            } catch (Exception e) {
              // ignore this
            }
            break;
          case TAG_CREATED_BY:
            termBuilder.createdBy((String) value);
            break;
          default:
            break;
        }
      }
      // finish adding the collections to the termBuilder
      Term term = termBuilder
        .altTermIds(altIds.build())
        .xrefs(xrefs.build())
        .databaseXrefs(simpleXrefs.build())
        .synonyms(synonyms.build())
        .subsets(subsets.build())

        .build();

//      System.out.println(term);
      return term;
    };
  }

  private TermSynonym mapToTermSynonym(Clause clause) {
    // synonym: "Hyperextensible finger" EXACT layperson []
    // synonym: "Hyperextensible fingers" RELATED layperson []
    // HP:0001230 synonym: "Wide long bones of hand" EXACT layperson [ORCID:0000-0001-5208-3432]
    TermSynonymScope termSynonymScope = null;
    String synonymTypeName = null;
    ImmutableList.Builder<TermXref> termXrefs = new ImmutableList.Builder<>();
    Object[] values = clause.getValues().toArray();
    for (int i = 0; i < values.length ; i++) {
      if (i == 1) {
        termSynonymScope = mapTermSynonymScope(values[i]);
      }
      if (i == 2) {
        synonymTypeName = values[i].toString();
      }
      if (i == 3) {
        String[] xrefsStrings = values[i].toString().replace("\\[", "").replace("]", "").split(",");
        for (String xref : xrefsStrings) {
          try {
            TermId xrefTermId = TermId.of(xref);
            termXrefs.add(new TermXref(xrefTermId,""));
          } catch (Exception e) {
            // ignore
          }
        }
      }
    }
    String value = (String) clause.getValue();
    return new TermSynonym(value, termSynonymScope, synonymTypeName, termXrefs.build());
  }

  private TermSynonymScope mapTermSynonymScope(Object value) {
    OboFormatTag synonymTag = OBOFormatConstants.getTag(value.toString());
    if (synonymTag  == null) {
      return null;
    }
    switch (synonymTag) {
      case TAG_EXACT:
        return TermSynonymScope.EXACT;
      case TAG_BROAD:
        return TermSynonymScope.BROAD;
      case TAG_NARROW:
        return TermSynonymScope.NARROW;
      case TAG_RELATED:
        return TermSynonymScope.RELATED;
    }
    return null;
  }

}
