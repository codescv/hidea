package com.codescv.intellij.plugin.hbase.logic;

import com.codescv.intellij.plugin.hbase.HBasePluginConfiguration;
import com.codescv.intellij.plugin.hbase.model.HBaseServer;
import com.codescv.intellij.plugin.hbase.model.HBaseServerConfiguration;
import com.codescv.intellij.plugin.hbase.model.HBaseTable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;

/**
 * Load and keep track of a list of hbase servers,
 * manage the CRUD operations on the tables of those servers
 */
public class HBaseManager {
    private List<HBaseServer> servers = new ArrayList<>();
    private List<HBaseServerConfiguration> serverConfigurations = new ArrayList<>();

    public HBaseManager() {

    }

    public static HBaseManager getInstance(Project project) {
        return ServiceManager.getService(project, HBaseManager.class);
    }

    public void reloadServers(HBasePluginConfiguration configuration) {
        serverConfigurations = configuration.getServerConfigurations();

        // load tables on servers
        servers.clear();
        for (HBaseServerConfiguration serverConfig: serverConfigurations) {
            HBaseServer server = new HBaseServer(serverConfig);
            Configuration config = serverConfig.getHBaseConfiguration();

            // System.out.println("quorum:" + config.get("hbase.zookeeper.quorum"));
            // System.out.println("port:" + config.get("hbase.zookeeper.property.clientPort"));

            try {
                Connection connection = ConnectionFactory.createConnection(config);
                Admin admin = connection.getAdmin();
                HTableDescriptor[] tableDescriptor = admin.listTables();
                List<HBaseTable> tables = new ArrayList<>();
                for (HTableDescriptor tableDesc : tableDescriptor) {
                    String tableName = tableDesc.getNameAsString();
                    tables.add(new HBaseTable(tableName));
                }
                server.setTables(tables);
            } catch (IOException e) {
                e.printStackTrace();
            }

            servers.add(server);
        }
    }

    private Connection getConnection(HBaseServerConfiguration serverConfig) {
        try {
            Configuration config = serverConfig.getHBaseConfiguration();
            return ConnectionFactory.createConnection(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Table getTable(HBaseServerConfiguration serverConfig, String tableName) {
        try {
            Configuration config = serverConfig.getHBaseConfiguration();
            Connection connection = ConnectionFactory.createConnection(config);
            return connection.getTable(TableName.valueOf(tableName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultScanner scan(HBaseServerConfiguration serverConfig, String tableName, String rowPrefix) {
        Table table = getTable(serverConfig, tableName);
        if (table == null) {
            return null;
        }

        Scan scan = new Scan();
        if (!rowPrefix.isEmpty())
            scan.setRowPrefixFilter(rowPrefix.getBytes());
        scan.setCaching(1000);
        scan.setCacheBlocks(false);

        try {
            return table.getScanner(scan);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<HBaseServer> getServers() {
        return servers;
    }

}
