package org.dulab.adapcompounddb.models;

public interface SubmissionCategory {

    long getId();
    void setId(long id);

    String getName();
    void setName(String name);

    String getDescription();
    void setDescription(String description);
}