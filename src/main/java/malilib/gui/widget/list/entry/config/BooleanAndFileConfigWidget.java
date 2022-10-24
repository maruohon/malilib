package malilib.gui.widget.list.entry.config;

import java.nio.file.Path;

import malilib.config.option.BooleanAndFileConfig;
import malilib.config.option.BooleanAndFileConfig.BooleanAndFile;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.button.OnOffButton;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;

public class BooleanAndFileConfigWidget extends BaseFileConfigWidget<BooleanAndFile, BooleanAndFileConfig>
{
    protected final OnOffButton booleanButton;

    public BooleanAndFileConfigWidget(BooleanAndFileConfig config,
                                      DataListEntryWidgetData constructData,
                                      ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.booleanButton = OnOffButton.simpleSlider(20, () -> this.config.getValue().booleanValue, this::toggleBooleanValue);
        this.booleanButton.setEnabledStatusSupplier(() -> this.config.isLocked() == false);
        this.booleanButton.getHoverInfoFactory().setStringListProvider("locked", config::getLockAndOverrideMessages, 101);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.booleanButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getElementsStartPosition();
        int elementWidth = this.getElementWidth();

        this.booleanButton.setX(x);
        this.booleanButton.centerVerticallyInside(this);
        this.openBrowserButton.setX(this.booleanButton.getRight() + 2);
        this.openBrowserButton.setWidth(elementWidth - this.booleanButton.getWidth() - 2);
    }

    @Override
    protected Path getFileFromConfig()
    {
        return this.config.getValue().fileValue;
    }

    @Override
    protected void setFileToConfig(Path file)
    {
        BooleanAndFile oldValue = this.config.getValue();
        BooleanAndFile newValue = new BooleanAndFile(oldValue.booleanValue, file);
        this.config.setValue(newValue);
    }

    protected void toggleBooleanValue()
    {
        BooleanAndFile oldValue = this.config.getValue();
        BooleanAndFile newValue = new BooleanAndFile(! oldValue.booleanValue, oldValue.fileValue);
        this.config.setValue(newValue);
    }
}
