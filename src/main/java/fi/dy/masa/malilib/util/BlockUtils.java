package fi.dy.masa.malilib.util;

import java.util.*;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import fi.dy.masa.malilib.gui.GuiBase;

public class BlockUtils
{

    static final Property<?>[] directionPropertiesList =
            {
                    Properties.FACING,
                    Properties.HOPPER_FACING,
                    Properties.HORIZONTAL_FACING,
                    Properties.VERTICAL_DIRECTION
            };

    /**
     * Returns the first PropertyDirection property from the provided state, if any.
     * @param state
     * @return the first PropertyDirection, or null if there are no such properties
     */
    @Nullable
    public static EnumProperty<Direction> getFirstDirectionProperty(BlockState state)
    {


        for (Property<?> prop : directionPropertiesList) {
            if (state.contains(prop))
            {
                return (EnumProperty<Direction>) prop;
            }
        }

        return null;
    }

    /**
     * Returns the EnumFacing value of the first found PropertyDirection
     * type blockstate property in the given state, if any.
     * If there are no PropertyDirection properties, then null is returned.
     * @param state
     * @return
     */
    @Nullable
    public static Direction getFirstPropertyFacingValue(BlockState state)
    {
        EnumProperty<Direction> prop = getFirstDirectionProperty(state);
        return prop != null ? state.get(prop) : null;
    }

    public static List<String> getFormattedBlockStateProperties(BlockState state)
    {
        return getFormattedBlockStateProperties(state, ": ");
    }

    public static List<String> getFormattedBlockStateProperties(BlockState state, String separator)
    {
        Collection<Property<?>> properties = state.getProperties();

        if (properties.size() > 0)
        {
            List<String> lines = new ArrayList<>();

            for (Property<?> prop : properties)
            {
                Comparable<?> val = state.get(prop);

                if (prop instanceof BooleanProperty)
                {
                    String pre = val.equals(Boolean.TRUE) ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
                    lines.add(prop.getName() + separator + pre + val.toString());
                }
                else
                {
                    for (Property<?> directionProp : directionPropertiesList) {
                        if (directionProp.getType() == prop.getType()) {
                            lines.add(prop.getName() + separator + GuiBase.TXT_GOLD + val.toString());
                            break;
                        }
                    }
                }
                if (prop instanceof IntProperty)
                {
                    lines.add(prop.getName() + separator + GuiBase.TXT_AQUA + val.toString());
                }
                else
                {
                    lines.add(prop.getName() + separator + val.toString());
                }
            }

            return lines;
        }

        return Collections.emptyList();
    }
}
