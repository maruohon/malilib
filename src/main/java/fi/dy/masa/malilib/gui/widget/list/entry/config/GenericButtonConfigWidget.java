package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.GenericButtonConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;

public class GenericButtonConfigWidget extends BaseConfigWidget<GenericButtonConfig>
{
    protected final GenericButton button;

    public GenericButtonConfigWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                     GenericButtonConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.button = GenericButton.simple(config.getButtonText(), config.getButtonActionListener());
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        int x = this.getElementsStartPosition();
        int y = this.getY() + 1;

        this.button.setPosition(x, y);

        this.addWidget(this.button);
    }
}
