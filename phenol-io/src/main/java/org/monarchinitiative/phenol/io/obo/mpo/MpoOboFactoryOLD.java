package org.monarchinitiative.phenol.io.obo.mpo;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.formats.mpo.MpoRelationshipType;
import org.monarchinitiative.phenol.formats.mpo.MpoTerm;
import org.monarchinitiative.phenol.formats.mpo.MpoRelationship;
import org.monarchinitiative.phenol.io.obo.OboImmutableOntologyLoader;
import org.monarchinitiative.phenol.io.obo.OboOntologyEntryFactory;
import org.monarchinitiative.phenol.io.obo.Stanza;
import org.monarchinitiative.phenol.io.obo.StanzaEntry;
import org.monarchinitiative.phenol.io.obo.StanzaEntryAltId;
import org.monarchinitiative.phenol.io.obo.StanzaEntryComment;
import org.monarchinitiative.phenol.io.obo.StanzaEntryCreatedBy;
import org.monarchinitiative.phenol.io.obo.StanzaEntryCreationDate;
import org.monarchinitiative.phenol.io.obo.StanzaEntryDef;
import org.monarchinitiative.phenol.io.obo.StanzaEntryDisjointFrom;
import org.monarchinitiative.phenol.io.obo.StanzaEntryId;
import org.monarchinitiative.phenol.io.obo.StanzaEntryIntersectionOf;
import org.monarchinitiative.phenol.io.obo.StanzaEntryIsA;
import org.monarchinitiative.phenol.io.obo.StanzaEntryIsObsolete;
import org.monarchinitiative.phenol.io.obo.StanzaEntryName;
import org.monarchinitiative.phenol.io.obo.StanzaEntryRelationship;
import org.monarchinitiative.phenol.io.obo.StanzaEntrySubset;
import org.monarchinitiative.phenol.io.obo.StanzaEntrySynonym;
import org.monarchinitiative.phenol.io.obo.StanzaEntryType;
import org.monarchinitiative.phenol.io.obo.StanzaEntryUnionOf;
import org.monarchinitiative.phenol.io.obo.StanzaEntryXref;
import org.monarchinitiative.phenol.ontology.data.Dbxref;
import org.monarchinitiative.phenol.ontology.data.ImmutableTermId;
import org.monarchinitiative.phenol.ontology.data.ImmutableTermSynonym;
import org.monarchinitiative.phenol.ontology.data.ImmutableTermXref;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermSynonym;
import org.monarchinitiative.phenol.ontology.data.TermSynonymScope;
import org.monarchinitiative.phenol.ontology.data.TermXref;
import com.google.common.collect.Lists;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Collectors;

