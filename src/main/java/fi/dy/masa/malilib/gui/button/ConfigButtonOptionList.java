package fi.dy.masa.malilib.gui.button;

import fi.dy.masa.malilib.config.IConfigOptionList;
import net.minecraft.client.MinecraftClient;

public class ConfigButtonOptionList extends ButtonGeneric
{
    private final IConfigOptionList config;

    public ConfigButtonOptionList(int id, int x, int y, int width, int height, IConfigOptionList config)
    {
        super(id, x, y, width, height, "");
        this.config = config;

        this.updateDisplayString();
    }

    @Override
    public void onMouseButtonClicked(int mouseButton)
    {
        this.config.setOptionListValue(this.config.getOptionListValue().cycle(mouseButton == 0));
        this.updateDisplayString();
        this.playPressedSound(MinecraftClient.getInstance().getSoundLoader());
    }

    @Override
    public void updateDisplayString()
    {
        this.text = String.valueOf(this.config.getOptionListValue().getDisplayName());
    }
}
