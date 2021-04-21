package fi.dy.masa.malilib.gui.widget.list.entry.config.list;

import fi.dy.masa.malilib.config.option.list.StringListConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.BaseValueListEditButton;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.StringListEditEntryWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class StringListConfigWidget extends BaseValueListConfigWidget<String, StringListConfig>
{
    public StringListConfigWidget(int x, int y, int width, int height, int listIndex,
                                  int originalListIndex, StringListConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);
    }

    @Override
    protected GenericButton createButton(int width, int height, StringListConfig config, ConfigWidgetContext ctx)
    {
        String title = StringUtils.translate("malilib.gui.title.string_list_edit", this.config.getDisplayName());

        return new BaseValueListEditButton<>(0, 0, width, height, config, this::updateButtonStates, ctx.getDialogHandler(),
                                             title, () -> "", StringListEditEntryWidget::new);
    }
}
