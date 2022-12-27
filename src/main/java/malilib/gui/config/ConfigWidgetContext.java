package malilib.gui.config;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import malilib.config.option.ConfigInfo;
import malilib.gui.widget.list.ConfigOptionListWidget;

public class ConfigWidgetContext
{
    protected final Supplier<ConfigOptionListWidget<? extends ConfigInfo>> listWidgetSupplier;
    @Nullable protected final KeybindEditScreen keyBindScreen;
    protected final int nestingLevel;

    public ConfigWidgetContext(Supplier<ConfigOptionListWidget<? extends ConfigInfo>> listWidgetSupplier,
                               @Nullable KeybindEditScreen keyBindScreen,
                               int nestingLevel)
    {
        this.listWidgetSupplier = listWidgetSupplier;
        this.keyBindScreen = keyBindScreen;
        this.nestingLevel = nestingLevel;
    }

    public ConfigOptionListWidget<? extends ConfigInfo> getListWidget()
    {
        return this.listWidgetSupplier.get();
    }

    @Nullable
    public KeybindEditScreen getKeybindEditingScreen()
    {
        return this.keyBindScreen;
    }

    public int getNestingLevel()
    {
        return this.nestingLevel;
    }

    public ConfigWidgetContext withNestingLevel(int nestingLevel)
    {
        return new ConfigWidgetContext(this.listWidgetSupplier, this.keyBindScreen, nestingLevel);
    }
}
