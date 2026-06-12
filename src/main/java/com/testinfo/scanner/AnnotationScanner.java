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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Scanner for discovering @TestInfo annotations on test classes and methods.
 * Uses ClassGraph to scan the classpath for annotated classes.
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
     *
     * @param classpath Path to scan (typically target/test-classes)
     * @param packageFilter Optional package prefix filter (e.g., "com.example")
     * @return List of TestInfoRecord objects discovered
     */
    public List<TestInfoRecord> discoverAnnotations(Path classpath, String packageFilter) {
        List<TestInfoRecord> records = new ArrayList<>();

        try (ScanResult scanResult = new ClassGraph()
                .overrideClasspath(classpath.toFile())
                .enableClassInfo()
                .enableMethodInfo()
                .enableAnnotationInfo()
                .scan()) {

            for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(TestInfo.class.getName())) {
                String className = classInfo.getName();

                // Apply package filter if provided
                if (packageFilter != null && !className.startsWith(packageFilter)) {
                    continue;
                }

                // Check for class-level annotation
                if (classInfo.hasAnnotation(TestInfo.class.getName())) {
                    TestInfo annotation = extractAnnotation(classInfo);
                    if (annotation != null) {
                        records.add(new TestInfoRecord(
                                className,
                                "", // blank TEST_NAME for class-level
                                annotation.type(),
                                annotation.team(),
                                annotation.criticality(),
                                arrayToString(annotation.tags())
                        ));
                    }
                }

                // Check for method-level annotations
                for (MethodInfo methodInfo : classInfo.getMethodInfo()) {
                    if (methodInfo.hasAnnotation(TestInfo.class.getName())) {
                        TestInfo annotation = extractMethodAnnotation(classInfo, methodInfo);
                        if (annotation != null) {
                            records.add(new TestInfoRecord(
                                    className,
                                    methodInfo.getName(),
                                    annotation.type(),
                                    annotation.team(),
                                    annotation.criticality(),
                                    arrayToString(annotation.tags())
                            ));
                        }
                    }
                }
            }
        }

        return records;
    }

    /**
     * Extracts @TestInfo annotation from a class using reflection.
     */
    private TestInfo extractAnnotation(ClassInfo classInfo) {
        try {
            Class<?> clazz = Class.forName(classInfo.getName());
            return clazz.getAnnotation(TestInfo.class);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Extracts @TestInfo annotation from a method using reflection.
     */
    private TestInfo extractMethodAnnotation(ClassInfo classInfo, MethodInfo methodInfo) {
        try {
            Class<?> clazz = Class.forName(classInfo.getName());
            // Try to find the method (without parameter types for simplicity)
            for (Method method : clazz.getDeclaredMethods()) {
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