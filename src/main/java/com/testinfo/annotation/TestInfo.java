package com.testinfo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking test classes and methods with metadata.
 * 
 * Supported attributes:
 * - type: Test type (e.g., "UNIT", "INTEGRATION", "SMOKE")
 * - team: Team responsible for the test
 * - criticality: Criticality level (e.g., "CRITICAL", "HIGH", "MEDIUM", "LOW")
 * - tags: Array of tags for categorization
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TestInfo {
    String type() default "TEST";
    String team() default "";
    String criticality() default "";
    String[] tags() default {};
}