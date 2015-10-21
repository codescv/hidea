package com.codescv.intellij.plugin.hbase.model;

import java.util.ArrayList;
import java.util.List;

/**
 * represents a hbase server
 */
public class HBaseServer {
    private List<HBaseTable> tables = new ArrayList<HBaseTable>();
    private HBaseServerConfiguration config;

    public HBaseServer(HBaseServerConfiguration config) {
        this.config = config;
    }

    public String getName() {
        return config.getName();
    }

    public List<HBaseTable> getTables() {
        return tables;
    }

    public void setTables(List<HBaseTable> tables) {
        this.tables = tables;
    }

    @Override
    public String toString() {
        return String.format("server:%s", getName());
    }

    public HBaseServerConfiguration getConfig() {
        return config;
    }
}
