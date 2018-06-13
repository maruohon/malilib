package fi.dy.masa.malilib.gui.button;

import fi.dy.masa.malilib.config.IConfigBoolean;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

public class ConfigButtonBoolean extends ButtonBase
{
    private final IConfigBoolean config;

    public ConfigButtonBoolean(int id, int x, int y, int width, int height, IConfigBoolean config)
    {
        super(id, x, y, width, height);
        this.config = config;

        this.updateDisplayString();
    }

    @Override
    public void onMouseButtonClicked(int mouseButton)
    {
        this.config.setBooleanValue(! this.config.getBooleanValue());
        this.updateDisplayString();
        this.playPressSound(Minecraft.getMinecraft().getSoundHandler());
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
