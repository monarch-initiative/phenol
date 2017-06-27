package de.charite.compbio.ontolib.io.obo.go;

import com.google.common.collect.Lists;
import de.charite.compbio.ontolib.OntoLibRuntimeException;
import de.charite.compbio.ontolib.formats.go.GoRelationQualifier;
import de.charite.compbio.ontolib.formats.go.GoTerm;
import de.charite.compbio.ontolib.formats.go.GoTermRelation;
import de.charite.compbio.ontolib.io.obo.OboImmutableOntologyLoader;
import de.charite.compbio.ontolib.io.obo.OboOntologyEntryFactory;
import de.charite.compbio.ontolib.io.obo.Stanza;
import de.charite.compbio.ontolib.io.obo.StanzaEntry;
import de.charite.compbio.ontolib.io.obo.StanzaEntryAltId;
import de.charite.compbio.ontolib.io.obo.StanzaEntryComment;
import de.charite.compbio.ontolib.io.obo.StanzaEntryCreatedBy;
import de.charite.compbio.ontolib.io.obo.StanzaEntryCreationDate;
import de.charite.compbio.ontolib.io.obo.StanzaEntryDef;
import de.charite.compbio.ontolib.io.obo.StanzaEntryDisjointFrom;
import de.charite.compbio.ontolib.io.obo.StanzaEntryId;
import de.charite.compbio.ontolib.io.obo.StanzaEntryIntersectionOf;
import de.charite.compbio.ontolib.io.obo.StanzaEntryIsA;
import de.charite.compbio.ontolib.io.obo.StanzaEntryIsObsolete;
import de.charite.compbio.ontolib.io.obo.StanzaEntryName;
import de.charite.compbio.ontolib.io.obo.StanzaEntryRelationship;
import de.charite.compbio.ontolib.io.obo.StanzaEntrySubset;
import de.charite.compbio.ontolib.io.obo.StanzaEntrySynonym;
import de.charite.compbio.ontolib.io.obo.StanzaEntryType;
import de.charite.compbio.ontolib.io.obo.StanzaEntryUnionOf;
import de.charite.compbio.ontolib.ontology.data.ImmutableTermId;
import de.charite.compbio.ontolib.ontology.data.ImmutableTermSynonym;
import de.charite.compbio.ontolib.ontology.data.ImmutableTermXref;
import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.data.TermSynonym;
import de.charite.compbio.ontolib.ontology.data.TermSynonymScope;
import de.charite.compbio.ontolib.ontology.data.TermXref;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Collectors;

// TODO: flesh out, then consolidate with HpoOboFactory and similar classes

