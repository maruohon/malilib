package fi.dy.masa.malilib.gui.button;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.options.IConfigOptionList;
import fi.dy.masa.malilib.util.StringUtils;

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
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        this.config.setOptionListValue(this.config.getOptionListValue().cycle(mouseButton == 0));
        this.updateDisplayString();

        return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }

    @Override
    protected String generateDisplayString()
    {
        if (this.prefixTranslationKey != null)
        {
            return StringUtils.translate(this.prefixTranslationKey, this.config.getOptionListValue().getDisplayName());
        }
        else
        {
            return this.config.getOptionListValue().getDisplayName();
        }
    }
}
