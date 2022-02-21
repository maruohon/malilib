package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.util.ArrayList;
import java.util.List;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.button.OptionListConfigButton;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public class OptionListConfigWidget extends BaseConfigWidget<OptionListConfig<OptionListConfigValue>>
{
    protected final OptionListConfig<OptionListConfigValue> config;
    protected final OptionListConfigValue initialValue;
    protected final OptionListConfigButton optionListButton;
    protected final DropDownListWidget<OptionListConfigValue> dropDownWidget;

    public OptionListConfigWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                  OptionListConfig<OptionListConfigValue> config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);

        this.config = config;
        this.initialValue = this.config.getValue();

        this.optionListButton = new OptionListConfigButton(80, 20, this.config);
        this.optionListButton.setHoverStringProvider("locked", this.config::getLockAndOverrideMessages);
        this.optionListButton.setChangeListener(this::updateResetButtonState);

        ArrayList<OptionListConfigValue> values = new ArrayList<>(config.getAllowedValues());
        this.dropDownWidget = new DropDownListWidget<>(80, 16, 200, 20, values, OptionListConfigValue::getDisplayName);
        this.dropDownWidget.setSelectedEntry(config.getValue());
        this.dropDownWidget.getHoverInfoFactory()
                .setTextLineProvider("list_preview", this::getOptionListPreviewHoverString, 99);
        this.dropDownWidget.setHoverStringProvider("locked", this.config::getLockAndOverrideMessages);
        this.dropDownWidget.setSelectionListener((v) -> {
            this.config.setValue(v);
            this.dropDownWidget.updateHoverStrings();
            this.updateResetButtonState();
        });

        this.resetButton.setActionListener(this::reset);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        boolean useDropDown = MaLiLibConfigs.Generic.OPTION_LIST_CONFIG_DROPDOWN.getBooleanValue();

        this.dropDownWidget.setEnabled(this.config.isLocked() == false);
        this.optionListButton.setEnabled(this.config.isLocked() == false);

        this.addWidget(useDropDown ? this.dropDownWidget : this.optionListButton);
        this.addWidget(this.resetButton);
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int x = this.getElementsStartPosition();
        int y = this.getY();
        int elementWidth = this.getElementWidth();

        this.dropDownWidget.setWidth(elementWidth);
        this.optionListButton.setWidth(elementWidth);

        this.dropDownWidget.updateHoverStrings();
        this.optionListButton.updateHoverStrings();

        this.optionListButton.setPosition(x, y + 1);
        this.dropDownWidget.setPosition(x, y + 2);

        this.updateResetButton(x + elementWidth + 4, y + 1);
    }

    @Override
    public boolean wasModified()
    {
        return this.config.getValue().equals(this.initialValue) == false;
    }

    protected void reset()
    {
        this.config.resetToDefault();

        if (MaLiLibConfigs.Generic.OPTION_LIST_CONFIG_DROPDOWN.getBooleanValue())
        {
            this.dropDownWidget.setSelectedEntry(this.config.getValue());
        }
        else
        {
            this.optionListButton.updateButtonState();
        }

        this.updateResetButtonState();
    }

    protected List<StyledTextLine> getOptionListPreviewHoverString(List<StyledTextLine> previousLines)
    {
        return OptionListConfigButton.getOptionListPreviewHoverString(this.config, previousLines);
    }
}
