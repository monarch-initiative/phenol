package de.charite.compbio.ontolib.io.obo;

/**
 * Enumeration for describing {@link StanzaEntry} types.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public enum StanzaEntryType {
  /** Stanza entry starting with <code>alt_id</code>. */
  ALT_ID,
  /** Stanza entry starting with <code>auto-generated-by</code>. */
  AUTO_GENERATED_BY,
  /** Stanza entry starting with <code>comment</code>. */
  COMMENT,
  /** Stanza entry starting with <code>consider</code>. */
  CONSIdER,
  /** Stanza entry starting with <code>created-by</code>. */
  CREATED_BY,
  /** Stanza entry starting with <code>creation_date</code>. */
  CREATION_DATE,
  /** Stanza entry starting with <code>data-version</code>. */
  DATA_VERSION,
  /** Stanza entry starting with <code>date</code>. */
  DATE,
  /** Stanza entry starting with <code>def</code>. */
  DEF,
  /** Stanza entry starting with <code>default-relationship-id-prefix</code>. */
  DEFAULT_RELATIONSHIP_Id_PREFIX,
  /** Stanza entry starting with <code>disjoint_from</code>. */
  DISJOINT_FROM,
  /** Stanza entry starting with <code>domain</code>. */
  DOMAIN,
  /** Stanza entry starting with <code>format-version</code>. */
  FORMAT_VERSION,
  /** Stanza entry starting with other strings. */
  GENERIC,
  /** Stanza entry starting with <code>id</code>. */
  Id,
  /** Stanza entry starting with <code>id-mapping</code>. */
  Id_MAPPING,
  /** Stanza entry starting with <code>idspace</code>. */
  IdSPACE,
  /** Stanza entry starting with <code>import</code>. */
  IMPORT,
  /** Stanza entry starting with <code>instance_of</code>. */
  INSTANCE_OF,
  /** Stanza entry starting with <code>intersection_of</code>. */
  INTERSECTION_OF,
  /** Stanza entry starting with <code>inverse_of</code>. */
  INVERSE_OF,
  /** Stanza entry starting with <code>is_a</code>. */
  IS_A,
  /** Stanza entry starting with <code>is_anonymous</code>. */
  IS_ANONYMOUS,
  /** Stanza entry starting with <code>is_antisymmetric</code>. */
  IS_ANTISYMMETRIC,
  /** Stanza entry starting with <code>is_cyclic</code>. */
  IS_CYCLIC,
  /** Stanza entry starting with <code>is_metadata</code>. */
  IS_METADATA,
  /** Stanza entry starting with <code>is_obsolete</code>. */
  IS_OBSOLETE,
  /** Stanza entry starting with <code>is_reflexive</code>. */
  IS_REFLEXIVE,
  /** Stanza entry starting with <code>is_symmetric</code>. */
  IS_SYMMETRIC,
  /** Stanza entry starting with <code>is_transitive</code>. */
  IS_TRANSITIVE,
  /** Stanze entry starting with <code>name</code>. */
  NAME,
  /** Stanza entry starting with <code>range</code>. */
  RANGE,
  /** Stanza entry starting with <code>relationship</code>. */
  RELATIONSHIP,
  /** Stanza entry starting with <code>remark</code>. */
  REMARK,
  /** Stanza entry starting with <code>replaced_by</code>. */
  REPLACED_BY,
  /** Stanza entry starting with <code>saved_by</code>. */
  SAVED_BY,
  /** Stanza entry starting with <code>subset</code>. */
  SUBSET,
  /** Stanza entry starting with <code>subsetdef</code>. */
  SUBSETDEF,
  /** Stanza entry starting with <code>synonym</code>. */
  SYNONYM,
  /** Stanza entry starting with <code>synonymtypedef</code>. */
  SYNONYMTYPEDEF,
  /** Stanza entry starting with <code>transitive_over</code>. */
  TRANSITIVE_OVER,
  /** Stanza entry starting with <code>union_of</code>. */
  UNION_OF,
  /** Stanza entry starting with <code>xref</code>. */
  XREF;
}
