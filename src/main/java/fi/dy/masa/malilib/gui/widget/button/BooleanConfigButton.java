package fi.dy.masa.malilib.gui.widget.button;

import fi.dy.masa.malilib.config.option.BooleanConfig;

public class BooleanConfigButton extends OnOffButton
{
    public BooleanConfigButton(int x, int y, int width, int height, BooleanConfig config)
    {
        this(x, y, width, height, config, OnOffStyle.SLIDER_ON_OFF);
    }

    public BooleanConfigButton(int x, int y, int width, int height, BooleanConfig config, OnOffStyle style)
    {
        super(x, y, width, height, style, config::getBooleanValue, null);

        this.setActionListener((btn, mbtn) -> {
            config.toggleBooleanValue();
            this.updateDisplayString();
        });
    }
}
