package com.testinfo.scanner;

import com.testinfo.annotation.TestInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.ScanResult;
import com.testinfo.scanner.model.TestInfoRecord;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Scanner for discovering @TestInfo annotations on test classes and methods.
 * Uses ClassGraph to scan the classpath for annotated classes.
 * 
 * Supports:
 * - Class-level annotations
 * - Method-level annotations
 * - Inherited annotations from parent classes
 * - Package filtering
 */
public class AnnotationScanner {

    /**
     * Discovers all @TestInfo annotations in the specified classpath.
     *
     * @param classpath Path to scan (typically target/test-classes)
     * @return List of TestInfoRecord objects discovered
     */
    public List<TestInfoRecord> discoverAnnotations(Path classpath) {
        return discoverAnnotations(classpath, null);
    }

    /**
     * Discovers all @TestInfo annotations in the specified classpath and package filter.
     * Handles class inheritance - annotations from parent classes are inherited by child classes.
     *
     * @param classpath Path to scan (typically target/test-classes)
     * @param packageFilter Optional package prefix filter (e.g., "com.example")
     * @return List of TestInfoRecord objects discovered
     */
    public List<TestInfoRecord> discoverAnnotations(Path classpath, String packageFilter) {
        List<TestInfoRecord> records = new ArrayList<>();
        Set<String> processedClasses = new HashSet<>();

        try (ScanResult scanResult = new ClassGraph()
                .overrideClasspath(classpath.toFile())
                .enableClassInfo()
                .enableMethodInfo()
                .enableAnnotationInfo()
                .scan()) {

            // First, get all classes with @TestInfo annotation (directly annotated)
            for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(TestInfo.class.getName())) {
                String className = classInfo.getName();

                // Apply package filter if provided
                if (packageFilter != null && !className.startsWith(packageFilter)) {
                    continue;
                }

                processClass(classInfo, records, processedClasses);
            }

            // Second, scan all classes to find those that inherit @TestInfo from parent
            for (ClassInfo classInfo : scanResult.getAllClasses()) {
                String className = classInfo.getName();

                // Skip if already processed
                if (processedClasses.contains(className)) {
                    continue;
                }

                // Apply package filter if provided
                if (packageFilter != null && !className.startsWith(packageFilter)) {
                    continue;
                }

                // Check if this class has annotation through inheritance
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.getAnnotation(TestInfo.class) != null) {
                        processClass(classInfo, records, processedClasses);
                    }
                } catch (ClassNotFoundException e) {
                    // Silently ignore
                }
            }
        }

        return records;
    }

    /**
     * Process a single class and extract all @TestInfo annotations.
     */
    private void processClass(ClassInfo classInfo, List<TestInfoRecord> records, Set<String> processedClasses) {
        String className = classInfo.getName();
        
        // Avoid duplicate processing
        if (processedClasses.contains(className)) {
            return;
        }
        processedClasses.add(className);

        // Check for class-level annotation (via reflection to get inherited annotation)
        TestInfo classAnnotation = extractAnnotation(classInfo);
        if (classAnnotation != null) {
            records.add(new TestInfoRecord(
                    className,
                    "", // blank TEST_NAME for class-level
                    classAnnotation.type(),
                    classAnnotation.team(),
                    classAnnotation.criticality(),
                    arrayToString(classAnnotation.tags())
            ));
        }

        // Check for method-level annotations (including inherited methods)
        Set<String> processedMethods = new HashSet<>();
        for (MethodInfo methodInfo : classInfo.getMethodInfo()) {
            if (methodInfo.hasAnnotation(TestInfo.class.getName())) {
                String methodKey = methodInfo.getName();
                if (!processedMethods.contains(methodKey)) {
                    TestInfo methodAnnotation = extractMethodAnnotation(classInfo, methodInfo);
                    if (methodAnnotation != null) {
                        records.add(new TestInfoRecord(
                                className,
                                methodInfo.getName(),
                                methodAnnotation.type(),
                                methodAnnotation.team(),
                                methodAnnotation.criticality(),
                                arrayToString(methodAnnotation.tags())
                        ));
                        processedMethods.add(methodKey);
                    }
                }
            }
        }
    }

    /**
     * Extracts @TestInfo annotation from a class using reflection.
     * Handles both directly declared and inherited annotations.
     */
    private TestInfo extractAnnotation(ClassInfo classInfo) {
        try {
            Class<?> clazz = Class.forName(classInfo.getName());
            // getAnnotation checks the class and parent classes
            return clazz.getAnnotation(TestInfo.class);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Extracts @TestInfo annotation from a method using reflection.
     * Handles both methods declared in the class and inherited methods.
     */
    private TestInfo extractMethodAnnotation(ClassInfo classInfo, MethodInfo methodInfo) {
        try {
            Class<?> clazz = Class.forName(classInfo.getName());
            // Try to find the method in the class or its parents
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodInfo.getName())) {
                    return method.getAnnotation(TestInfo.class);
                }
            }
            // If not found in declared methods, try all methods (includes inherited)
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodInfo.getName())) {
                    return method.getAnnotation(TestInfo.class);
                }
            }
        } catch (ClassNotFoundException e) {
            // Silently ignore
        }
        return null;
    }

    /**
     * Converts a string array to a comma-separated string.
     */
    private String arrayToString(String[] array) {
        if (array == null || array.length == 0) {
            return "";
        }
        return Arrays.stream(array)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(","));
    }
}
