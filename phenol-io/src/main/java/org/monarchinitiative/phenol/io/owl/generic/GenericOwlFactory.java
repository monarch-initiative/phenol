package org.monarchinitiative.phenol.io.owl.generic;

import java.lang.reflect.Field;
import java.util.List;

import org.geneontology.obographs.model.Meta;
import org.geneontology.obographs.model.Node;
import org.geneontology.obographs.model.meta.DefinitionPropertyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.monarchinitiative.phenol.formats.generic.GenericRelationshipType;
import org.monarchinitiative.phenol.formats.generic.GenericTerm;
import org.monarchinitiative.phenol.formats.generic.GenericRelationship;
import org.monarchinitiative.phenol.io.owl.OwlOntologyEntryFactory;
import org.monarchinitiative.phenol.io.owl.SynonymMapper;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermSynonym;

/**
 * Factory class for constructing {@link GenericTerm} and {@link GenericRelationship} objects from
 * Obographs's Nodes.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class GenericOwlFactory
    implements OwlOntologyEntryFactory<GenericTerm, GenericRelationship> {
  private static final Logger LOGGER = LoggerFactory.getLogger(GenericOwlFactory.class);

  @Override
  public GenericTerm constructTerm(Node node, TermId termId) {
    GenericTerm commonTerm = new GenericTerm();
    commonTerm.setId(termId);
    commonTerm.setName(node.getLabel());

    Meta meta = node.getMeta();
    if (meta == null) {
      LOGGER.warn("No meta instance exists for the node: " + node.getId());
      return commonTerm;
    }

    DefinitionPropertyValue definition = meta.getDefinition();
    if (definition != null) commonTerm.setDefinition(definition.getVal());

    List<String> comments = meta.getComments();
    if (comments != null) commonTerm.setComment(String.join(", ", comments));

    commonTerm.setSubsets(meta.getSubsets());

    List<TermSynonym> termSynonyms = SynonymMapper.mapSynonyms(meta.getSynonyms());
    commonTerm.setSynonyms(termSynonyms);

    // The obsolete/deprecated field in Meta is somehow not accessible, so we use Java reflection to pull the value of that field.
    Boolean isObsolete = false;
    try {
      Field f = Meta.class.getDeclaredField("deprecated");
      f.setAccessible(true);
      if (meta != null) {
        Boolean deprecated = (Boolean) f.get(meta);
        if (deprecated == null || deprecated != true) isObsolete = false;
        else if (deprecated) isObsolete = true;
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
    }
    commonTerm.setObsolete(isObsolete);

    // Additional properties/annotations can be further mapped by iterating BasicPropertyValue.
    /*
    List<BasicPropertyValue> bpvs = meta.getBasicPropertyValues();
    if (bpvs != null) {
    	for (BasicPropertyValue bpv: bpvs) {
    		System.out.println("Pred: " + bpv.getPred());
    		System.out.println("Val: " + bpv.getVal());
    	}
    }
     */
    return commonTerm;
  }

  // It seems that only actual relation used across ontologies is "IS_A" one for now.
  @Override
  public GenericRelationship constructRelationship(TermId source, TermId dest, int id) {
    return new GenericRelationship(source, dest, id, GenericRelationshipType.IS_A);
  }
}
