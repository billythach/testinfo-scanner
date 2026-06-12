package com.testinfo.scanner;

import com.testinfo.scanner.model.TestInfoRecord;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Generates CSV report from discovered test annotations.
 * CSV Format: CLASS_NAME;TEST_NAME;TYPE;TEAM;CRITICALITY;TAGS
 */
public class CsvReportGenerator {

    private static final String[] CSV_HEADERS = {
            "CLASS_NAME", "TEST_NAME", "TYPE", "TEAM", "CRITICALITY", "TAGS"
    };

    private static final CSVFormat CSV_FORMAT = CSVFormat.TDF
            .withDelimiter(';')
            .withHeader(CSV_HEADERS)
            .withRecordSeparator('\n')
            .withTrim();

    /**
     * Generates a CSV report and writes it to the specified file.
     *
     * @param outputFile Path to the output CSV file
     * @param records List of TestInfoRecord objects to write
     * @throws IOException if an I/O error occurs
     */
    public void generateReport(Path outputFile, List<TestInfoRecord> records) throws IOException {
        // Create parent directories if they don't exist
        if (outputFile.getParent() != null) {
            Files.createDirectories(outputFile.getParent());
        }

        try (Writer writer = Files.newBufferedWriter(
                outputFile,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
             CSVPrinter printer = new CSVPrinter(writer, CSV_FORMAT)) {

            for (TestInfoRecord record : records) {
                printer.printRecord(
                        record.getClassName(),
                        record.getTestName(),
                        record.getType(),
                        record.getTeam(),
                        record.getCriticality(),
                        record.getTags()
                );
            }

            printer.flush();
        }
    }
}