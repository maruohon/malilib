package fi.dy.masa.malilib.gui.widget.list.entry;

import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ConfigOnTab;

public class ConfigInfoEntryWidget extends BaseDataListEntryWidget<ConfigOnTab>
{
    public ConfigInfoEntryWidget(int x, int y, int width, int height, int listIndex, int originalListIndex,
                                 ConfigOnTab data,
                                 DataListWidget<ConfigOnTab> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        ConfigInfo config = data.config;
        String hoverText = StringUtils.translate("malilib.gui.hover.config_info_entry_widget",
                                                 config.getDisplayName(), config.getName(),
                                                 config.getClass().getSimpleName());
        this.addHoverStrings(hoverText);

        this.setText(StyledTextLine.of(config.getDisplayName()));

        int bgColor = this.isOdd ? 0x30FFFFFF : 0x20FFFFFF;
        this.setBackgroundColor(bgColor);
        this.setRenderBackground(true);
    }
}
