package fi.dy.masa.malilib.gui.widget.list.entry.config;

import fi.dy.masa.malilib.config.option.GenericButtonConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class GenericButtonConfigWidget extends BaseConfigWidget<GenericButtonConfig>
{
    protected final GenericButton button;

    public GenericButtonConfigWidget(GenericButtonConfig config,
                                     DataListEntryWidgetData constructData,
                                     ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.button = GenericButton.create(config.getButtonTextTranslationKey());
        this.button.setActionListener(config.getButtonActionListener());
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();
        this.addWidget(this.button);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();
        this.button.setPosition(this.getElementsStartPosition(), this.getY() + 1);
    }
}
