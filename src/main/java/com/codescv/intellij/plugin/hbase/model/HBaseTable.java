package com.codescv.intellij.plugin.hbase.model;

/**
 * represents a hbase table
 */
public class HBaseTable {
    private String name;

    public HBaseTable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("table:%s", this.name);
    }
}
