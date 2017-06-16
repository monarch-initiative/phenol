package de.charite.compbio.ontolib.ontology.data;

/**
 * Immutable implementation of {@link TermXRef}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ImmutableTermXRef implements TermXRef {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /** Referenced term ID. */
  private final TermID id;

  /** Referenced description. */
  private final String description;

  /**
   * Constructor.
   *
   * @param id The term's ID.
   * @param description The cross reference description.
   */
  public ImmutableTermXRef(TermID id, String description) {
    this.id = id;
    this.description = description;
  }

  @Override
  public TermID getID() {
    return id;
  }

  @Override
  public String getDescription() {
    return description;
  }

}
