package org.monarchinitiative.phenol.io.owl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import org.geneontology.obographs.model.Meta;
import org.geneontology.obographs.model.Node;
import org.geneontology.obographs.model.meta.BasicPropertyValue;
import org.geneontology.obographs.model.meta.DefinitionPropertyValue;
import org.geneontology.obographs.model.meta.SynonymPropertyValue;
import org.geneontology.obographs.model.meta.XrefPropertyValue;
import org.monarchinitiative.phenol.ontology.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Factory class for constructing {@link Term} and {@link Relationship} objects from
 * Obographs's Nodes.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class Owl2OboTermFactory
    implements OwlOntologyEntryFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(Owl2OboTermFactory.class);

  @Override
  public Term constructTerm(Node node, TermId termId) {
    Term.Builder termBuilder = Term.builder();
    termBuilder.id(termId);
    termBuilder.name(node.getLabel());

    Meta meta = node.getMeta();
    if (meta == null) {
      LOGGER.warn("No meta instance exists for the node: {}", node.getId());
      return termBuilder.build();
    }

    // 1. definition
    DefinitionPropertyValue definition = meta.getDefinition();
    ImmutableList.Builder<SimpleXref> simpleXrefBuilder = new ImmutableList.Builder<>();
    if (definition != null) {
      termBuilder.definition(definition.getVal());
      List<String> xrefs = definition.getXrefs();
      for (String x : xrefs) {
        SimpleXref sxref = new SimpleXref(x);
        if (sxref.isValid()) { // Add Xrefs that we might want to reference later on
          // this includes PMIDs and OMIMs
          simpleXrefBuilder.add(sxref);
        }
      }
      termBuilder.databaseXrefs(simpleXrefBuilder.build());
    }

    // 2. comments
    List<String> comments = meta.getComments();
    if (comments != null) termBuilder.comment(String.join(", ", comments));

    // 3. subsets
    termBuilder.subsets(meta.getSubsets());

    // 4. synonyms
    List<TermSynonym> termSynonyms = mapSynonyms(meta.getSynonyms());
    termBuilder.synonyms(termSynonyms);

    // 5. xrefs
    List<XrefPropertyValue> xrefPropertyValues = meta.getXrefs();
    List<Dbxref> dbxrefs = Lists.newArrayList();
    if (xrefPropertyValues != null) {
      for (XrefPropertyValue xrefPropertyValue : xrefPropertyValues) {
        String val = xrefPropertyValue.getVal();
        if (val == null) continue;
        dbxrefs.add(new Dbxref(val, null, null));
      }
      termBuilder.xrefs(dbxrefs);
    }

    // 6. obsolete; the obsolete/deprecated field in Meta is somehow not accessible,
    // so we use Java reflection to pull the value of that field.
    boolean isObsolete = false;
    try {
      Field f = Meta.class.getDeclaredField("deprecated");
      f.setAccessible(true);
      Boolean deprecated = (Boolean) f.get(meta);
      if (deprecated == null || !deprecated) isObsolete = false;
      else if (deprecated) isObsolete = true;
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
    termBuilder.obsolete(isObsolete);

    // 7. altIds
    List<TermId> altIdList = new ArrayList<>();
    List<BasicPropertyValue> basicPropertyValues = meta.getBasicPropertyValues();
    if (basicPropertyValues != null) {
      for (BasicPropertyValue bpv : basicPropertyValues) {
        if ("http://www.geneontology.org/formats/oboInOwl#hasAlternativeId".equals(bpv.getPred())) {
          String altId = bpv.getVal();
          altIdList.add(TermId.constructWithPrefix(altId));
        }
      }
      termBuilder.altTermIds(altIdList);
    }

    return termBuilder.build();
  }

  // It seems that only actual relation used across ontologies is "IS_A" one for now.
  @Override
  public Relationship constructRelationship(TermId source, TermId dest, int id,  RelationshipType reltype) {
    return new Relationship(source, dest, id, reltype);
  }

  /** @return list of synoynms (can be an empty list but cannot be null). */
  private List<TermSynonym> mapSynonyms(List<SynonymPropertyValue> spvs) {
    if (spvs == null) return ImmutableList.of();
    ImmutableList.Builder<TermSynonym> termSynonymBuilder = new ImmutableList.Builder<>();
    for (SynonymPropertyValue spv : spvs) {

      // Map the scope of Synonym
      TermSynonymScope scope = null;
      String pred = spv.getPred();
      if (pred.equals(SynonymPropertyValue.PREDS.hasExactSynonym.toString())) {
        scope = TermSynonymScope.EXACT;
      } else if (pred.equals(SynonymPropertyValue.PREDS.hasBroadSynonym.toString())) {
        scope = TermSynonymScope.BROAD;
      } else if (pred.equals(SynonymPropertyValue.PREDS.hasNarrowSynonym.toString())) {
        scope = TermSynonymScope.NARROW;
      } else if (pred.equals(SynonymPropertyValue.PREDS.hasRelatedSynonym.toString())) {
        scope = TermSynonymScope.RELATED;
      }

      // Map the synonym's type name.
      String synonymTypeName = String.join(", ", spv.getTypes());

      // Map the synonym's cross-references.
      List<String> xrefs = spv.getXrefs();
      List<TermXref> termXrefs = mapXref(xrefs);

      TermSynonym its = new TermSynonym(spv.getVal(), scope, synonymTypeName, termXrefs);
      termSynonymBuilder.add(its);
    }

    return termSynonymBuilder.build();
  }

  /**
   * We try to map the cross references to Curies, e.g., ORCID:0000-0000-0000-0123.
   * If a cross-reference is not in CURIE for, we just ignore it. For now we
   * use an empty string for the Description field of the cross-reference.
   * @param xrefs list of cross references as Strings
   * @return list of cross references as {@link TermXref} objects. Can be empty but not null.
   */
  private List<TermXref> mapXref(List<String>  xrefs) {
    ImmutableList.Builder<TermXref> termXrefBuilder = new ImmutableList.Builder<>();
    for (String xref : xrefs) {
      try {
        TermId xrefTermId = TermId.constructWithPrefix(xref);
        TermXref trf = new TermXref(xrefTermId,"");
        termXrefBuilder.add(trf);
      } catch (Exception e) {
        // ignore
      }
    }

    return termXrefBuilder.build();
  }
}
