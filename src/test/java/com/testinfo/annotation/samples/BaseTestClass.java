package com.testinfo.annotation.samples;

import com.testinfo.annotation.TestInfo;
import org.junit.jupiter.api.Test;

/**
 * Base test class with @TestInfo annotation.
 * Demonstrates inheritance scenario for test annotation scanning.
 */
@TestInfo(
        type = "UNIT",
        team = "Core",
        criticality = "HIGH",
        tags = {"base-class", "inheritance"}
)
public class BaseTestClass {

    @Test
    @TestInfo(
            type = "UNIT",
            team = "Core",
            criticality = "CRITICAL",
            tags = {"base-method"}
    )
    public void testBaseMethod() {
        // Base test implementation
    }

    @Test
    public void testBaseMethodWithoutAnnotation() {
        // Base test without specific annotation
    }
}
