package com.codecritique.regextool.entity;


import java.util.Objects;

public class Regex {
    private int id;
    private String value;
    private String description;
    private String text;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public String getText() {
        return text;
    }

    public Regex(String value, String description, String text) {
        this.value = value;
        this.description = description;
        this.text = text;
    }

    public Regex(int id, String value, String description, String text) {
        this(value, description, text);
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Regex regex = (Regex) o;
        return id == regex.id &&
                value.equals(regex.value) &&
                description.equals(regex.description) &&
                text.equals(regex.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, description, text);
    }

    @Override
    public String toString() {
        return "Regex [" + id + "] " + value;
    }
}
