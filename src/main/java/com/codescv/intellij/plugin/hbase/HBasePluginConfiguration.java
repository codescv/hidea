/*
 * Copyright (c) 2013 David Boissier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codescv.intellij.plugin.hbase;

import com.codescv.intellij.plugin.hbase.model.HBaseServerConfiguration;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;

import java.util.ArrayList;
import java.util.List;

@State(
        name = "HBaseConfiguration",
        storages = {
                @Storage(file = "$PROJECT_FILE$"),
                @Storage(file = "$PROJECT_CONFIG_DIR$/hbaseSettings.xml", scheme = StorageScheme.DIRECTORY_BASED)
        }
)

public class HBasePluginConfiguration implements PersistentStateComponent<HBasePluginConfiguration> {

    private List<HBaseServerConfiguration> serverConfigurations = new ArrayList<>();

    public static HBasePluginConfiguration getInstance(Project project) {
        return ServiceManager.getService(project, HBasePluginConfiguration.class);
    }

    public HBasePluginConfiguration getState() {
        return this;
    }

    public void loadState(HBasePluginConfiguration hbasePluginConfiguration) {
        XmlSerializerUtil.copyBean(hbasePluginConfiguration, this);
    }

    public void setServerConfigurations(List<HBaseServerConfiguration> serverConfigurations) {
        this.serverConfigurations = serverConfigurations;
    }

    public List<HBaseServerConfiguration> getServerConfigurations() {
        return serverConfigurations;
    }
}
