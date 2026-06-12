package com.testinfo.annotation.samples;

import com.testinfo.annotation.TestInfo;
import org.junit.jupiter.api.Test;

/**
 * Test class that inherits from BaseTestClass and adds its own @TestInfo annotation.
 * This demonstrates a child class overriding/adding its own annotations.
 * 
 * The scanner should detect:
 * 1. The child class's own @TestInfo annotation
 * 2. Methods inherited from parent with their own @TestInfo annotations
 * 3. New methods with @TestInfo annotations defined in the child class
 */
@TestInfo(
        type = "INTEGRATION",
        team = "Backend",
        criticality = "CRITICAL",
        tags = {"inherited-with-override", "integration-tests"}
)
public class InheritedTestWithOwnAnnotations extends BaseTestClass {

    @Test
    @TestInfo(
            type = "INTEGRATION",
            team = "Backend",
            criticality = "CRITICAL",
            tags = {"database", "persistence"}
    )
    public void testDatabaseIntegration() {
        // Integration test specific to child class
    }

    @Test
    @TestInfo(
            type = "INTEGRATION",
            team = "Backend",
            criticality = "HIGH",
            tags = {"api-integration"}
    )
    public void testApiIntegration() {
        // Another integration test
    }

    @Test
    public void testChildMethodWithoutAnnotation() {
        // Child method without annotation
    }
}
