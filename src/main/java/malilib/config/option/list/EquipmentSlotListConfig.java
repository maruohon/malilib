package malilib.config.option.list;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EquipmentSlot;

public class EquipmentSlotListConfig extends ValueListConfig<EquipmentSlot>
{
    public static final ImmutableList<EquipmentSlot> SLOTS = ImmutableList.copyOf(EquipmentSlot.values());

    public EquipmentSlotListConfig(String name, ImmutableList<EquipmentSlot> defaultValues)
    {
        this(name, defaultValues, EquipmentSlot::getName, EquipmentSlotListConfig::fromString);
    }

    public EquipmentSlotListConfig(String name, ImmutableList<EquipmentSlot> defaultValues,
                                   Function<EquipmentSlot, String> toStringConverter,
                                   Function<String, EquipmentSlot> fromStringConverter)
    {
        super(name, defaultValues, toStringConverter, fromStringConverter);
    }

    public EquipmentSlotListConfig(String name, ImmutableList<EquipmentSlot> defaultValues,
                                   Function<EquipmentSlot, String> toStringConverter,
                                   Function<String, EquipmentSlot> fromStringConverter,
                                   @Nullable String commentTranslationKey, Object... commentArgs)
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
        ImmutableList.Builder<EquipmentSlot> builder = ImmutableList.builder();

        for (String name : itemNames)
        {
            EquipmentSlot slot = fromString(name);

            if (slot != null)
            {
                builder.add(slot);
            }
        }

        return create(cfgName, builder.build());
    }

    public static EquipmentSlotListConfig create(String cfgName, ImmutableList<EquipmentSlot> slots)
    {
        return new EquipmentSlotListConfig(cfgName, slots);
    }

    public static EquipmentSlotListConfig create(String cfgName, ImmutableList<EquipmentSlot> slots, List<EquipmentSlot> validSlots)
    {
        EquipmentSlotListConfig config = new EquipmentSlotListConfig(cfgName, slots);
        config.setValidValues(validSlots);
        return config;
    }

    @Nullable
    public static EquipmentSlot fromString(String name)
    {
        for (EquipmentSlot slot : SLOTS)
        {
            if (slot.getName().equals(name))
            {
                return slot;
            }
        }

        return null;
    }
}
