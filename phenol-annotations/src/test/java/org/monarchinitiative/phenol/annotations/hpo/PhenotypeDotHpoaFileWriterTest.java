package org.monarchinitiative.phenol.annotations.hpo;


import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;


class PhenotypeDotHpoaFileWriterTest {

    /** Note that the header should have a #
     */
    @Test
    void testV2Header() {
        String expected="#DatabaseID\tDiseaseName\tQualifier\tHPO_ID\tReference\tEvidence\tOnset\tFrequency\tSex\tModifier\tAspect\tBiocuration";
        assertEquals(expected,PhenotypeDotHpoaFileWriter.getHeaderLine());
    }

}
