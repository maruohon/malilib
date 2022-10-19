package malilib.gui.widget.list.entry.config.list;

import malilib.config.option.list.StringListConfig;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.button.BaseValueListEditButton;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.gui.widget.list.entry.StringListEditEntryWidget;
import malilib.util.StringUtils;

public class StringListConfigWidget extends BaseValueListConfigWidget<String, StringListConfig>
{
    public StringListConfigWidget(StringListConfig config,
                                  DataListEntryWidgetData constructData,
                                  ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);
    }

    @Override
    protected GenericButton createButton(int width, int height, StringListConfig config, ConfigWidgetContext ctx)
    {
        String title = StringUtils.translate("malilib.title.screen.string_list_edit", this.config.getDisplayName());

        return new BaseValueListEditButton<>(width, height, config, this::updateWidgetState,
                                             () -> "", StringListEditEntryWidget::new, title);
    }
}
