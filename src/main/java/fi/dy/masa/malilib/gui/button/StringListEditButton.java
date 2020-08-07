package fi.dy.masa.malilib.gui.button;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.StringListConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.StringListEditScreen;
import fi.dy.masa.malilib.gui.interfaces.IConfigGui;
import fi.dy.masa.malilib.gui.interfaces.IDialogHandler;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;

public class StringListEditButton extends ButtonGeneric
{
    private final StringListConfig config;
    private final IConfigGui configGui;
    @Nullable private final IDialogHandler dialogHandler;
    @Nullable protected final EventListener saveListener;

    public StringListEditButton(int x, int y, int width, int height,
                                StringListConfig config, IConfigGui configGui, @Nullable EventListener saveListener)
    {
        super(x, y, width, height, "");

        this.config = config;
        this.configGui = configGui;
        this.dialogHandler = configGui.getDialogHandler();
        this.saveListener = saveListener;

        this.updateDisplayString();
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        super.onMouseClickedImpl(mouseX, mouseY, mouseButton);

        if (this.dialogHandler != null)
        {
            this.dialogHandler.openDialog(new StringListEditScreen(this.config, this.configGui, this.dialogHandler, null, this.saveListener));
        }
        else
        {
            BaseScreen.openPopupGui(new StringListEditScreen(this.config, this.configGui, null, GuiUtils.getCurrentScreen(), this.saveListener));
        }

        return true;
    }

    @Override
    protected String generateDisplayString()
    {
        return StringUtils.getDisplayStringForList(this.config.getStrings(), this.getWidth() - 10, "'", "[ ", " ]");
    }
}
