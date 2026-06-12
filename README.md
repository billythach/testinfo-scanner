# TestInfo Scanner

A Maven-friendly project that scans custom `@TestInfo` annotations on test classes during Maven compilation and generates a CSV report summarizing all discovered test metadata.

## Features

- **Annotation-Based Metadata**: Mark test classes and methods with `@TestInfo` to capture:
  - `type`: Test type (e.g., "UNIT", "INTEGRATION", "SMOKE")
  - `team`: Team responsible for the test
  - `criticality`: Criticality level (e.g., "CRITICAL", "HIGH", "MEDIUM", "LOW")
  - `tags`: Array of categorization tags
- **Automatic Scanning**: Discovers both class-level and method-level annotations
- **CSV Report Generation**: Produces a well-structured CSV file with columns:
  ```
  CLASS_NAME;TEST_NAME;TYPE;TEAM;CRITICALITY;TAGS
  ```
- **Maven Integration**: Easily integrates into your Maven build lifecycle

## Quick Start

### 1. Add Dependency

Add the testinfo-scanner to your test project's `pom.xml`:

```xml
<dependency>
    <groupId>com.testinfo</groupId>
    <artifactId>testinfo-scanner</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. Annotate Your Tests

```java
import com.testinfo.annotation.TestInfo;

@TestInfo(
    type = "UNIT",
    team = "Backend",
    criticality = "HIGH",
    tags = {"authentication", "security"}
)
public class AuthenticationTest {
    
    @Test
    @TestInfo(
        type = "UNIT",
        team = "Backend",
        criticality = "CRITICAL",
        tags = {"login"}
    )
    public void testLoginFlow() {
        // test implementation
    }
}
```

### 3. Configure Maven to Run Scanner

Add the following to your test module's `pom.xml` build plugins:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.1.0</version>
    <executions>
        <execution>
            <id>scan-test-annotations</id>
            <phase>process-test-classes</phase>
            <goals>
                <goal>java</goal>
            </goals>
            <configuration>
                <mainClass>com.testinfo.scanner.ScannerMain</mainClass>
                <arguments>
                    <argument>${project.build.testOutputDirectory}</argument>
                    <argument>${project.build.directory}/test-reports/testinfo-report.csv</argument>
                    <argument>com.yourcompany.tests</argument> <!-- optional package filter -->
                </arguments>
                <includePluginDependencies>true</includePluginDependencies>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>com.testinfo</groupId>
            <artifactId>testinfo-scanner</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
</plugin>
```

### 4. Run Maven Build

```bash
mvn clean test
```

The CSV report will be generated at `target/test-reports/testinfo-report.csv`.

## CSV Report Format

Example output:

```
CLASS_NAME;TEST_NAME;TYPE;TEAM;CRITICALITY;TAGS
com.example.AuthenticationTest;;UNIT;Backend;HIGH;authentication,security
com.example.AuthenticationTest;testLoginFlow;UNIT;Backend;CRITICAL;login
com.example.IntegrationTest;testDatabaseConnection;INTEGRATION;Backend;CRITICAL;database,persistence
```

### Column Descriptions

- **CLASS_NAME**: Fully-qualified class name (e.g., `com.example.MyTest`)
- **TEST_NAME**: Method name if method-level annotation, blank for class-level only
- **TYPE**: Test type (UNIT, INTEGRATION, SMOKE, etc.)
- **TEAM**: Team name responsible for the test
- **CRITICALITY**: Priority level (CRITICAL, HIGH, MEDIUM, LOW)
- **TAGS**: Comma-separated tags for categorization

## Usage Examples

### Class-Level Annotation Only

```java
@TestInfo(type = "UNIT", team = "Core", criticality = "HIGH")
public class BasicUnitTest {
    @Test
    public void testSomething() { }
}
```

CSV output:
```
com.example.BasicUnitTest;;UNIT;Core;HIGH;
```

### Method-Level Annotations

```java
public class MixedAnnotationTest {
    @Test
    @TestInfo(type = "UNIT", team = "Core", criticality = "CRITICAL", tags = {"critical-path"})
    public void testCriticalPath() { }
    
    @Test
    public void testWithoutAnnotation() { }
}
```

CSV output:
```
com.example.MixedAnnotationTest;testCriticalPath;UNIT;Core;CRITICAL;critical-path
```

### With Tags

```java
@TestInfo(
    type = "INTEGRATION",
    team = "API",
    criticality = "HIGH",
    tags = {"rest-api", "authentication", "v2"}
)
public class ApiIntegrationTest {
    @Test
    public void testApiEndpoint() { }
}
```

CSV output:
```
com.example.ApiIntegrationTest;;INTEGRATION;API;HIGH;rest-api,authentication,v2
```

## Building from Source

```bash
git clone https://github.com/billythach/testinfo-scanner.git
cd testinfo-scanner
mvn clean install
```

## API Reference

### AnnotationScanner

The core scanner class for discovering annotations.

```java
AnnotationScanner scanner = new AnnotationScanner();

// Scan all test classes
List<TestInfoRecord> records = scanner.discoverAnnotations(
    Paths.get("target/test-classes")
);

// Scan with package filter
List<TestInfoRecord> records = scanner.discoverAnnotations(
    Paths.get("target/test-classes"),
    "com.example.tests"
);
```

### CsvReportGenerator

Generates CSV reports from discovered records.

```java
CsvReportGenerator generator = new CsvReportGenerator();
generator.generateReport(
    Paths.get("target/test-reports/testinfo-report.csv"),
    records
);
```

## Testing

Run unit tests:

```bash
mvn test
```

The test suite includes:
- Scanner discovery tests with sample annotated classes
- CSV generation and format validation tests
- Record model tests

Sample annotated test classes are included in `src/test/java/com/testinfo/annotation/samples/`.

## Requirements

- Java 11+
- Maven 3.6+

## Dependencies

- **ClassGraph** (4.8.157): Efficient classpath scanning
- **Apache Commons CSV** (1.10.0): CSV file generation
- **JUnit 5** (5.9.2): Testing framework
- **AssertJ** (3.24.1): Fluent assertions

## License

MIT License - see LICENSE file for details

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## Support

For issues, questions, or suggestions, please open a GitHub issue.