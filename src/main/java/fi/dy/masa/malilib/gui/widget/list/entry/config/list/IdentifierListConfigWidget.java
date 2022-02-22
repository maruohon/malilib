package fi.dy.masa.malilib.gui.widget.list.entry.config.list;

import java.util.ArrayList;
import java.util.Comparator;
import com.google.common.collect.ImmutableSet;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.config.option.list.IdentifierListConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.BaseValueListEditButton;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseValueListEditEntryWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.gui.widget.list.entry.IdentifierListEditEntryWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class IdentifierListConfigWidget extends BaseValueListConfigWidget<ResourceLocation, IdentifierListConfig>
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
        String title = StringUtils.translate("malilib.title.screen.identifier_list_edit", this.config.getDisplayName());
        ImmutableSet<ResourceLocation> validValues = this.config.getValidValues();

        if (validValues != null && validValues.isEmpty() == false)
        {
            final ResourceLocation entry = validValues.stream().findFirst().orElse(new ResourceLocation("minecraft:foo"));
            final ArrayList<ResourceLocation> possibleValues = new ArrayList<>(validValues);
            possibleValues.sort(Comparator.comparing(ResourceLocation::toString));

            return new BaseValueListEditButton<>(width, height, config, this::updateWidgetDisplayValues, () -> entry,
                                                 (iv, cd, dv) -> new BaseValueListEditEntryWidget<>(iv, cd, dv,
                                                    possibleValues, ResourceLocation::toString, null), title);
        }
        else
        {
            return new BaseValueListEditButton<>(width, height, config, this::updateWidgetDisplayValues,
                                                 () -> new ResourceLocation("minecraft:foo"),
                                                 IdentifierListEditEntryWidget::new, title);
        }
    }
}
