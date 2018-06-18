package org.monarchinitiative.phenol.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * Utility class with constants from "Mode of Inheritance" sub ontology.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a> * @author <a
 *     href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public final class HpoModeOfInheritanceTermIds {

  public static final TermId INHERITANCE_ROOT = TermId.constructWithPrefix("HP:0000005");

  /** {@link TermId} for term "heterogeneous"/"genetic heterogeneity". */
  public static final TermId HETEROGENEOUS = TermId.constructWithPrefix("HP:0001425");

  /** {@link TermId} for term "multifactorial inheritance". */
  public static final TermId MULTIFACTORIAL = TermId.constructWithPrefix("HP:0001426");

  /** {@link TermId} for term "polygenic inheritance". */
  public static final TermId POLYGENIC = TermId.constructWithPrefix("HP:0010982");

  /** {@link TermId} for term "oligogenic inheritance". */
  public static final TermId OLIGOGENIC = TermId.constructWithPrefix("HP:0010983");

  /** {@link TermId} for term "digenic inheritance". */
  public static final TermId DIGENIC = TermId.constructWithPrefix("HP:0010984");

  /** {@link TermId} for term "mitochondrial inheritance". */
  public static final TermId MITOCHONDRIAL = TermId.constructWithPrefix("HP:0001427");

  /** {@link TermId} for "somatic mutation". */
  public static final TermId SOMATIC_MUTATION = TermId.constructWithPrefix("HP:0001428");

  /** {@link TermId} for "somatic mosaicism". */
  public static final TermId SOMATIC_MOSAICISM = TermId.constructWithPrefix("HP:0001442");

  /** {@link TermId} for "contiguous gene syndrom". */
  public static final TermId CONTIGUOUS_GENE_SYNDROME =
      TermId.constructWithPrefix("HP:0001466");

  /**
   * {@link TermId} for "autosomal dominant contiguous gene syndrom.
   *
   * @see #AUTOSOMAL_DOMINANT_CONTIGUOUS_GENE_SYNDROME
   */
  public static final TermId CONTIGUOUS_GENE_SYNDROME_AUTOSOMAL_DOMINANT =
      TermId.constructWithPrefix("HP:0001452");

  /** {@link TermId} for "familial predisposition". */
  public static final TermId FAMILIAL_PREDISPOSITION =
      TermId.constructWithPrefix("HP:0001472");

  /** {@link TermId} for "genetic anticipation". */
  public static final TermId GENETIC_ANTICIPATION =
      TermId.constructWithPrefix("HP:0003743");

  /** {@link TermId} for "genetic anticipation with paternal bias". */
  public static final TermId GENETIC_ANTICIPATION_PATERNAL_BIAS =
      TermId.constructWithPrefix("HP:0003744");

  /** {@link TermId} for "sporadic"/"isolated cases". */
  public static final TermId SPORADIC = TermId.constructWithPrefix("HP:0003745");

  /** {@link TermId} for "gonosomal inheritance". */
  public static final TermId GONOSOMAL = TermId.constructWithPrefix("HP:0010985");

  /** {@link TermId} for "X-linked inheritance". */
  public static final TermId X_LINKED = TermId.constructWithPrefix("HP:0001417");

  /** {@link TermId} for "X-linked dominant inheritance. */
  public static final TermId X_LINKED_DOMINANT = TermId.constructWithPrefix("HP:0001423");

  /** {@link TermId} for "X-linked recessive inheritance. */
  public static final TermId X_LINKED_RECESSIVE = TermId.constructWithPrefix("HP:0001419");

  /** {@link TermId} for "Y-linked inheritance. */
  public static final TermId Y_LINKED = TermId.constructWithPrefix("HP:0001450");
  /** {@link TermId} for "autosomal recessive inheritance. */
  public static final TermId AUTOSOMAL_RECESSIVE = TermId.constructWithPrefix("HP:0000007");
  /** {@link TermId} for "autosomal dominant inheritance. */
  public static final TermId AUTOSOMAL_DOMINANT = TermId.constructWithPrefix("HP:0000006");

  /** {@link TermId} for "autosomal dominant inheritance with paternal imprinting". */
  public static final TermId AUTOSOMAL_DOMINANT_PATERNAL_IMPRINTING =
      TermId.constructWithPrefix("HP:0012274");

  /** {@link TermId} for "autosomal dominant inheritance with maternal imprinting". */
  public static final TermId AUTOSOMAL_DOMINANT_MATERNAL_IMPRINTING =
      TermId.constructWithPrefix("HP:0012275");

  public static final TermId AUTOSOMAL_DOMINANT_GERMLINE_DENOVO =
      TermId.constructWithPrefix("HP:0025352");

  /**
   * Autosomal dominant contiguous gene syndrom (alias to {@link #CONTIGUOUS_GENE_SYNDROME}).
   *
   * @see #CONTIGUOUS_GENE_SYNDROME_AUTOSOMAL_DOMINANT
   */
  public static final TermId AUTOSOMAL_DOMINANT_CONTIGUOUS_GENE_SYNDROME =
      TermId.constructWithPrefix("HP:0001452");

  /** {@link TermId} for "sex-limited autosomal dominant". */
  public static final TermId AUTOSOMAL_DOMINANT_SEX_LIMITED =
      TermId.constructWithPrefix("HP:0001470");

  /** {@link TermId} for "male-limited autosomal dominant". */
  public static final TermId AUTOSOMAL_DOMINANT_MALE_LIMITED =
      TermId.constructWithPrefix("HP:0001475");
}
