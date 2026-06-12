package com.testinfo.scanner;

import com.testinfo.scanner.model.TestInfoRecord;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for AnnotationScanner.
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
}