/**
 * Factory class for constructing {@link MpoTerm} and {@link MpoRelationship} objects from {@link
 * Stanza} objects for usage in {@link OboOntologyEntryFactory}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
class MpoOboFactoryOLD implements OboOntologyEntryFactory<MpoTerm, MpoRelationship> {

  /**
   * Mapping from string representation of term Id to {@link TermId}.
   *
   * <p>All occuring termIds must be previously registered into this map before calling any of this
   * object's functions. This happens in {@link OboImmutableOntologyLoader}.
   */
  private SortedMap<String, ImmutableTermId> termIds = null;

  /** Id of next relation. */
  private int nextRelationId = 1;

  @Override
  public void setTermIds(SortedMap<String, ImmutableTermId> termIds) {
    this.termIds = termIds;
  }

  @Override
  public MpoTerm constructTerm(Stanza stanza) {
    final TermId id =
        termIds.get(this.<StanzaEntryId>getCardinalityOneEntry(stanza, StanzaEntryType.ID).getId());

    final String name =
        this.<StanzaEntryName>getCardinalityOneEntry(stanza, StanzaEntryType.NAME).getName();

    final List<TermId> altTermIds;
    final List<StanzaEntry> altEntryList = stanza.getEntryByType().get(StanzaEntryType.ALT_ID);
    if (altEntryList == null) {
      altTermIds = Lists.newArrayList();
    } else {
      altTermIds =
          altEntryList
              .stream()
              .map(e -> termIds.get(((StanzaEntryAltId) e).getAltId()))
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
      subsets =
          subsetEntryList
              .stream()
              .map(e -> ((StanzaEntrySubset) e).getName())
              .collect(Collectors.toList());
    }

    final List<TermSynonym> synonyms;
    final List<StanzaEntry> synonymEntryList = stanza.getEntryByType().get(StanzaEntryType.SYNONYM);
    if (synonymEntryList == null) {
      synonyms = Lists.newArrayList();
    } else {
      synonyms =
          synonymEntryList
              .stream()
              .map(
                  e -> {
                    final StanzaEntrySynonym s = (StanzaEntrySynonym) e;

                    final String value = s.getText();
                    final TermSynonymScope scope = s.getTermSynonymScope();
                    final String synonymTypeName = s.getSynonymTypeName();
                    final List<TermXref> termXrefs =
                        s.getDbXrefList()
                            .getDbXrefs()
                            .stream()
                            .map(
                                xref ->
                                    new ImmutableTermXref(
                                        termIds.get(xref.getName()), xref.getDescription()))
                            .collect(Collectors.toList());

                    return new ImmutableTermSynonym(value, scope, synonymTypeName, termXrefs);
                  })
              .collect(Collectors.toList());
    }

    final StanzaEntryIsObsolete isObsoleteEntry =
        this.<StanzaEntryIsObsolete>getCardinalityZeroOrOneEntry(
            stanza, StanzaEntryType.IS_OBSOLETE);
    final boolean obsolete = (isObsoleteEntry == null) ? false : isObsoleteEntry.getValue();

    final StanzaEntryCreatedBy createdByEntry =
        this.<StanzaEntryCreatedBy>getCardinalityZeroOrOneEntry(stanza, StanzaEntryType.CREATED_BY);
    final String createdBy = (createdByEntry == null) ? null : createdByEntry.getCreator();

    final StanzaEntryCreationDate creationDateEntry =
        this.<StanzaEntryCreationDate>getCardinalityZeroOrOneEntry(
            stanza, StanzaEntryType.CREATION_DATE);
    final String creationDateStr =
        (creationDateEntry == null) ? null : creationDateEntry.getValue();

    final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    Date creationDate = null;
    if (creationDateStr != null) {
      try {
        creationDate = format.parse(creationDateStr);
      } catch (ParseException e) {
        throw new PhenolRuntimeException("Problem parsing date string " + creationDateStr, e);
      }
    }

    final List<StanzaEntry> entryList = stanza.getEntryByType().get(StanzaEntryType.XREF);
    final List<Dbxref> dbxrefList = new ArrayList<>();
    if (entryList != null) {
      final List<StanzaEntryXref> xrefs =
          entryList.stream().map(entry -> (StanzaEntryXref) entry).collect(Collectors.toList());
      for (StanzaEntryXref xref : xrefs) {
        final Dbxref dbXref = xref.getDbXref();
        dbxrefList.add(dbXref);
      }
    }

    return new MpoTerm(
        id,
        altTermIds,
        name,
        definition,
        comment,
        subsets,
        synonyms,
        obsolete,
        createdBy,
        creationDate,
        dbxrefList);
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
      throw new PhenolRuntimeException(
          type + " tag must have cardinality 1 but was null (" + stanza + ")");
    } else if (typeEntries.size() != 1) {
      throw new PhenolRuntimeException(
          type
              + " tag must have cardinality 1 but was "
              + typeEntries.size()
              + " ("
              + stanza
              + ")");
    }
    return (E) typeEntries.get(0);
  }

  /**
   * Extract cardinality zero or one entry (=tag) of type <code>type</code> from <code>stanza</code>
   * .
   *
   * @param stanza {@link Stanza} to get {@link StanzaEntry} from.
   * @param type {@link StanzaEntryType} to use.
   * @return Resulting {@link StanzaEntry}, properly cast, or <code>null</code>.
   */
  @SuppressWarnings("unchecked")
  protected <E extends StanzaEntry> E getCardinalityZeroOrOneEntry(
      Stanza stanza, StanzaEntryType type) {
    final List<StanzaEntry> typeEntries = stanza.getEntryByType().get(type);
    if (typeEntries == null) {
      return null;
    } else if (typeEntries.size() != 1) {
      throw new RuntimeException(
          type
              + " tag must have cardinality <= 1 but was "
              + typeEntries.size()
              + " ("
              + stanza
              + ")");
    } else {
      return (E) typeEntries.get(0);
    }
  }

  @Override
  public MpoRelationship constructrelationship(Stanza stanza, StanzaEntryIsA stanzaEntry) {
    final TermId sourceId =
        termIds.get(this.<StanzaEntryId>getCardinalityOneEntry(stanza, StanzaEntryType.ID).getId());
    final TermId destId = termIds.get(stanzaEntry.getId());
    return new MpoRelationship(sourceId, destId, nextRelationId++, MpoRelationshipType.IS_A);
  }

  @Override
  public MpoRelationship constructrelationship(Stanza stanza, StanzaEntryDisjointFrom stanzaEntry) {
    throw new UnsupportedOperationException();
  }

  @Override
  public MpoRelationship constructrelationship(Stanza stanza, StanzaEntryUnionOf stanzaEntry) {
    throw new UnsupportedOperationException();
  }

  @Override
  public MpoRelationship constructrelationship(
      Stanza stanza, StanzaEntryIntersectionOf stanzaEntry) {
    throw new UnsupportedOperationException();
  }

  @Override
  public MpoRelationship constructrelationship(Stanza stanza, StanzaEntryRelationship stanzaEntry) {
    throw new UnsupportedOperationException();
  }
}
