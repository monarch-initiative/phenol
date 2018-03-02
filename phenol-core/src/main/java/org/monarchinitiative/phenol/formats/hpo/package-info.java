/**
 * HPO-specific implementations of {@link org.monarchinitiative.phenol.ontology.data.Ontology} and
 * related classes.
 *
 * <p>
 * The classes from this package facilitate working with the HPO by specialized types with
 * attributes relevant when working with the HPO, the official HPO annotation formats as well as
 * various shortcuts and utilities.
 * </p>
 *
 * <p>
 * You can use {@link de.charite.compbio.ontolib.io.obo.hpo.HpoOboParser} for parsing OBO-formatted
 * files and <code>String</codes>s into {@link org.monarchinitiative.phenol.formats.hpo.HpoOntology}
 * objects.
 * </p>
 *
 * <p>
 * You can use {@link de.charite.compbio.ontolib.io.obo.hpo.HpoDiseaseAnnotationParser} for loading
 * the "phenotype annotation" files and
 * {@link de.charite.compbio.ontolib.io.obo.hpo.HpoGeneAnnotationParser} for loading the "genes to
 * phenotypes" annotation file.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
package org.monarchinitiative.phenol.formats.hpo;
