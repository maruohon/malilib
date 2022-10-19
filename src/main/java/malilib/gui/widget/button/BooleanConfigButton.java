package malilib.gui.widget.button;

import javax.annotation.Nullable;
import malilib.util.data.BooleanStorage;

public class BooleanConfigButton extends OnOffButton
{
    public BooleanConfigButton(int width, int height, BooleanStorage config)
    {
        this(width, height, config, OnOffStyle.SLIDER_ON_OFF, null);
    }

    public BooleanConfigButton(int width, int height, BooleanStorage config, OnOffStyle style, @Nullable String translationKey)
    {
        super(width, height, style, config::getBooleanValue, translationKey);

        this.setActionListener(config::toggleBooleanValue);
    }
}
