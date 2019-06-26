package com.lauvinson.open.assistant.configuration

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

import java.util.HashMap

/**
 * SettingsConfiguration
 * @author created by vinson on 2019/6/20
 */
@State(name = "private-interactive-assistant", storages = [Storage(value = "\$APP_CONFIG$/private-interactive-assistant_settings.xml")])
class SettingsConfiguration : PersistentStateComponent<SettingsConfiguration.AbilityMap> {

    val abilityMap = AbilityMap(LinkedHashMap())

    override fun getState(): AbilityMap? {
        return this.abilityMap
    }

    override fun loadState(abilityMap: AbilityMap) {
        XmlSerializerUtil.copyBean(abilityMap, this.abilityMap)
    }

    /**
     * Ability mirror
     * @author created by vinson on 2019/6/20
     */
    class AbilityMap(var api: LinkedHashMap<String, LinkedHashMap<String, String>>)

    companion object {

        var INSTANCE = instance

        private val instance: SettingsConfiguration
            get() = ServiceManager.getService(SettingsConfiguration::class.java)
    }
}
