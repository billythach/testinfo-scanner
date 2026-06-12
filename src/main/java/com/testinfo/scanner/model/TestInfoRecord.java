package com.testinfo.scanner.model;

import java.util.Objects;

/**
 * Represents a single test discovered by the scanner.
 */
public class TestInfoRecord {
    private final String className;
    private final String testName;
    private final String type;
    private final String team;
    private final String criticality;
    private final String tags;

    public TestInfoRecord(String className, String testName, String type, String team, String criticality, String tags) {
        this.className = className;
        this.testName = testName != null ? testName : "";
        this.type = type != null ? type : "";
        this.team = team != null ? team : "";
        this.criticality = criticality != null ? criticality : "";
        this.tags = tags != null ? tags : "";
    }

    public String getClassName() {
        return className;
    }

    public String getTestName() {
        return testName;
    }

    public String getType() {
        return type;
    }

    public String getTeam() {
        return team;
    }

    public String getCriticality() {
        return criticality;
    }

    public String getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestInfoRecord record = (TestInfoRecord) o;
        return Objects.equals(className, record.className) &&
                Objects.equals(testName, record.testName) &&
                Objects.equals(type, record.type) &&
                Objects.equals(team, record.team) &&
                Objects.equals(criticality, record.criticality) &&
                Objects.equals(tags, record.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, testName, type, team, criticality, tags);
    }

    @Override
    public String toString() {
        return "TestInfoRecord{" +
                "className='" + className + '\'' +
                ", testName='" + testName + '\'' +
                ", type='" + type + '\'' +
                ", team='" + team + '\'' +
                ", criticality='" + criticality + '\'' +
                ", tags='" + tags + '\'' +
                '}';
    }
}