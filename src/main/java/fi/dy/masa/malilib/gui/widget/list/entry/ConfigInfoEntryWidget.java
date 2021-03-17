package fi.dy.masa.malilib.gui.widget.list.entry;

import java.util.Collections;
import java.util.List;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class ConfigInfoEntryWidget extends BaseDataListEntryWidget<ConfigOnTab>
{
    protected final LabelWidget nameLabelWidget;

    public ConfigInfoEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                 ConfigOnTab data,
                                 DataListWidget<ConfigOnTab> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        ConfigInfo config = data.config;
        String hoverText = StringUtils.translate("malilib.gui.hover.config_info_entry_widget",
                                                 config.getDisplayName(), config.getName());
        List<String> hoverLines = Collections.singletonList(hoverText);
        this.setHoverStrings(hoverLines);

        this.nameLabelWidget = new LabelWidget(0, 0, -1, -1, 0xFFFFFFFF, config.getDisplayName());
        this.nameLabelWidget.setHoverStrings(hoverLines);

        int bgColor = this.isOdd ? 0x30FFFFFF : 0x20FFFFFF;
        this.setBackgroundColor(bgColor);
        this.setBackgroundEnabled(true);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.nameLabelWidget);
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int y = this.getY() + this.getHeight() / 2 - this.nameLabelWidget.getHeight() / 2;
        this.nameLabelWidget.setPosition(this.getX() + 4, y);
    }
}
