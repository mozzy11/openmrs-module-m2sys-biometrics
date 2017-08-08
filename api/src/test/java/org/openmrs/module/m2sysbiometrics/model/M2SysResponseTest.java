package org.openmrs.module.m2sysbiometrics.model;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class M2SysResponseTest {

    @Test
    @Ignore
    public void shouldParseMatchingResult() throws Exception {
        M2SysResponse response = new M2SysResponse();
        response.setMatchingResult(readFile("sampleMatchingResult.xml"));

        M2SysMatchingResult matchingResult = response.parseMatchingResult();
        M2SysResult result = matchingResult.getResults().get(0);

        assertNotNull(matchingResult);
        assertEquals(0, result.getScore());
        assertEquals("27", result.getValue());
        assertEquals(1, result.getInstance());
    }

    private String readFile(String file) throws IOException {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(file)) {
            return IOUtils.toString(in);
        }
    }
}
