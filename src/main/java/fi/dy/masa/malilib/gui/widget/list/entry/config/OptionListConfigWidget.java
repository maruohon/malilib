package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.util.ArrayList;
import java.util.List;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.button.OptionListConfigButton;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public class OptionListConfigWidget extends BaseConfigWidget<OptionListConfig<OptionListConfigValue>>
{
    protected final OptionListConfig<OptionListConfigValue> config;
    protected final OptionListConfigValue initialValue;
    protected final OptionListConfigButton optionListButton;
    protected final DropDownListWidget<OptionListConfigValue> dropDownWidget;

    public OptionListConfigWidget(OptionListConfig<OptionListConfigValue> config,
                                  DataListEntryWidgetData constructData,
                                  ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.config = config;
        this.initialValue = this.config.getValue();

        this.optionListButton = new OptionListConfigButton(80, 20, this.config);
        this.optionListButton.setHoverStringProvider("locked", this.config::getLockAndOverrideMessages);
        this.optionListButton.setChangeListener(this::updateWidgetState);

        ArrayList<OptionListConfigValue> values = new ArrayList<>(config.getAllowedValues());
        this.dropDownWidget = new DropDownListWidget<>(80, 16, 200, 20, values, OptionListConfigValue::getDisplayName);
        this.dropDownWidget.setSelectedEntry(config.getValue());
        this.dropDownWidget.getHoverInfoFactory()
                .setTextLineProvider("list_preview", this::getOptionListPreviewHoverString, 99);
        this.dropDownWidget.setHoverStringProvider("locked", this.config::getLockAndOverrideMessages);
        this.dropDownWidget.setSelectionListener((v) -> {
            this.config.setValue(v);
            this.dropDownWidget.updateHoverStrings();
            this.updateWidgetState();
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        boolean useDropDown = MaLiLibConfigs.Generic.OPTION_LIST_CONFIG_USE_DROPDOWN.getBooleanValue();

        this.addWidget(useDropDown ? this.dropDownWidget : this.optionListButton);
        this.addWidget(this.resetButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getElementsStartPosition();
        int y = this.getY();
        int elementWidth = this.getElementWidth();

        this.dropDownWidget.setWidth(elementWidth);
        this.optionListButton.setWidth(elementWidth);

        this.optionListButton.setPosition(x, y + 1);
        this.dropDownWidget.setPosition(x, y + 2);

        this.resetButton.setPosition(x + elementWidth + 4, y + 1);
    }

    @Override
    public void updateWidgetState()
    {
        super.updateWidgetState();

        this.dropDownWidget.setEnabled(this.config.isLocked() == false);
        this.dropDownWidget.updateHoverStrings();
        this.optionListButton.setEnabled(this.config.isLocked() == false);
        this.optionListButton.updateButtonState();
    }

    @Override
    protected void onResetButtonClicked()
    {
        super.onResetButtonClicked();
        this.dropDownWidget.setSelectedEntry(this.config.getValue());
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getValue().equals(this.initialValue) == false;
    }

    protected List<StyledTextLine> getOptionListPreviewHoverString(List<StyledTextLine> previousLines)
    {
        return OptionListConfigButton.getOptionListPreviewHoverString(this.config, previousLines);
    }
}
