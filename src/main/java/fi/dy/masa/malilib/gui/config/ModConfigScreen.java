package fi.dy.masa.malilib.gui.config;

import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.widget.ScreenContext;
import fi.dy.masa.malilib.util.data.ModInfo;

public class ModConfigScreen extends BaseConfigScreen
{
    protected final List<? extends ConfigInfo> configs;

    public ModConfigScreen(ModInfo modInfo, List<? extends ConfigInfo> configs, String titleKey, Object... args)
    {
        super(modInfo, null, ImmutableList.of(), null, titleKey, args);

        this.configs = configs;
    }

    @Override
    protected int getListHeight()
    {
        return this.height - 70;
    }

    @Override
    public List<? extends ConfigInfo> getConfigs()
    {
        return this.configs;
    }

    @Override
    protected void renderScreenTitle(int mouseX, int mouseY, ScreenContext ctx)
    {
        // NO-OP
    }
}
