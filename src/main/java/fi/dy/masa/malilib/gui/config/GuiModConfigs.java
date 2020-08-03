package fi.dy.masa.malilib.gui.config;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.ConfigInfo;

public class GuiModConfigs extends BaseConfigScreen
{
    protected final List<? extends ConfigInfo> configs;

    public GuiModConfigs(String modId, List<? extends ConfigInfo> configs, String titleKey, Object... args)
    {
        super(10, 0, modId, null, ImmutableList.of(), titleKey, args);

        this.configs = configs;
    }

    @Override
    protected int getBrowserHeight()
    {
        return this.height - 70;
    }

    @Override
    @Nullable
    public ConfigTab getCurrentTab()
    {
        return null;
    }

    @Override
    public void setCurrentTab(ConfigTab tab)
    {
        // NO-OP
    }

    @Override
    protected void createTabButtons()
    {
        // NO-OP
    }

    @Override
    public List<? extends ConfigInfo> getConfigs()
    {
        return this.configs;
    }

    @Override
    protected void drawTitle(int mouseX, int mouseY, float partialTicks)
    {
        // NO-OP
    }
}
