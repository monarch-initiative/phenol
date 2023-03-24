package org.monarchinitiative.phenol.annotations.hpo;


import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;


public class PhenotypeDotHpoaFileWriterTest {

    /** Note that the header should have a #
     */
    @Test
    public void testHpoaHeader() {
        String expected="database_id\tdisease_name\tqualifier\thpo_id\treference\tevidence\tonset\tfrequency\tsex\tmodifier\taspect\tbiocuration";
        assertEquals(expected,PhenotypeDotHpoaFileWriter.getHeaderLine());
    }

}
