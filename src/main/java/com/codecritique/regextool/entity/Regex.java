package com.codecritique.regextool.entity;

import java.util.Objects;

public class Regex {
    private String id;
    private String value;
    private String description;
    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public Regex(String value, String description, String text) {
        this.value = value;
        this.description = description;
        this.text = text;
    }

    public Regex(String id, String value, String description, String text) {
        this(value, description, text);
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Regex regex = (Regex) o;
        return Objects.equals(id, regex.id) &&
                value.equals(regex.value) &&
                description.equals(regex.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, description);
    }

    @Override
    public String toString() {
        return "Regex [" + id + "] " + value;
    }
}
