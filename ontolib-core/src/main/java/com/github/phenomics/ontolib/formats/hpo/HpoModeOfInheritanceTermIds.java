package com.github.phenomics.ontolib.formats.hpo;

import com.github.phenomics.ontolib.ontology.data.ImmutableTermId;
import com.github.phenomics.ontolib.ontology.data.TermId;

/**
 * Utility class with constants from "Mode of Inheritance" sub ontology.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class HpoModeOfInheritanceTermIds {

  /** {@link TermId} for term "heterogeneous"/"genetic heterogeneity". */
  public static final TermId HETEROGENEOUS = ImmutableTermId.constructWithPrefix("HP:0001425");

  /** {@link TermId} for term "multifactorial inheritance". */
  public static final TermId MULTIFACTORIAL = ImmutableTermId.constructWithPrefix("HP:0001426");

  /** {@link TermId} for term "polygenic inheritance". */
  public static final TermId POLYGENIC = ImmutableTermId.constructWithPrefix("HP:0010982");

  /** {@link TermId} for term "oligogenic inheritance". */
  public static final TermId OLIGOGENIC = ImmutableTermId.constructWithPrefix("HP:0010983");

  /** {@link TermId} for term "digenic inheritance". */
  public static final TermId DIGENIC = ImmutableTermId.constructWithPrefix("HP:0010984");

  /** {@link TermId} for term "mitochondrial inheritance". */
  public static final TermId MITOCHONDRIAL = ImmutableTermId.constructWithPrefix("HP:0001427");

  /** {@link TermId} for "somatic mutation". */
  public static final TermId SOMATIC_MUTATION = ImmutableTermId.constructWithPrefix("HP:0001428");

  /** {@link TermId} for "somatic mosaicism". */
  public static final TermId SOMATIC_MOSAICISM = ImmutableTermId.constructWithPrefix("HP:0001442");

  /** {@link TermId} for "contiguous gene syndrom". */
  public static final TermId CONTIGUOUS_GENE_SYNDROME =
      ImmutableTermId.constructWithPrefix("HP:0001466");

  /**
   * {@link TermId} for "autosomal dominant contiguous gene syndrom.
   * 
   * @see #AUTOSOMAL_DOMINANT_CONTIGUOUS_GENE_SYNDROME
   */
  public static final TermId CONTIGUOUS_GENE_SYNDROME_AUTOSOMAL_DOMINANT =
      ImmutableTermId.constructWithPrefix("HP:0001452");

  /** {@link TermId} for "familial predisposition". */
  public static final TermId FAMILIAL_PREDISPOSITION =
      ImmutableTermId.constructWithPrefix("HP:0001472");

  /** {@link TermId} for "genetic anticipation". */
  public static final TermId GENETIC_ANTICIPATION =
      ImmutableTermId.constructWithPrefix("HP:0003743");

  /** {@link TermId} for "genetic anticipation with paternal bias". */
  public static final TermId GENETIC_ANTICIPATION_PATERNAL_BIAS =
      ImmutableTermId.constructWithPrefix("HP:0003744");

  /** {@link TermId} for "sporadic"/"isolated cases". */
  public static final TermId SPORADIC = ImmutableTermId.constructWithPrefix("HP:0003745");

  /** {@link TermId} for "gonosomal inheritance". */
  public static final TermId GONOSOMAL = ImmutableTermId.constructWithPrefix("HP:0010985");

  /** {@link TermId} for "X-linked inheritance". */
  public static final TermId X_LINKED = ImmutableTermId.constructWithPrefix("HP:0001417");

  /** {@link TermId} for "X-linked dominant inheritance. */
  public static final TermId X_LINKED_DOMINANT = ImmutableTermId.constructWithPrefix("HP:0001423");

  /** {@link TermId} for "X-linked recessive inheritance. */
  public static final TermId X_LINKED_RECESSIVE = ImmutableTermId.constructWithPrefix("HP:0001419");

  /** {@link TermId} for "Y-linked inheritance. */
  public static final TermId Y_LINKED = ImmutableTermId.constructWithPrefix("HP:0001450");

  /** {@link TermId} for "autosomal dominant inheritance. */
  public static final TermId AUTOSOMAL_DOMINANT = ImmutableTermId.constructWithPrefix("HP:0000006");

  /** {@link TermId} for "autosomal dominant inheritance with paternal imprinting". */
  public static final TermId AUTOSOMAL_DOMINANT_PATERNAL_IMPRINTING =
      ImmutableTermId.constructWithPrefix("HP:0012274");

  /** {@link TermId} for "autosomal dominant inheritance with maternal imprinting". */
  public static final TermId AUTOSOMAL_DOMINANT_MATERNAL_IMPRINTING =
      ImmutableTermId.constructWithPrefix("HP:0012275");

  public static final TermId AUTOSOMAL_DOMINANT_GERMLINE_DENOVO =
      ImmutableTermId.constructWithPrefix("HP:0025352");

  /**
   * Autosomal dominant contiguous gene syndrom (alias to {@link #CONTIGUOUS_GENE_SYNDROME}).
   *
   * @see #CONTIGUOUS_GENE_SYNDROME_AUTOSOMAL_DOMINANT
   */
  public static final TermId AUTOSOMAL_DOMINANT_CONTIGUOUS_GENE_SYNDROME =
      ImmutableTermId.constructWithPrefix("HP:0001452");

  /** {@link TermId} for "sex-limited autosomal dominant". */
  public static final TermId AUTOSOMAL_DOMINANT_SEX_LIMITED =
      ImmutableTermId.constructWithPrefix("HP:0001470");

  /** {@link TermId} for "male-limited autosomal dominant". */
  public static final TermId AUTOSOMAL_DOMINANT_MALE_LIMITED =
      ImmutableTermId.constructWithPrefix("HP:0001475");
}