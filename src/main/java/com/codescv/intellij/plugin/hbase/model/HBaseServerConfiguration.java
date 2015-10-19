package com.codescv.intellij.plugin.hbase.model;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * hbase server configuration
 */
public class HBaseServerConfiguration {
    // server name (used as a label)
    private String name;
    // zookeeper host
    private String server;
    // zookeeper port
    private String port;

    public Configuration getHBaseConfiguration() {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", this.server);
        conf.set("hbase.zookeeper.property.clientPort", this.port);
        return conf;
    }

    public static HBaseServerConfiguration defaultConfiguration() {
        HBaseServerConfiguration config = new HBaseServerConfiguration();
        config.setName("local server");
        config.setServer("localhost");
        config.setPort("2181");
        return config;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
