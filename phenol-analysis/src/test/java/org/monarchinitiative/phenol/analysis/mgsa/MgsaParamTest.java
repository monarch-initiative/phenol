package org.monarchinitiative.phenol.analysis.mgsa;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.monarchinitiative.phenol.analysis.mgsa.MgsaParam.Type.MCMC;


public class MgsaParamTest {

    @Test
    public void testMCMC() {
        MgsaParam param = new DoubleParam(MCMC, 0.3);
        assertTrue(param.isMCMC());

    }
}
