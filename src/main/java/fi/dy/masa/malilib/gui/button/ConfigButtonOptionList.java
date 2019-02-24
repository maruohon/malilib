package fi.dy.masa.malilib.gui.button;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.IConfigOptionList;
import net.minecraft.client.resources.I18n;

public class ConfigButtonOptionList extends ButtonGeneric
{
    private final IConfigOptionList config;
    @Nullable private final String prefixTranslationKey;

    public ConfigButtonOptionList(int x, int y, int width, int height, IConfigOptionList config)
    {
        this(x, y, width, height, config, null);
    }

    public ConfigButtonOptionList(int x, int y, int width, int height, IConfigOptionList config, @Nullable String prefixTranslationKey)
    {
        super(x, y, width, height, "");
        this.config = config;
        this.prefixTranslationKey = prefixTranslationKey;

        this.updateDisplayString();
    }

    @Override
    public void onMouseButtonClicked(int mouseButton)
    {
        this.config.setOptionListValue(this.config.getOptionListValue().cycle(mouseButton == 0));
        this.updateDisplayString();

        super.onMouseButtonClicked(mouseButton);
    }

    @Override
    public void updateDisplayString()
    {
        if (this.prefixTranslationKey != null)
        {
            this.displayString = I18n.format(this.prefixTranslationKey, this.config.getOptionListValue().getDisplayName());
        }
        else
        {
            this.displayString = this.config.getOptionListValue().getDisplayName();
        }
    }
}
