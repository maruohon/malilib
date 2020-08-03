package fi.dy.masa.malilib.gui.button;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.StringListConfig;
import fi.dy.masa.malilib.gui.interfaces.IConfigGui;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigButtonStringList extends ButtonGeneric
{
    private final StringListConfig config;
    private final IConfigGui configGui;
    @Nullable private final IDialogHandler dialogHandler;

    public ConfigButtonStringList(int x, int y, int width, int height, StringListConfig config, IConfigGui configGui, @Nullable IDialogHandler dialogHandler)
    {
        super(x, y, width, height, "");

        this.config = config;
        this.configGui = configGui;
        this.dialogHandler = dialogHandler;

        this.updateDisplayString();
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        super.onMouseClickedImpl(mouseX, mouseY, mouseButton);

        // TODO config refactor
        /*
        if (this.dialogHandler != null)
        {
            this.dialogHandler.openDialog(new StringListEditScreen(this.config, this.configGui, this.dialogHandler, null));
        }
        else
        {
            BaseScreen.openPopupGui(new StringListEditScreen(this.config, this.configGui, null, GuiUtils.getCurrentScreen()));
        }
        */

        return true;
    }

    @Override
    protected String generateDisplayString()
    {
        return StringUtils.getClampedDisplayStringRenderlen(this.config.getStrings(), this.getWidth() - 10, "[ ", " ]");
    }
}
