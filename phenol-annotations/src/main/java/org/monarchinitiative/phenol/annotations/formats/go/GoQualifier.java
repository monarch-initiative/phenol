package org.monarchinitiative.phenol.annotations.formats.go;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;

public enum GoQualifier {
    /** (RO:0002327) */
    enables,
    /**(RO:0002326) */
    contributes_to,
    /** (RO:0002331) */
    involved_in,
    /** (RO:0002263) */
    acts_upstream_of,
    /** (RO:0004034) 	 */
    acts_upstream_of_positive_effect,
    /** (RO:0004035)  */
    acts_upstream_of_negative_effect,
    /** (RO:0002264) */
    acts_upstream_of_or_within,
    /** (RO:0004032) */
    acts_upstream_of_or_within_positive_effect,
    /** (RO:0004033) */
    acts_upstream_of_or_within_negative_effect,
    /** (for non-protein-containing complex terms) (RO:0001025) */
    located_in,
    /** (for protein-containing complex terms)*/
    part_of,
    /** (RO:0002432) */
    is_active_in,
    /** (RO:0002325) */
    colocalizes_with;

    public static GoQualifier fromString(String qualifierString) {
        switch (qualifierString) {
            case "enables": return enables;
            case "contributes_to": return contributes_to;
            case "involved_in": return involved_in;
            case "acts_upstream_of": return acts_upstream_of;
            case "acts_upstream_of_positive_effect": return acts_upstream_of_positive_effect;
            case "acts_upstream_of_negative_effect": return acts_upstream_of_negative_effect;
            case "acts_upstream_of_or_within": return acts_upstream_of_or_within;
            case "acts_upstream_of_or_within_positive_effect": return acts_upstream_of_or_within_positive_effect;
            case "acts_upstream_of_or_within_negative_effect": return acts_upstream_of_or_within_negative_effect;
            case "located_in": return located_in;
            case "part_of": return part_of;
            case "is_active_in": return is_active_in;
            case "colocalizes_with": return colocalizes_with;
        }
        throw new PhenolRuntimeException("Could not recognize GO qualifier \"" + qualifierString + "\"");
    }
}
