package fi.dy.masa.malilib.gui.button;

import fi.dy.masa.malilib.config.IConfigBoolean;
import net.minecraft.util.text.TextFormatting;

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
    public void onMouseButtonClicked(int mouseButton)
    {
        this.config.setBooleanValue(! this.config.getBooleanValue());
        this.updateDisplayString();

        super.onMouseButtonClicked(mouseButton);
    }

    @Override
    public void updateDisplayString()
    {
        String valueStr = String.valueOf(this.config.getBooleanValue());

        if (this.config.getBooleanValue())
        {
            this.displayString = TextFormatting.DARK_GREEN + valueStr + TextFormatting.RESET;
        }
        else
        {
            this.displayString = TextFormatting.DARK_RED + valueStr + TextFormatting.RESET;
        }
    }
}
