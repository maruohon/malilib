package fi.dy.masa.malilib.gui.config;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.util.DialogHandler;
import fi.dy.masa.malilib.gui.widget.list.ConfigOptionListWidget;

public class ConfigWidgetContext
{
    @Nullable protected final KeybindEditingScreen keyBindScreen;
    protected final Supplier<ConfigOptionListWidget<? extends ConfigInfo>> listWidgetSupplier;
    protected final Supplier<DialogHandler> dialogSupplier;

    public ConfigWidgetContext(Supplier<ConfigOptionListWidget<? extends ConfigInfo>> listWidgetSupplier,
                               @Nullable KeybindEditingScreen keyBindScreen, Supplier<DialogHandler> dialogSupplier)
    {
        this.listWidgetSupplier = listWidgetSupplier;
        this.keyBindScreen = keyBindScreen;
        this.dialogSupplier = dialogSupplier;
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
}
