package malilib.config.option.list;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EquipmentSlotListConfig extends ValueListConfig<EntityEquipmentSlot>
{
    public EquipmentSlotListConfig(String name, ImmutableList<EntityEquipmentSlot> defaultValues)
    {
        this(name, defaultValues, EntityEquipmentSlot::getName, EquipmentSlotListConfig::fromString);
    }

    public EquipmentSlotListConfig(String name, ImmutableList<EntityEquipmentSlot> defaultValues,
                                   Function<EntityEquipmentSlot, String> toStringConverter,
                                   Function<String, EntityEquipmentSlot> fromStringConverter)
    {
        super(name, defaultValues, toStringConverter, fromStringConverter);
    }

    public EquipmentSlotListConfig(String name, ImmutableList<EntityEquipmentSlot> defaultValues,
                                   Function<EntityEquipmentSlot, String> toStringConverter,
                                   Function<String, EntityEquipmentSlot> fromStringConverter,
                                   String commentTranslationKey, Object... commentArgs)
    {
        super(name, defaultValues, toStringConverter, fromStringConverter, commentTranslationKey, commentArgs);
    }

    @Override
    public EquipmentSlotListConfig copy()
    {
        EquipmentSlotListConfig config = new EquipmentSlotListConfig(this.name, this.defaultValue, this.toStringConverter, this.fromStringConverter);
        config.copyValuesFrom(this);
        return config;
    }

    public static EquipmentSlotListConfig fromNames(String cfgName, String... itemNames)
    {
        return fromNames(cfgName, Arrays.asList(itemNames));
    }

    public static EquipmentSlotListConfig fromNames(String cfgName, List<String> itemNames)
    {
        ImmutableList.Builder<EntityEquipmentSlot> builder = ImmutableList.builder();

        for (String name : itemNames)
        {
            EntityEquipmentSlot slot = fromString(name);

            if (slot != null)
            {
                builder.add(slot);
            }
        }

        return create(cfgName, builder.build());
    }

    public static EquipmentSlotListConfig create(String cfgName, ImmutableList<EntityEquipmentSlot> slots)
    {
        return new EquipmentSlotListConfig(cfgName, slots);
    }

    public static EquipmentSlotListConfig create(String cfgName, ImmutableList<EntityEquipmentSlot> slots, List<EntityEquipmentSlot> validSlots)
    {
        EquipmentSlotListConfig config = new EquipmentSlotListConfig(cfgName, slots);
        config.setValidValues(validSlots);
        return config;
    }

    @Nullable
    public static EntityEquipmentSlot fromString(String name)
    {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values())
        {
            if (slot.getName().equals(name))
            {
                return slot;
            }
        }

        return null;
    }
}
