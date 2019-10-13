package fi.dy.masa.malilib.gui.button;

import fi.dy.masa.malilib.config.options.IConfigBoolean;
import fi.dy.masa.malilib.gui.GuiBase;

public class ConfigButtonBoolean extends ButtonGeneric
{
    private final IConfigBoolean config;

    public ConfigButtonBoolean(int x, int y, int width, int height, IConfigBoolean config)
    {
        super(x, y, width, height, "");
        this.config = config;

        this.updateDisplayString();
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        this.config.toggleBooleanValue();
        this.updateDisplayString();

        return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }

    @Override
    protected String generateDisplayString()
    {
        String valueStr = String.valueOf(this.config.getBooleanValue());

        return (this.config.getBooleanValue() ? GuiBase.TXT_DARK_GREEN : GuiBase.TXT_DARK_RED) + valueStr + GuiBase.TXT_RST;
    }
}
