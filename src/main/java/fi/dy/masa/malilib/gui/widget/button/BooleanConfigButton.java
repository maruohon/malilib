package fi.dy.masa.malilib.gui.widget.button;

import fi.dy.masa.malilib.util.data.BooleanStorage;

public class BooleanConfigButton extends OnOffButton
{
    public BooleanConfigButton(int width, int height, BooleanStorage config)
    {
        this(width, height, config, OnOffStyle.SLIDER_ON_OFF);
    }

    public BooleanConfigButton(int width, int height, BooleanStorage config, OnOffStyle style)
    {
        super(width, height, style, config::getBooleanValue, null);

        this.setActionListener(config::toggleBooleanValue);
    }
}
