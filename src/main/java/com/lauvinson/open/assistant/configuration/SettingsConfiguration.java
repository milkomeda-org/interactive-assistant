package com.lauvinson.open.assistant.configuration;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * SettingsConfiguration
 * @author created by vinson on 2019/6/20
 */
@State(name = "private-interactive-assistant",storages = {@Storage(value = "$APP_CONFIG$/private-interactive-assistant_settings.xml")})
public class SettingsConfiguration implements PersistentStateComponent<SettingsConfiguration.AbilityMap> {

    @Nullable
    @Override
    public AbilityMap getState() {
        return this.abilityMap;
    }

    private AbilityMap abilityMap = new AbilityMap();

    @Override
    public void loadState(@NotNull AbilityMap abilityMap) {
        XmlSerializerUtil.copyBean(abilityMap, this.abilityMap);
    }

    public static SettingsConfiguration INSTANCE = SettingsConfiguration.getInstance();

    private static SettingsConfiguration getInstance() {
        return ServiceManager.getService(SettingsConfiguration.class);
    }

    /**
     * Ability mirror
     * @author created by vinson on 2019/6/20
     */
    static class AbilityMap {
        /** alias - api **/
        private Map<String, String> api = new HashMap<>();
    }
}