/**
 * Factory class for constructing {@link GoTerm} and {@link GoTermRelation} objects from
 * {@link Stanza} objects for usage in {@link OboOntologyEntryFactory}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
class GoOboFactory implements OboOntologyEntryFactory<GoTerm, GoTermRelation> {

  /**
   * Mapping from string representation of term Id to {@link TermId}.
   *
   * <p>
   * All occuring termIds must be previously registered into this map before calling any of this
   * object's functions. This happens in {@link OboImmutableOntologyLoader}.
   * </p>
   */
  private SortedMap<String, ImmutableTermId> termIds = null;

  /** Id of next relation. */
  private int nextRelationId = 1;

  @Override
  public void setTermIds(SortedMap<String, ImmutableTermId> termIds) {
    this.termIds = termIds;
  }

  @Override
  public GoTerm constructTerm(Stanza stanza) {
    final TermId id =
        termIds.get(this.<StanzaEntryId>getCardinalityOneEntry(stanza, StanzaEntryType.ID).getId());

    final String name =
        this.<StanzaEntryName>getCardinalityOneEntry(stanza, StanzaEntryType.NAME).getName();

    final List<TermId> altTermIds;
    final List<StanzaEntry> altEntryList = stanza.getEntryByType().get(StanzaEntryType.ALT_ID);
    if (altEntryList == null) {
      altTermIds = Lists.newArrayList();
    } else {
      altTermIds = altEntryList.stream().map(e -> termIds.get(((StanzaEntryAltId) e).getAltId()))
          .collect(Collectors.toList());
    }

    final StanzaEntryDef defEntry =
        this.<StanzaEntryDef>getCardinalityZeroOrOneEntry(stanza, StanzaEntryType.DEF);
    final String definition = (defEntry == null) ? null : defEntry.getText();

    final StanzaEntryComment commentEntry =
        this.<StanzaEntryComment>getCardinalityZeroOrOneEntry(stanza, StanzaEntryType.COMMENT);
    final String comment = (commentEntry == null) ? null : commentEntry.getText();

    final List<String> subsets;
    final List<StanzaEntry> subsetEntryList = stanza.getEntryByType().get(StanzaEntryType.SUBSET);
    if (subsetEntryList == null) {
      subsets = Lists.newArrayList();
    } else {
      subsets = subsetEntryList.stream().map(e -> ((StanzaEntrySubset) e).getName())
          .collect(Collectors.toList());
    }

    final List<TermSynonym> synonyms;
    final List<StanzaEntry> synonymEntryList = stanza.getEntryByType().get(StanzaEntryType.SYNONYM);
    if (synonymEntryList == null) {
      synonyms = Lists.newArrayList();
    } else {
      synonyms = synonymEntryList.stream().map(e -> {
        final StanzaEntrySynonym s = (StanzaEntrySynonym) e;

        final String value = s.getText();
        final TermSynonymScope scope = s.getTermSynonymScope();
        final String synonymTypeName = s.getSynonymTypeName();
        final List<TermXref> termXrefs = s.getDbXrefList().getDbXrefs().stream()
            .map(xref -> new ImmutableTermXref(termIds.get(xref.getName()), xref.getDescription()))
            .collect(Collectors.toList());

        return new ImmutableTermSynonym(value, scope, synonymTypeName, termXrefs);
      }).collect(Collectors.toList());
    }

    final StanzaEntryIsObsolete isObsoleteEntry = this.<
        StanzaEntryIsObsolete>getCardinalityZeroOrOneEntry(stanza, StanzaEntryType.IS_OBSOLETE);
    final boolean obsolete = (isObsoleteEntry == null) ? false : isObsoleteEntry.getValue();

    final StanzaEntryCreatedBy createdByEntry =
        this.<StanzaEntryCreatedBy>getCardinalityZeroOrOneEntry(stanza, StanzaEntryType.CREATED_BY);
    final String createdBy = (createdByEntry == null) ? null : createdByEntry.getCreator();

    final StanzaEntryCreationDate creationDateEntry = this.<
        StanzaEntryCreationDate>getCardinalityZeroOrOneEntry(stanza, StanzaEntryType.CREATION_DATE);
    final String creationDate = (creationDateEntry == null) ? null : creationDateEntry.getValue();

    return new GoTerm(id, altTermIds, name, definition, comment, subsets, synonyms, obsolete,
        createdBy, creationDate);
  }

  /**
   * Extract cardinality one entry (=tag) of type <code>type</code> from <code>stanza</code>.
   *
   * @param stanza {@link Stanza} to get {@link StanzaEntry} from.
   * @param type {@link StanzaEntryType} to use.
   * @return Resulting {@link StanzaEntry}, properly cast.
   */
  @SuppressWarnings("unchecked")
  protected <E extends StanzaEntry> E getCardinalityOneEntry(Stanza stanza, StanzaEntryType type) {
    final List<StanzaEntry> typeEntries = stanza.getEntryByType().get(type);
    if (typeEntries == null) {
      throw new OntoLibRuntimeException(
          type + " tag must have cardinality 1 but was null (" + stanza + ")");
    } else if (typeEntries.size() != 1) {
      throw new OntoLibRuntimeException(type + " tag must have cardinality 1 but was "
          + typeEntries.size() + " (" + stanza + ")");
    }
    return (E) typeEntries.get(0);
  }

  /**
   * Extract cardinality zero or one entry (=tag) of type <code>type</code> from
   * <code>stanza</code>.
   *
   * @param stanza {@link Stanza} to get {@link StanzaEntry} from.
   * @param type {@link StanzaEntryType} to use.
   * @return Resulting {@link StanzaEntry}, properly cast, or <code>null</code>.
   */
  @SuppressWarnings("unchecked")
  protected <E extends StanzaEntry> E getCardinalityZeroOrOneEntry(Stanza stanza,
      StanzaEntryType type) {
    final List<StanzaEntry> typeEntries = stanza.getEntryByType().get(type);
    if (typeEntries == null) {
      return null;
    } else if (typeEntries.size() != 1) {
      throw new RuntimeException(type + " tag must have cardinality <= 1 but was "
          + typeEntries.size() + " (" + stanza + ")");
    } else {
      return (E) typeEntries.get(0);
    }
  }

  @Override
  public GoTermRelation constructTermRelation(Stanza stanza, StanzaEntryIsA stanzaEntry) {
    final TermId sourceId =
        termIds.get(this.<StanzaEntryId>getCardinalityOneEntry(stanza, StanzaEntryType.ID).getId());
    final TermId destId = termIds.get(stanzaEntry.getId());
    return new GoTermRelation(sourceId, destId, nextRelationId++, GoRelationQualifier.IS_A);
  }

  @Override
  public GoTermRelation constructTermRelation(Stanza stanza, StanzaEntryDisjointFrom stanzaEntry) {
    throw new UnsupportedOperationException();
  }

  @Override
  public GoTermRelation constructTermRelation(Stanza stanza, StanzaEntryUnionOf stanzaEntry) {
    throw new UnsupportedOperationException();
  }

  @Override
  public GoTermRelation constructTermRelation(Stanza stanza,
      StanzaEntryIntersectionOf stanzaEntry) {
    throw new UnsupportedOperationException();
  }

  @Override
  public GoTermRelation constructTermRelation(Stanza stanza, StanzaEntryRelationship stanzaEntry) {
    throw new UnsupportedOperationException();
  }

}
