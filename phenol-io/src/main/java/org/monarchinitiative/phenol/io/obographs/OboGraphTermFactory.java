package org.monarchinitiative.phenol.io.obographs;

import java.util.ArrayList;
import java.util.List;

import org.geneontology.obographs.core.model.Meta;
import org.geneontology.obographs.core.model.Node;
import org.geneontology.obographs.core.model.meta.BasicPropertyValue;
import org.geneontology.obographs.core.model.meta.DefinitionPropertyValue;
import org.geneontology.obographs.core.model.meta.SynonymPropertyValue;
import org.geneontology.obographs.core.model.meta.XrefPropertyValue;
import org.monarchinitiative.phenol.ontology.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class for constructing {@link Term} and {@link Relationship} objects from
 * Obographs's Nodes.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
class OboGraphTermFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(OboGraphTermFactory.class);

  private OboGraphTermFactory(){}

  static Term constructTerm(Node node, TermId termId) {
    Term.Builder termBuilder = Term.builder(termId);
    String label = node.getLabel();
    // labels for obsolete terms ids found in the alt_id section are null
    termBuilder.name(label == null ? "" : label);

    Meta meta = node.getMeta();
    if (meta == null) {
      // this isn't really a problem
      LOGGER.debug("No meta instance exists for node: {} {}", node.getId(), node.getLabel());
      return termBuilder.build();
    }

    // 1. definition
    DefinitionPropertyValue definitionPropertyValue = meta.getDefinition();
    String definition = getDefinition(definitionPropertyValue);
    termBuilder.definition(definition);

    List<SimpleXref> simpleXrefs = convertToXrefs(definitionPropertyValue);
    termBuilder.databaseXrefs(simpleXrefs);

    // 2. comments
    List<String> comments = meta.getComments();
    if (comments != null) {
      termBuilder.comment(String.join(", ", comments));
    }

    // 3. subsets
    List<String> subsets = meta.getSubsets();
    if (subsets != null) {
      termBuilder.subsets(subsets);
    }

    // 4. synonyms
    List<TermSynonym> termSynonyms = convertToSynonyms(meta.getSynonyms());
    termBuilder.synonyms(termSynonyms);

    // 5. xrefs
    List<Dbxref> xrefs = convertToDbXrefs(meta.getXrefs());
    termBuilder.xrefs(xrefs);

    // 6. obsolete; the obsolete/deprecated field in Meta is somehow not accessible,
    // so we use Java reflection to pull the value of that field.
    boolean isObsolete = isObsolete(meta);
    termBuilder.obsolete(isObsolete);

    // 7. altIds
    List<TermId> altIds = convertToAltIds(meta.getBasicPropertyValues());
    termBuilder.altTermIds(altIds);



    return termBuilder.build();
  }

  private static String getDefinition(DefinitionPropertyValue definitionPropertyValue) {
    return (definitionPropertyValue == null)? "" : definitionPropertyValue.getVal();
  }

  private static List<SimpleXref> convertToXrefs(DefinitionPropertyValue definitionPropertyValue) {
    if (definitionPropertyValue == null) {
      return List.of();
    }

    List<String> xrefs = definitionPropertyValue.getXrefs();
    if (xrefs == null) {
      return List.of();
    }

    List<SimpleXref> simpleXrefBuilder = new ArrayList<>();
    for (String xref : xrefs) {
      SimpleXref sxref = new SimpleXref(xref);
      if (sxref.isValid()) { // Add Xrefs that we might want to reference later on
        // this includes PMIDs and OMIMs
        simpleXrefBuilder.add(sxref);
      }
    }
    return List.copyOf(simpleXrefBuilder);
  }

  /** @return list of synoynms (can be an empty list but cannot be null). */
  private static List<TermSynonym> convertToSynonyms(List<SynonymPropertyValue> spvs) {
    if (spvs == null) return List.of();
    List<TermSynonym> termSynonymBuilder = new ArrayList<>();
    for (SynonymPropertyValue spv : spvs) {

      // Map the scope of Synonym
      TermSynonymScope scope = null;
      if (spv.isExact()) {
        scope = TermSynonymScope.EXACT;
      } else if (spv.isBroad()) {
        scope = TermSynonymScope.BROAD;
      } else if (spv.isNarrow()) {
        scope = TermSynonymScope.NARROW;
      } else if (spv.isRelated()) {
        scope = TermSynonymScope.RELATED;
      }
      String synonymType = spv.getSynonymType();

      // Map the synonym's type name.
      String synonymTypeName = String.join(", ", spv.getTypes());

      // Map the synonym's cross-references.
      List<String> xrefs = spv.getXrefs();
      List<TermXref> termXrefs = mapXref(xrefs);

      TermSynonym its = new TermSynonym(spv.getVal(), scope, synonymTypeName, termXrefs, synonymType);
      termSynonymBuilder.add(its);
    }

    return List.copyOf(termSynonymBuilder);
  }
  /**
   * We try to map the cross references to Curies, e.g., ORCID:0000-0000-0000-0123.
   * If a cross-reference is not in CURIE for, we just ignore it. For now we
   * use an empty string for the Description field of the cross-reference.
   * @param xrefs list of cross references as Strings
   * @return list of cross references as {@link TermXref} objects. Can be empty but not null.
   */
  private static List<TermXref> mapXref(List<String>  xrefs) {
    List<TermXref> termXrefBuilder = new ArrayList<>();
    for (String xref : xrefs) {
      try {
        TermId xrefTermId = TermId.of(xref);
        TermXref trf = new TermXref(xrefTermId,"");
        termXrefBuilder.add(trf);
      } catch (Exception e) {
        // ignore
      }
    }

    return List.copyOf(termXrefBuilder);
  }

  private static List<Dbxref> convertToDbXrefs(List<XrefPropertyValue> xrefPropertyValues) {
    if (xrefPropertyValues == null || xrefPropertyValues.isEmpty()) {
      return List.of();
    }

    List<Dbxref> dbxrefs = new ArrayList<>();
    for (XrefPropertyValue xrefPropertyValue : xrefPropertyValues) {
      String val = xrefPropertyValue.getVal();
      if (val == null) continue;
      dbxrefs.add(new Dbxref(val, null, null));
    }
    return List.copyOf(dbxrefs);
  }


  private static boolean isObsolete(Meta meta) {
    try {
      return meta.getDeprecated();
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
    return false;
  }

  private static List<TermId> convertToAltIds(List<BasicPropertyValue> basicPropertyValues) {
    if (basicPropertyValues == null || basicPropertyValues.isEmpty()) {
      return List.of();
    }

    List<TermId> altIdsBuilder = new ArrayList<>();
    for (BasicPropertyValue bpv : basicPropertyValues) {
      if ("http://www.geneontology.org/formats/oboInOwl#hasAlternativeId".equals(bpv.getPred())) {
        String altId = bpv.getVal();
        altIdsBuilder.add(TermId.of(altId));
      }
    }
    return List.copyOf(altIdsBuilder);
  }

}
