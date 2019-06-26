package com.lauvinson.open.assistant.configuration;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

@State(name = "private-interactive-assistant", storages = {@Storage(value = "$APP_CONFIG$/private-interactive-assistant.xml")})
public class ConfigService implements PersistentStateComponent<Config> {
    private Config config = new Config();

    public static ConfigService getInstance() {
        return ServiceManager.getService(ConfigService.class);
    }

    @Override
    public Config getState() {
        return config;
    }

    @Override
    public void loadState(Config config) {
        XmlSerializerUtil.copyBean(config, this.config);
    }
}
