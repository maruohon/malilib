package fi.dy.masa.malilib.gui.widget.list.entry.config.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.inventory.EntityEquipmentSlot;
import fi.dy.masa.malilib.config.option.list.EquipmentSlotListConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.button.BaseValueListEditButton;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.util.StringUtils;

public class EquipmentSlotListConfigWidget extends BaseValueListConfigWidget<EntityEquipmentSlot, EquipmentSlotListConfig>
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
                                             () -> EntityEquipmentSlot.MAINHAND,
                                             this::getSortedSlotList,
                                             EntityEquipmentSlot::getName,
                                             null,
                                             title);
    }

    public List<EntityEquipmentSlot> getSortedSlotList()
    {
        return getSortedSlotList(this.config.getValidValues());
    }

    public static List<EntityEquipmentSlot> getSortedSlotList(@Nullable Set<EntityEquipmentSlot> validValues)
    {
        List<EntityEquipmentSlot> slots = new ArrayList<>();

        slots.add(EntityEquipmentSlot.MAINHAND);
        slots.add(EntityEquipmentSlot.OFFHAND);
        slots.add(EntityEquipmentSlot.HEAD);
        slots.add(EntityEquipmentSlot.CHEST);
        slots.add(EntityEquipmentSlot.LEGS);
        slots.add(EntityEquipmentSlot.FEET);

        if (validValues == null)
        {
            return slots;
        }

        return slots.stream().filter(validValues::contains).collect(Collectors.toList());
    }
}
