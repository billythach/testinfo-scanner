package com.testinfo.annotation.samples;

import com.testinfo.annotation.TestInfo;
import org.junit.jupiter.api.Test;

/**
 * Sample test class with @TestInfo annotations to demonstrate scanner usage.
 */
@TestInfo(
        type = "UNIT",
        team = "Core",
        criticality = "HIGH",
        tags = {"fast", "core-functionality"}
)
public class SampleUnitTest {

    @Test
    @TestInfo(
            type = "UNIT",
            team = "Core",
            criticality = "CRITICAL",
            tags = {"auth", "security"}
    )
    public void testAuthenticationLogic() {
        // Test implementation
    }

    @Test
    @TestInfo(
            type = "UNIT",
            team = "Utils",
            criticality = "MEDIUM",
            tags = {"utility", "string-handling"}
    )
    public void testStringUtility() {
        // Test implementation
    }

    @Test
    public void testWithoutAnnotation() {
        // This test has no @TestInfo annotation
    }
}