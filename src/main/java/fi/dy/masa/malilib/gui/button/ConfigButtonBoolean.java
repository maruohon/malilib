package fi.dy.masa.malilib.gui.button;

import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.gui.GuiBase;
import net.minecraft.client.MinecraftClient;

public class ConfigButtonBoolean extends ButtonGeneric
{
    private final IConfigBoolean config;

    public ConfigButtonBoolean(int id, int x, int y, int width, int height, IConfigBoolean config)
    {
        super(id, x, y, width, height, "");
        this.config = config;

        this.updateDisplayString();
    }

    @Override
    public void onMouseButtonClicked(int mouseButton)
    {
        this.config.setBooleanValue(! this.config.getBooleanValue());
        this.updateDisplayString();
        this.playDownSound(MinecraftClient.getInstance().getSoundManager());
    }

    @Override
    public void updateDisplayString()
    {
        String valueStr = String.valueOf(this.config.getBooleanValue());

        if (this.config.getBooleanValue())
        {
            this.setMessage(GuiBase.TXT_DARK_GREEN + valueStr + GuiBase.TXT_RST);
        }
        else
        {
            this.setMessage(GuiBase.TXT_DARK_RED + valueStr + GuiBase.TXT_RST);
        }
    }
}
