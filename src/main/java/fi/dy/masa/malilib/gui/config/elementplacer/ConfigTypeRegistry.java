package fi.dy.masa.malilib.gui.config.elementplacer;

import java.util.HashMap;
import fi.dy.masa.malilib.config.ConfigType;

public class ConfigTypeRegistry
{
    public static final ConfigTypeRegistry INSTANCE = new ConfigTypeRegistry();

    private final HashMap<ConfigType, ConfigElementPlacer> elementPlacers = new HashMap<>();

    private ConfigTypeRegistry()
    {
        this.registerDefaultPlacers();
    }

    /**
     * Registers a config screen element placer for the given config type
     * @param type
     * @param placer
     */
    public void registerElementPlacer(ConfigType type, ConfigElementPlacer placer)
    {
        this.elementPlacers.put(type, placer);
    }

    private void registerDefaultPlacers()
    {
        this.registerElementPlacer(ConfigType.BOOLEAN, new BooleanConfigPlacer());
    }
}
