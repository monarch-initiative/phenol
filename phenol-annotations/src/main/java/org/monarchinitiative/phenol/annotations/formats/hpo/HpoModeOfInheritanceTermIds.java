package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * Utility class with constants from "Mode of Inheritance" sub ontology.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @deprecated the class will be removed in <code>v3.0.0</code>, use {@link org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds} instead.
 */
@Deprecated(forRemoval = true, since = "2.0.0-RC2")
public final class HpoModeOfInheritanceTermIds {

  public static final TermId INHERITANCE_ROOT = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.INHERITANCE_ROOT;

  /** {@link TermId} for term "heterogeneous"/"genetic heterogeneity". */
  public static final TermId HETEROGENEOUS = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.HETEROGENEOUS;

  /** {@link TermId} for term "multifactorial inheritance". */
  public static final TermId MULTIFACTORIAL = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.MULTIFACTORIAL;

  /** {@link TermId} for term "polygenic inheritance". */
  public static final TermId POLYGENIC = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.POLYGENIC;

  /** {@link TermId} for term "oligogenic inheritance". */
  public static final TermId OLIGOGENIC = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.OLIGOGENIC;

  /** {@link TermId} for term "digenic inheritance". */
  public static final TermId DIGENIC = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.DIGENIC;

  /** {@link TermId} for term "mitochondrial inheritance". */
  public static final TermId MITOCHONDRIAL = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.MITOCHONDRIAL;

  /** {@link TermId} for "somatic mutation". */
  public static final TermId SOMATIC_MUTATION = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.SOMATIC_MUTATION;

  /** {@link TermId} for "somatic mosaicism". */
  public static final TermId SOMATIC_MOSAICISM = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.SOMATIC_MOSAICISM;

  /** {@link TermId} for "contiguous gene syndrom". */
  public static final TermId CONTIGUOUS_GENE_SYNDROME = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.CONTIGUOUS_GENE_SYNDROME;

  /**
   * {@link TermId} for "autosomal dominant contiguous gene syndrom.
   *
   * @see #AUTOSOMAL_DOMINANT_CONTIGUOUS_GENE_SYNDROME
   */
  public static final TermId CONTIGUOUS_GENE_SYNDROME_AUTOSOMAL_DOMINANT = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.CONTIGUOUS_GENE_SYNDROME_AUTOSOMAL_DOMINANT;

  /** {@link TermId} for "familial predisposition". */
  public static final TermId FAMILIAL_PREDISPOSITION = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.FAMILIAL_PREDISPOSITION;

  /** {@link TermId} for "genetic anticipation". */
  public static final TermId GENETIC_ANTICIPATION = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.GENETIC_ANTICIPATION;

  /** {@link TermId} for "genetic anticipation with paternal bias". */
  public static final TermId GENETIC_ANTICIPATION_PATERNAL_BIAS = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.GENETIC_ANTICIPATION_PATERNAL_BIAS;

  /** {@link TermId} for "sporadic"/"isolated cases". */
  public static final TermId SPORADIC = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.SPORADIC;

  /** {@link TermId} for "gonosomal inheritance". */
  public static final TermId GONOSOMAL = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.GONOSOMAL;

  /** {@link TermId} for "X-linked inheritance". */
  public static final TermId X_LINKED = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.X_LINKED;

  /** {@link TermId} for "X-linked dominant inheritance. */
  public static final TermId X_LINKED_DOMINANT = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.X_LINKED_DOMINANT;

  /** {@link TermId} for "X-linked recessive inheritance. */
  public static final TermId X_LINKED_RECESSIVE = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.X_LINKED_RECESSIVE;

  /** {@link TermId} for "Y-linked inheritance. */
  public static final TermId Y_LINKED = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.Y_LINKED;
  /** {@link TermId} for "autosomal recessive inheritance. */
  public static final TermId AUTOSOMAL_RECESSIVE = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.AUTOSOMAL_RECESSIVE;
  /** {@link TermId} for "autosomal dominant inheritance. */
  public static final TermId AUTOSOMAL_DOMINANT = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.AUTOSOMAL_DOMINANT;

  /** {@link TermId} for "autosomal dominant inheritance with paternal imprinting". */
  public static final TermId AUTOSOMAL_DOMINANT_PATERNAL_IMPRINTING = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.AUTOSOMAL_DOMINANT_PATERNAL_IMPRINTING;

  /** {@link TermId} for "autosomal dominant inheritance with maternal imprinting". */
  public static final TermId AUTOSOMAL_DOMINANT_MATERNAL_IMPRINTING = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.AUTOSOMAL_DOMINANT_MATERNAL_IMPRINTING;

  public static final TermId AUTOSOMAL_DOMINANT_GERMLINE_DENOVO = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.AUTOSOMAL_DOMINANT_GERMLINE_DENOVO;

  /**
   * Autosomal dominant contiguous gene syndrom (alias to {@link #CONTIGUOUS_GENE_SYNDROME}).
   *
   * @see #CONTIGUOUS_GENE_SYNDROME_AUTOSOMAL_DOMINANT
   */
  public static final TermId AUTOSOMAL_DOMINANT_CONTIGUOUS_GENE_SYNDROME = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.AUTOSOMAL_DOMINANT_CONTIGUOUS_GENE_SYNDROME;

  /** {@link TermId} for "sexSpecific-limited autosomal dominant". */
  public static final TermId AUTOSOMAL_DOMINANT_SEX_LIMITED = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.AUTOSOMAL_DOMINANT_SEX_LIMITED;

  /** {@link TermId} for "male-limited autosomal dominant". */
  public static final TermId AUTOSOMAL_DOMINANT_MALE_LIMITED = org.monarchinitiative.phenol.annotations.constants.hpo.HpoModeOfInheritanceTermIds.AUTOSOMAL_DOMINANT_MALE_LIMITED;

  private HpoModeOfInheritanceTermIds() {}
}
