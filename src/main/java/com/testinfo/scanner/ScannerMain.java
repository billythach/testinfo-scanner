package com.testinfo.scanner;

import com.testinfo.scanner.model.TestInfoRecord;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main entry point for the TestInfo Scanner.
 * Can be invoked from Maven using exec-maven-plugin.
 * 
 * Usage:
 *   java -cp ... com.testinfo.scanner.ScannerMain <classpath> <outputFile> [packageFilter]
 * 
 * Example:
 *   java -cp ... com.testinfo.scanner.ScannerMain target/test-classes target/test-reports/testinfo-report.csv com.example
 */
public class ScannerMain {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: ScannerMain <classpath> <outputFile> [packageFilter]");
            System.err.println("Example: ScannerMain target/test-classes target/test-reports/testinfo-report.csv com.example");
            System.exit(1);
        }

        String classesPath = args[0];
        String outputFile = args[1];
        String packageFilter = args.length > 2 ? args[2] : null;

        try {
            Path classpath = Paths.get(classesPath);
            Path output = Paths.get(outputFile);

            System.out.println("Scanning for @TestInfo annotations in: " + classpath);
            if (packageFilter != null) {
                System.out.println("Package filter: " + packageFilter);
            }

            AnnotationScanner scanner = new AnnotationScanner();
            List<TestInfoRecord> records = scanner.discoverAnnotations(classpath, packageFilter);

            System.out.println("Found " + records.size() + " annotated test(s)");

            CsvReportGenerator generator = new CsvReportGenerator();
            generator.generateReport(output, records);

            System.out.println("Report generated: " + output);

        } catch (IOException e) {
            System.err.println("Error generating report: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}