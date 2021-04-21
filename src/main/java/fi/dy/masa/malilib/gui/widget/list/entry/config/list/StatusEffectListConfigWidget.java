package fi.dy.masa.malilib.gui.widget.list.entry.config.list;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import fi.dy.masa.malilib.config.option.list.StatusEffectListConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.BaseValueListEditButton;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.util.StringUtils;

public class StatusEffectListConfigWidget extends BaseValueListConfigWidget<Potion, StatusEffectListConfig>
{
    public StatusEffectListConfigWidget(int x, int y, int width, int height, int listIndex,
                                int originalListIndex, StatusEffectListConfig config, ConfigWidgetContext ctx)
    {
        super(x, y, width, height, listIndex, originalListIndex, config, ctx);
    }

    @Override
    protected GenericButton createButton(int width, int height, StatusEffectListConfig config, ConfigWidgetContext ctx)
    {
        String title = StringUtils.translate("malilib.gui.title.status_effect_list_edit", this.config.getDisplayName());

        return new BaseValueListEditButton<>(0, 0, width, height, config, this::updateButtonStates, ctx.getDialogHandler(),
                                             title, () -> MobEffects.REGENERATION,
                                             StatusEffectListConfigWidget::getSortedEffectList,
                                             StatusEffectListConfig::getRegistryName,
                                             null);
    }

    public static List<Potion> getSortedEffectList()
    {
        List<Potion> effects = new ArrayList<>();

        for (Potion effect : Potion.REGISTRY)
        {
            effects.add(effect);
        }

        effects.sort(Comparator.comparing(StatusEffectListConfig::getRegistryName));

        return effects;
    }
}
