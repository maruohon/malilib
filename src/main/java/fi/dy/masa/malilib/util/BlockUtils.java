package fi.dy.masa.malilib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import fi.dy.masa.malilib.gui.GuiBase;

public class BlockUtils
{
    /**
     * Returns the first PropertyDirection property from the provided state, if any.
     * @param state
     * @return the first PropertyDirection, or null if there are no such properties
     */
    @Nullable
    public static DirectionProperty getFirstDirectionProperty(BlockState state)
    {
        for (Property<?> prop : state.getProperties())
        {
            if (prop instanceof DirectionProperty)
            {
                return (DirectionProperty) prop;
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
        DirectionProperty prop = getFirstDirectionProperty(state);
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
                else if (prop instanceof DirectionProperty)
                {
                    lines.add(prop.getName() + separator + GuiBase.TXT_GOLD + val.toString());
                }
                else if (prop instanceof IntProperty)
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
