package org.monarchinitiative.phenol.io.obo;

import com.google.common.base.Joiner;

/**
 * Smoke test base calss with example OBO files.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class SmokeTestBase {
  public static final String MINIMAL_FILE = "format-version: 1.2\n\n[TermI]\nid: HP:0000001\n";

  public static final String HEAD_HPO =
      Joiner.on("\n")
              .join(
                  new String[] {
                    "format-version: 1.2",
                    "data-version: releases/2017-04-13",
                    "saved-by: Peter Robinson, Sebastian Koehler, Sandra Doelken, Chris Mungall, "
                        + "Melissa Haendel, Nicole Vasilevsky, Monarch Initiative, et al.",
                    "subsetdef: hposlim_core \"Core clinical terminology\"",
                    "subsetdef: secondary_consequence \"Consequence of a disorder in another organ system.\"",
                    "synonymtypedef: HP:0045076 \"UK spelling\"",
                    "synonymtypedef: HP:0045077 \"abbreviation\"",
                    "synonymtypedef: HP:0045078 \"plural form\"",
                    "synonymtypedef: layperson \"layperson term\"",
                    "default-namespace: human_phenotype",
                    "ontology: hp",
                    "property_value: http://purl.org/dc/elements/1.1/contributor \"Chris Mungall\" xsd:string",
                    "property_value: http://purl.org/dc/elements/1.1/contributor \"Courtney Hum\" xsd:string",
                    "property_value: http://purl.org/dc/elements/1.1/contributor \"Joie Davis\" xsd:string",
                    "property_value: http://purl.org/dc/elements/1.1/contributor \"Mark Engelstad\" xsd:string",
                    "property_value: http://purl.org/dc/elements/1.1/contributor \"Melissa Haendel\" xsd:string",
                    "property_value: http://purl.org/dc/elements/1.1/contributor \"Nicole Vasilevsky\" "
                        + "xsd:string",
                    "property_value: http://purl.org/dc/elements/1.1/contributor \"Sandra Doelken\" xsd:string",
                    "property_value: http://purl.org/dc/elements/1.1/creator \"Peter N Robinson\" xsd:string",
                    "property_value: http://purl.org/dc/elements/1.1/creator \"Sebastian Koehler\" xsd:string",
                    "property_value: http://purl.org/dc/elements/1.1/creator \"The Human Phenotype Ontology "
                        + "Consortium\" xsd:string",
                    "property_value: http://purl.org/dc/elements/1.1/creator \"The Monarch Initiative\" "
                        + "xsd:string",
                    "property_value: http://purl.org/dc/elements/1.1/license \"see "
                        + "http://www.human-phenotype-ontology.org\" xsd:string",
                    "property_value: http://purl.org/dc/elements/1.1/rights \"Peter Robinson, "
                        + "Sebastian Koehler, The Human Phenotype Ontology Consortium, and The Monarch "
                        + "Initiative\" xsd:string",
                    "property_value: http://purl.org/dc/elements/1.1/subject \"Phenotypic abnormalities "
                        + "encountered in human disease\" xsd:string",
                    "owl-axioms: Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\\n"
                        + "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\\nPrefix("
                        + "xml:=<http://www.w3.org/XML/1998/namespace>)\\nPrefix("
                        + "xsd:=<http://www.w3.org/2001/XMLSchema#>)\\nPrefix("
                        + "rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\\n\\n\\nOntology("
                        + "\\nAnnotationAssertion(<http://www.geneontology.org/formats/oboInOwl"
                        + "#hasRelatedSynonym> <http://purl.obolibrary.org/obo/HP_0000510> "
                        + "\\\"\\\")\\nAnnotationAssertion(<http://www.geneontology.org/formats/"
                        + "oboInOwl#hasAlternativeId> <http://purl.obolibrary.org/obo/HP_0030761> "
                        + "\\\"\\\")\\nAnnotationAssertion(<http://www.geneontology.org/formats/obo"
                        + "InOwl#hasOBONamespace> <http://purl.obolibrary.org/obo/HP_0030243> \\\"\\\")"
                        + "\\nAnnotationAssertion(<http://www.geneontology.org/formats/oboInOwl#has"
                        + "OBONamespace> <http://purl.obolibrary.org/obo/HP_0030370> \\\"\\\")\\n"
                        + "AnnotationAssertion(rdfs:comment <http://purl.obolibrary.org/obo/HP_0040081> "
                        + "\\\"\\\")\\nAnnotationAssertion(<http://www.geneontology.org/formats/oboInOwl"
                        + "#hasRelatedSynonym> <http://purl.obolibrary.org/obo/HP_0012377> \\\"\\\"^^"
                        + "xsd:string)\\n)",
                    "logical-definition-view-relation: has_part",
                    "",
                    "[TermI]",
                    "id: HP:0000001",
                    "name: All",
                    "comment: Root of all terms in the Human Phenotype Ontology.",
                    "xref: UMLS:C0444868",
                    "",
                    "[TermI]",
                    "id: HP:0000002",
                    "name: Abnormality of body height",
                    "def: \"Deviation from the norm of height with respect to that which is expected "
                        + "according to age and gender norms.\" [HPO:probinson]",
                    "synonym: \"Abnormality of body height\" EXACT layperson []",
                    "xref: UMLS:C4025901",
                    "is_a: HP:0001507 ! Growth abnormality",
                    "created_by: peter",
                    "creation_date: 2008-02-27T02:20:00Z"
                  })
          + "\n";
}
