package com.testinfo.scanner;

import com.testinfo.scanner.model.TestInfoRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for CsvReportGenerator.
 */
public class CsvReportGeneratorTest {

    private CsvReportGenerator generator;

    @BeforeEach
    public void setUp() {
        generator = new CsvReportGenerator();
    }

    @Test
    public void testGenerateReport(@TempDir Path tempDir) throws IOException {
        Path outputFile = tempDir.resolve("report.csv");

        List<TestInfoRecord> records = Arrays.asList(
                new TestInfoRecord("com.example.Test1", "", "UNIT", "TeamA", "HIGH", "tag1,tag2"),
                new TestInfoRecord("com.example.Test2", "testMethod", "INTEGRATION", "TeamB", "CRITICAL", "tag3")
        );

        generator.generateReport(outputFile, records);

        assertThat(outputFile).exists();

        String content = Files.readString(outputFile, StandardCharsets.UTF_8);
        assertThat(content).contains("CLASS_NAME;TEST_NAME;TYPE;TEAM;CRITICALITY;TAGS");
        assertThat(content).contains("com.example.Test1");
        assertThat(content).contains("com.example.Test2;;INTEGRATION;TeamB;CRITICAL;tag3");
    }

    @Test
    public void testGenerateReportWithEmptyList(@TempDir Path tempDir) throws IOException {
        Path outputFile = tempDir.resolve("empty-report.csv");
        List<TestInfoRecord> records = Arrays.asList();

        generator.generateReport(outputFile, records);

        assertThat(outputFile).exists();
        String content = Files.readString(outputFile, StandardCharsets.UTF_8);
        assertThat(content).contains("CLASS_NAME;TEST_NAME;TYPE;TEAM;CRITICALITY;TAGS");
    }

    @Test
    public void testGenerateReportCreatesParentDirectories(@TempDir Path tempDir) throws IOException {
        Path outputFile = tempDir.resolve("subdir1/subdir2/report.csv");
        List<TestInfoRecord> records = Arrays.asList(
                new TestInfoRecord("com.example.Test1", "", "UNIT", "TeamA", "MEDIUM", "")
        );

        generator.generateReport(outputFile, records);

        assertThat(outputFile).exists();
        assertThat(outputFile.getParent()).exists();
    }
}