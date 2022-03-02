/**
 * The package contains loaders for content of several useful files.
 * <p>
 * The {@link org.monarchinitiative.phenol.annotations.assoc.HpoAssociationLoader} is the top-level loader that returns {@link org.monarchinitiative.phenol.annotations.formats.hpo.HpoAssociationData}.
 * To provide {@link org.monarchinitiative.phenol.annotations.formats.hpo.HpoAssociationData},
 * {@link org.monarchinitiative.phenol.annotations.assoc.HpoAssociationLoader} uses sub-loaders:
 * <ul>
 *   <li>{@link org.monarchinitiative.phenol.annotations.assoc.HumanGeneInfoLoader} to load {@link org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers}</li>
 *   <li>{@link org.monarchinitiative.phenol.annotations.assoc.DiseaseToGeneAssociationLoader} to load associations between genes and diseases, and</li>
 *   <li>{@link org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseLoader} to create {@link org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease} with disease model definitions.</li>
 * </ul>
 * <p>
 */
package org.monarchinitiative.phenol.annotations.assoc;
