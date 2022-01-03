package org.monarchinitiative.phenol.annotations.hpo;


import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;


public class PhenotypeDotHpoaFileWriterTest {

    /** Note that the header should have a #
     */
    @Test
    public void testV2Header() {
        String expected="#DatabaseID\tDiseaseName\tQualifier\tHPO_ID\tReference\tEvidence\tOnset\tFrequency\tSex\tModifier\tAspect\tBiocuration";
        assertEquals(expected,PhenotypeDotHpoaFileWriter.getHeaderLine());
    }

}
