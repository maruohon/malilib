package malilib.gui.widget.list.entry.config.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.minecraft.entity.EquipmentSlot;

import malilib.config.option.list.EquipmentSlotListConfig;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.button.BaseValueListEditButton;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.util.StringUtils;

public class EquipmentSlotListConfigWidget extends BaseValueListConfigWidget<EquipmentSlot, EquipmentSlotListConfig>
{
    public EquipmentSlotListConfigWidget(EquipmentSlotListConfig config,
                                         DataListEntryWidgetData constructData,
                                         ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);
    }

    @Override
    protected GenericButton createButton(int width, int height, EquipmentSlotListConfig config, ConfigWidgetContext ctx)
    {
        String title = StringUtils.translate("malilib.title.screen.equipment_slot_list_edit", this.config.getDisplayName());

        return new BaseValueListEditButton<>(width, height,
                                             config,
                                             this::updateWidgetState,
                                             () -> EquipmentSlot.MAINHAND,
                                             this::getSortedSlotList,
                                             EquipmentSlot::getName,
                                             null,
                                             title);
    }

    public List<EquipmentSlot> getSortedSlotList()
    {
        return getSortedSlotList(this.config.getValidValues());
    }

    public static List<EquipmentSlot> getSortedSlotList(@Nullable Set<EquipmentSlot> validValues)
    {
        List<EquipmentSlot> slots = new ArrayList<>();

        slots.add(EquipmentSlot.MAINHAND);
        slots.add(EquipmentSlot.OFFHAND);
        slots.add(EquipmentSlot.HEAD);
        slots.add(EquipmentSlot.CHEST);
        slots.add(EquipmentSlot.LEGS);
        slots.add(EquipmentSlot.FEET);

        if (validValues == null)
        {
            return slots;
        }

        return slots.stream().filter(validValues::contains).collect(Collectors.toList());
    }
}
