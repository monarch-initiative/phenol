package com.github.phenomics.ontolib.io.owl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.github.phenomics.ontolib.formats.common.CommonTerm;
import com.github.phenomics.ontolib.formats.common.CommonTermRelation;
import com.github.phenomics.ontolib.io.owl.common.CommonOwlFactory;
import com.github.phenomics.ontolib.ontology.data.ImmutableOntology;

/**
 * A testcase that tests the codes of loading a dummy ontology bulit from ncit.owl.
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class OwlImmutableOntologyLoaderTest {
	@Test
	public void testLoader() {
		// ncit_module.owl contains 6 classes where 2 classes are dummy ones. 
		OwlImmutableOntologyLoader<CommonTerm, CommonTermRelation> loader = 
				new OwlImmutableOntologyLoader<CommonTerm, CommonTermRelation>(new File("src/test/resources/ncit_module.owl"));

		try {
			CommonOwlFactory cof = new CommonOwlFactory();
			ImmutableOntology<CommonTerm, CommonTermRelation> ontology = loader.load(cof);
			
			assertEquals(
					"ImmutableDirectedGraph [edgeLists={ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2852]=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2919], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2852], id=1]], outEdges=[]], ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2919]=ImmutableVertexEdgeList [inEdges=[], outEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2919], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C48596], id=2], ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2919], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2852], id=1]]], ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C48596]=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2919], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C48596], id=2]], outEdges=[]], ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C60312]=ImmutableVertexEdgeList [inEdges=[], outEdges=[]]}, edgeCount=2]",
					ontology.getGraph().toString());

			assertEquals(
					"[ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C60312], ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C48596], ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2919], ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2852]]",
					ontology.getAllTermIds().toString());

			assertEquals(
					"{1=CommonTermRelation [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2919], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2852], id=1, relationQualifier=IS_A], 2=CommonTermRelation [source=ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C2919], dest=ImmutableTermId [prefix=ImmutableTermPrefix [value=NCIT], id=C48596], id=2, relationQualifier=IS_A]}",
					ontology.getRelationMap().toString());
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}