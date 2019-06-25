package org.monarchinitiative.phenol.io.annotations.hpo;


import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;


class PhenotypeDotHpoaFileWriterTest {

    /** Note that the header should NOT have a #, which is used for the
     * preceding comment lines.
     */
    @Test
    void testV2Header() {
        String expected="DatabaseID\tDiseaseName\tQualifier\tHPO_ID\tReference\tEvidence\tOnset\tFrequency\tSex\tModifier\tAspect\tBiocuration";
        assertEquals(expected,PhenotypeDotHpoaFileWriter.getHeaderLine());
    }

}
