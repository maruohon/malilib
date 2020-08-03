package fi.dy.masa.malilib.gui.config;

import java.util.HashMap;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.option.ConfigInfo;

public class ConfigTypeRegistry
{
    public static final ConfigTypeRegistry INSTANCE = new ConfigTypeRegistry();

    private final HashMap<ConfigType, ConfigOptionWidgetFactory> widgetFactories = new HashMap<>();
    private final ConfigOptionWidgetFactory missingTypeFactory = new MissingConfigTypeFactory();

    private ConfigTypeRegistry()
    {
        this.registerDefaultPlacers();
    }

    /**
     * Registers a config screen widget factory for the given config type
     * @param type
     * @param factory
     */
    public void registerWidgetFactory(ConfigType type, ConfigOptionWidgetFactory factory)
    {
        this.widgetFactories.put(type, factory);
    }

    public ConfigOptionWidgetFactory getWidgetFactory(ConfigInfo config)
    {
        return this.widgetFactories.getOrDefault(config.getType(), this.missingTypeFactory);
    }

    private void registerDefaultPlacers()
    {
        //this.registerWidgetFactory(ConfigType.BOOLEAN, new BooleanConfigPlacer());
    }
}
