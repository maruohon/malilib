package fi.dy.masa.malilib.gui.config;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.config.liteloader.DialogHandler;
import fi.dy.masa.malilib.gui.widget.list.ConfigOptionListWidget;

public class ConfigWidgetContext
{
    protected final Supplier<ConfigOptionListWidget<? extends ConfigInfo>> listWidgetSupplier;
    @Nullable protected final KeybindEditingScreen keyBindScreen;
    protected final Supplier<DialogHandler> dialogSupplier;
    protected final int nestingLevel;

    public ConfigWidgetContext(Supplier<ConfigOptionListWidget<? extends ConfigInfo>> listWidgetSupplier,
                               @Nullable KeybindEditingScreen keyBindScreen,
                               Supplier<DialogHandler> dialogSupplier,
                               int nestingLevel)
    {
        this.listWidgetSupplier = listWidgetSupplier;
        this.keyBindScreen = keyBindScreen;
        this.dialogSupplier = dialogSupplier;
        this.nestingLevel = nestingLevel;
    }

    public ConfigOptionListWidget<? extends ConfigInfo> getListWidget()
    {
        return this.listWidgetSupplier.get();
    }

    @Nullable
    public DialogHandler getDialogHandler()
    {
        return this.dialogSupplier.get();
    }

    @Nullable
    public KeybindEditingScreen getKeybindEditingScreen()
    {
        return this.keyBindScreen;
    }

    public int getNestingLevel()
    {
        return this.nestingLevel;
    }

    public ConfigWidgetContext withNestingLevel(int nestingLevel)
    {
        return new ConfigWidgetContext(this.listWidgetSupplier, this.keyBindScreen, this.dialogSupplier, nestingLevel);
    }
}
