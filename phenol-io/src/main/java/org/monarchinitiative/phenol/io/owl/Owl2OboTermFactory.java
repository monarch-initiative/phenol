package org.monarchinitiative.phenol.io.owl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import org.geneontology.obographs.model.Meta;
import org.geneontology.obographs.model.Node;
import org.geneontology.obographs.model.meta.BasicPropertyValue;
import org.geneontology.obographs.model.meta.DefinitionPropertyValue;
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
    Term genericTerm = new Term();
    genericTerm.setId(termId);
    genericTerm.setName(node.getLabel());

    Meta meta = node.getMeta();
    if (meta == null) {
      LOGGER.warn("No meta instance exists for the node: " + node.getId());
      return genericTerm;
    }

    // 1. definition
    DefinitionPropertyValue definition = meta.getDefinition();
    List<SimpleXref> database_cross_ref_list=new ArrayList<>();
    if (definition != null) {
      genericTerm.setDefinition(definition.getVal());
      List<String> xrefs = definition.getXrefs();
      for (String x : xrefs) {
        SimpleXref sxref = new SimpleXref(x);
        if (sxref.isValid()) {
          database_cross_ref_list.add(sxref);
        } else {
          LOGGER.warn("[ERROR] invalid database cross ref: " + x);
        }
      }
      genericTerm.setDatabaseXrefs(database_cross_ref_list);
    } else {
      genericTerm.setDefinition("");
      genericTerm.setDatabaseXrefs(ImmutableList.of());
    }


    // 2. comments
    List<String> comments = meta.getComments();
    if (comments != null) genericTerm.setComment(String.join(", ", comments));

    // 3. subsets
    genericTerm.setSubsets(meta.getSubsets());

    // 4. synonyms
    List<TermSynonym> termSynonyms = SynonymMapper.mapSynonyms(meta.getSynonyms());
    genericTerm.setSynonyms(termSynonyms);

    // 5. xrefs
    List<XrefPropertyValue> xrefPVList = meta.getXrefs();
    List<Dbxref> dbxrefList = Lists.newArrayList();
    if (xrefPVList != null) {
      for (XrefPropertyValue xrefPV : xrefPVList) {
        String val = xrefPV.getVal();
        if (val == null) continue;
        dbxrefList.add(new Dbxref(val, null, null));
      }
      if (!dbxrefList.isEmpty()) genericTerm.setXrefs(dbxrefList);
    }

    // 6. obsolete; the obsolete/deprecated field in Meta is somehow not accessible,
    // so we use Java reflection to pull the value of that field.
    boolean isObsolete = false;
    try {
      Field f = Meta.class.getDeclaredField("deprecated");
      f.setAccessible(true);
      Boolean deprecated = (Boolean) f.get(meta);
      if (deprecated == null || ! deprecated ) isObsolete = false;
      else if (deprecated) isObsolete = true;
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
    genericTerm.setObsolete(isObsolete);

    // 7. altIds
    List<TermId> altIdList=new ArrayList<>();
    List<BasicPropertyValue> bpvs = meta.getBasicPropertyValues();
    if (bpvs!=null) {
      for (BasicPropertyValue bpv : bpvs) {
        if ("http://www.geneontology.org/formats/oboInOwl#hasAlternativeId".equals(bpv.getPred())) {
          String altId = bpv.getVal();
          altIdList.add(TermId.constructWithPrefix(altId));
        }
      }
      genericTerm.setAltTermIds(altIdList);
    } else {
      genericTerm.setAltTermIds(ImmutableList.of());
    }

    return genericTerm;
  }

  // It seems that only actual relation used across ontologies is "IS_A" one for now.
  @Override
  public Relationship constructRelationship(TermId source, TermId dest, int id,  RelationshipType reltype) {
    return new Relationship(source, dest, id, reltype);
  }
}
