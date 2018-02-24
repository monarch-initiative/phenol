package com.github.phenomics.ontolib.io.owl.common;

import java.lang.reflect.Field;
import java.util.List;

import org.geneontology.obographs.model.Meta;
import org.geneontology.obographs.model.Node;
import org.geneontology.obographs.model.meta.DefinitionPropertyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.phenomics.ontolib.formats.common.CommonRelationQualifier;
import com.github.phenomics.ontolib.formats.common.CommonTerm;
import com.github.phenomics.ontolib.formats.common.CommonTermRelation;
import com.github.phenomics.ontolib.formats.go.GoTerm;
import com.github.phenomics.ontolib.formats.go.GoTermRelation;
import com.github.phenomics.ontolib.io.obo.OboOntologyEntryFactory;
import com.github.phenomics.ontolib.io.obo.Stanza;
import com.github.phenomics.ontolib.io.owl.OwlOntologyEntryFactory;
import com.github.phenomics.ontolib.io.owl.SynonymMapper;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.github.phenomics.ontolib.ontology.data.TermSynonym;

/**
 * Factory class for constructing {@link CommonTerm} and {@link CommonTermRelation} objects from Obographs's Nodes.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class CommonOwlFactory implements OwlOntologyEntryFactory<CommonTerm, CommonTermRelation>{
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonOwlFactory.class);

	@Override
	public CommonTerm constructTerm(Node node, TermId termId) {
		CommonTerm commonTerm = new CommonTerm();
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
				if (deprecated ==null || deprecated != true) 
					isObsolete = false;
				else if (deprecated)
					isObsolete = true;
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
	public CommonTermRelation constructTermRelation(TermId source, TermId dest, int id) {
		return new CommonTermRelation(source, dest, id, CommonRelationQualifier.IS_A);
	}
}