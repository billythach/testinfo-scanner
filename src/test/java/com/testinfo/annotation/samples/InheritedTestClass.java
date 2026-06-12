package com.testinfo.annotation.samples;

import org.junit.jupiter.api.Test;

/**
 * Test class that inherits from BaseTestClass without adding its own @TestInfo annotation.
 * Inherits the class-level annotation and methods from the base class.
 * 
 * This tests whether the scanner correctly handles inherited test methods
 * and inherited class-level annotations.
 */
public class InheritedTestClass extends BaseTestClass {

    @Test
    public void testInheritedChild() {
        // Child class test without explicit annotation
        // Should inherit the class-level @TestInfo from parent
    }

    @Test
    public void testAnotherChildMethod() {
        // Another child method
    }
}
