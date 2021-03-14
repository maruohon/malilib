package fi.dy.masa.malilib.gui.config;

import java.util.List;
import java.util.function.Function;
import net.minecraft.client.gui.GuiScreen;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.BaseScreenTab;

public class BaseConfigTab extends BaseScreenTab implements ConfigTab
{
    protected final List<? extends ConfigInfo> configs;
    protected final String name;
    protected final String modName;
    protected final int configWidth;

    public BaseConfigTab(String translationKey, String modName, int configWidth,
                         List<? extends ConfigInfo> configs, Function<GuiScreen, BaseScreen> screenFactory)
    {
        // The current screen is also a config screen, so a simple tab switch is enough
        super(translationKey, (scr) -> scr instanceof BaseConfigScreen, screenFactory);

        this.name = translationKey.substring(translationKey.lastIndexOf(".") + 1);
        this.modName = modName;
        this.configWidth = configWidth;
        this.configs = configs;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getModName()
    {
        return this.modName;
    }

    @Override
    public int getConfigWidth()
    {
        return this.configWidth;
    }

    @Override
    public boolean showOnConfigScreen()
    {
        return true;
    }

    @Override
    public List<? extends ConfigInfo> getConfigsForDisplay()
    {
        return this.configs;
    }
}
