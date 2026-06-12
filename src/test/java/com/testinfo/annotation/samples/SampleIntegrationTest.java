package com.testinfo.annotation.samples;

import com.testinfo.annotation.TestInfo;
import org.junit.jupiter.api.Test;

/**
 * Sample integration test class with @TestInfo annotations.
 */
@TestInfo(
        type = "INTEGRATION",
        team = "Backend",
        criticality = "HIGH",
        tags = {"database", "integration"}
)
public class SampleIntegrationTest {

    @Test
    @TestInfo(
            type = "INTEGRATION",
            team = "Backend",
            criticality = "CRITICAL",
            tags = {"database", "persistence"}
    )
    public void testDatabaseConnection() {
        // Integration test implementation
    }

    @Test
    @TestInfo(
            type = "INTEGRATION",
            team = "API",
            criticality = "HIGH",
            tags = {"api", "rest-endpoint"}
    )
    public void testRestApi() {
        // Integration test implementation
    }
}