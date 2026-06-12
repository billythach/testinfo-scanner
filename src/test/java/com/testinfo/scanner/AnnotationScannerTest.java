package com.testinfo.scanner;

import com.testinfo.scanner.model.TestInfoRecord;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for AnnotationScanner.
 * Tests basic annotation discovery and inheritance scenarios.
 */
public class AnnotationScannerTest {

    @Test
    public void testDiscoverAnnotations() {
        AnnotationScanner scanner = new AnnotationScanner();
        List<TestInfoRecord> records = scanner.discoverAnnotations(
                Paths.get("target/test-classes"),
                "com.testinfo.annotation.samples"
        );

        assertThat(records).isNotEmpty();
        assertThat(records.stream()
                .filter(r -> r.getClassName().equals("com.testinfo.annotation.samples.SampleUnitTest"))
                .count()
        ).isGreaterThan(0);
    }

    @Test
    public void testDiscoverAnnotationsWithoutFilter() {
        AnnotationScanner scanner = new AnnotationScanner();
        List<TestInfoRecord> records = scanner.discoverAnnotations(Paths.get("target/test-classes"));

        assertThat(records).isNotEmpty();
    }

    @Test
    public void testRecordCreation() {
        TestInfoRecord record = new TestInfoRecord(
                "com.example.MyTest",
                "testMethod",
                "UNIT",
                "TeamA",
                "HIGH",
                "tag1,tag2"
        );

        assertThat(record.getClassName()).isEqualTo("com.example.MyTest");
        assertThat(record.getTestName()).isEqualTo("testMethod");
        assertThat(record.getType()).isEqualTo("UNIT");
        assertThat(record.getTeam()).isEqualTo("TeamA");
        assertThat(record.getCriticality()).isEqualTo("HIGH");
        assertThat(record.getTags()).isEqualTo("tag1,tag2");
    }

    @Test
    public void testRecordWithBlankTestName() {
        TestInfoRecord record = new TestInfoRecord(
                "com.example.MyTest",
                null,
                "UNIT",
                "TeamA",
                "HIGH",
                "tag1"
        );

        assertThat(record.getTestName()).isEmpty();
    }

    /**
     * Test inheritance scenario: Base class with @TestInfo annotation.
     * Verifies that BaseTestClass is discovered with its annotations.
     */
    @Test
    public void testBaseClassDiscovery() {
        AnnotationScanner scanner = new AnnotationScanner();
        List<TestInfoRecord> records = scanner.discoverAnnotations(
                Paths.get("target/test-classes"),
                "com.testinfo.annotation.samples"
        );

        List<TestInfoRecord> baseClassRecords = records.stream()
                .filter(r -> r.getClassName().equals("com.testinfo.annotation.samples.BaseTestClass"))
                .collect(Collectors.toList());

        assertThat(baseClassRecords).isNotEmpty();
        // Should have class-level annotation
        assertThat(baseClassRecords.stream()
                .filter(r -> r.getTestName().isEmpty() && r.getType().equals("UNIT"))
                .count()
        ).isGreaterThan(0);
    }

    /**
     * Test inheritance scenario: Child class without own annotation inherits from base.
     * Verifies that InheritedTestClass inherits @TestInfo from BaseTestClass.
     */
    @Test
    public void testInheritedClassDiscovery() {
        AnnotationScanner scanner = new AnnotationScanner();
        List<TestInfoRecord> records = scanner.discoverAnnotations(
                Paths.get("target/test-classes"),
                "com.testinfo.annotation.samples"
        );

        List<TestInfoRecord> inheritedRecords = records.stream()
                .filter(r -> r.getClassName().equals("com.testinfo.annotation.samples.InheritedTestClass"))
                .collect(Collectors.toList());

        // InheritedTestClass should inherit @TestInfo from BaseTestClass
        assertThat(inheritedRecords).isNotEmpty();
        // Should have inherited class-level annotation with UNIT type from parent
        assertThat(inheritedRecords.stream()
                .filter(r -> r.getTestName().isEmpty() && r.getType().equals("UNIT"))
                .count()
        ).isGreaterThan(0);
    }

    /**
     * Test inheritance scenario: Child class with own annotation and inherited methods.
     * Verifies that InheritedTestWithOwnAnnotations has its own annotation and inherited methods.
     */
    @Test
    public void testInheritedClassWithOwnAnnotations() {
        AnnotationScanner scanner = new AnnotationScanner();
        List<TestInfoRecord> records = scanner.discoverAnnotations(
                Paths.get("target/test-classes"),
                "com.testinfo.annotation.samples"
        );

        List<TestInfoRecord> ownAnnotRecords = records.stream()
                .filter(r -> r.getClassName().equals("com.testinfo.annotation.samples.InheritedTestWithOwnAnnotations"))
                .collect(Collectors.toList());

        assertThat(ownAnnotRecords).isNotEmpty();
        
        // Should have class-level annotation with INTEGRATION type (own annotation)
        assertThat(ownAnnotRecords.stream()
                .filter(r -> r.getTestName().isEmpty() && r.getType().equals("INTEGRATION"))
                .count()
        ).isGreaterThan(0);
        
        // Should have method-level annotations
        assertThat(ownAnnotRecords.stream()
                .filter(r -> !r.getTestName().isEmpty())
                .count()
        ).isGreaterThan(0);
    }

    /**
     * Test that inherited methods are discovered with their annotations.
     */
    @Test
    public void testInheritedMethodsDiscovery() {
        AnnotationScanner scanner = new AnnotationScanner();
        List<TestInfoRecord> records = scanner.discoverAnnotations(
                Paths.get("target/test-classes"),
                "com.testinfo.annotation.samples"
        );

        // BaseTestClass should have testBaseMethod with annotations
        List<TestInfoRecord> baseMethodRecords = records.stream()
                .filter(r -> r.getClassName().equals("com.testinfo.annotation.samples.BaseTestClass")
                        && r.getTestName().equals("testBaseMethod"))
                .collect(Collectors.toList());

        assertThat(baseMethodRecords).isNotEmpty();
        assertThat(baseMethodRecords.get(0).getCriticality()).isEqualTo("CRITICAL");
    }

    /**
     * Test discovery of all test classes including inherited ones.
     */
    @Test
    public void testAllTestClassesDiscovery() {
        AnnotationScanner scanner = new AnnotationScanner();
        List<TestInfoRecord> records = scanner.discoverAnnotations(
                Paths.get("target/test-classes"),
                "com.testinfo.annotation.samples"
        );

        // Should find BaseTestClass, InheritedTestClass, and InheritedTestWithOwnAnnotations
        long baseClassCount = records.stream()
                .filter(r -> r.getClassName().equals("com.testinfo.annotation.samples.BaseTestClass"))
                .count();
        long inheritedClassCount = records.stream()
                .filter(r -> r.getClassName().equals("com.testinfo.annotation.samples.InheritedTestClass"))
                .count();
        long inheritedWithOwnCount = records.stream()
                .filter(r -> r.getClassName().equals("com.testinfo.annotation.samples.InheritedTestWithOwnAnnotations"))
                .count();

        assertThat(baseClassCount).isGreaterThan(0);
        assertThat(inheritedClassCount).isGreaterThan(0);
        assertThat(inheritedWithOwnCount).isGreaterThan(0);
    }
}
