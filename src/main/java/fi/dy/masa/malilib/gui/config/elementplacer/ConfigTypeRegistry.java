package fi.dy.masa.malilib.gui.config.elementplacer;

import java.util.HashMap;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigTypeRegistry
{
    public static final ConfigTypeRegistry INSTANCE = new ConfigTypeRegistry();

    private final HashMap<ConfigType, ConfigElementPlacer<?>> elementPlacers = new HashMap<>();
    private final ConfigElementPlacer<?> missing = new MissingElementPlacer();

    private ConfigTypeRegistry()
    {
        this.registerDefaultPlacers();
    }

    /**
     * Registers a config screen element placer for the given config type
     * @param type
     * @param placer
     */
    public void registerElementPlacer(ConfigType type, ConfigElementPlacer<?> placer)
    {
        this.elementPlacers.put(type, placer);
    }

    @SuppressWarnings("unchecked")
    public <C extends ConfigInfo> ConfigElementPlacer<C> getElementPlacer(C config)
    {
        try
        {
            return (ConfigElementPlacer<C>) this.elementPlacers.getOrDefault(config.getType(), this.missing);
        }
        catch (Exception e)
        {
            return (cfg, cont, gui) -> {
                int x = cont.getX();
                int y = cont.getY();
                cont.addLabel(x + 10, y + 4, 0xFFFFFFFF, StringUtils.translate(
                        "malilib.gui.label_error.no_element_placer_for_config_type", cfg.getType().getName()));
            };
        }
    }

    private void registerDefaultPlacers()
    {
        this.registerElementPlacer(ConfigType.BOOLEAN, new BooleanConfigPlacer());
    }
}
