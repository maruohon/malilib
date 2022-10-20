package malilib.gui.widget.list.entry.config.list;

import java.util.ArrayList;
import java.util.Comparator;
import com.google.common.collect.ImmutableSet;
import malilib.config.option.list.IdentifierListConfig;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.button.BaseValueListEditButton;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.entry.BaseValueListEditEntryWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.gui.widget.list.entry.IdentifierListEditEntryWidget;
import malilib.util.StringUtils;
import net.minecraft.util.Identifier;

public class IdentifierListConfigWidget extends BaseValueListConfigWidget<Identifier, IdentifierListConfig>
{
    public IdentifierListConfigWidget(IdentifierListConfig config,
                                      DataListEntryWidgetData constructData,
                                      ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);
    }

    @Override
    protected GenericButton createButton(int width, int height, IdentifierListConfig config, ConfigWidgetContext ctx)
    {
        String title = StringUtils.translate("malilibdev.title.screen.identifier_list_edit", this.config.getDisplayName());
        ImmutableSet<Identifier> validValues = this.config.getValidValues();

        if (validValues != null && validValues.isEmpty() == false)
        {
            final Identifier entry = validValues.stream().findFirst().orElse(new Identifier("minecraft:foo"));
            final ArrayList<Identifier> possibleValues = new ArrayList<>(validValues);
            possibleValues.sort(Comparator.comparing(Identifier::toString));

            return new BaseValueListEditButton<>(width, height, config, this::updateWidgetState, () -> entry,
                                                 (iv, cd, dv) -> new BaseValueListEditEntryWidget<>(iv, cd, dv,
                                                                                                    possibleValues, Identifier::toString, null), title);
        }
        else
        {
            return new BaseValueListEditButton<>(width, height, config, this::updateWidgetState,
                                                 () -> new Identifier("minecraft:foo"),
                                                 IdentifierListEditEntryWidget::new, title);
        }
    }
}
