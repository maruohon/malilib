package fi.dy.masa.malilib.config.gui;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.interfaces.IConfigGuiTab;

public class GuiModConfigs extends GuiConfigsBase
{
    protected final List<ConfigOptionWrapper> configWrappers;

    public GuiModConfigs(String modId, List<? extends IConfigBase> configs, String titleKey, Object... args)
    {
        this(modId, ConfigOptionWrapper.createFor(configs), false, titleKey, args);
    }

    public GuiModConfigs(String modId, List<ConfigOptionWrapper> wrappers, boolean unused, String titleKey, Object... args)
    {
        super(10, 0, modId, null, ImmutableList.of(), titleKey, args);

        this.configWrappers = wrappers;
    }

    @Override
    protected int getBrowserHeight()
    {
        return this.height - 70;
    }

    @Override
    @Nullable
    public IConfigGuiTab getCurrentTab()
    {
        return null;
    }

    @Override
    public void setCurrentTab(IConfigGuiTab tab)
    {
        // NO-OP
    }

    @Override
    protected void createTabButtons()
    {
        // NO-OP
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        return this.configWrappers;
    }

    @Override
    protected void drawTitle(int mouseX, int mouseY, float partialTicks)
    {
        // NO-OP
    }
}
