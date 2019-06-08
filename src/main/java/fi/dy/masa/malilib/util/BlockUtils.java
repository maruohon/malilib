package fi.dy.masa.malilib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.GuiBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.EnumFacing;

public class BlockUtils
{
    /**
     * Returns the first PropertyDirection property from the provided state, if any.
     * @param state
     * @return the first PropertyDirection, or null if there are no such properties
     */
    @Nullable
    public static DirectionProperty getFirstDirectionProperty(IBlockState state)
    {
        for (IProperty<?> prop : state.getProperties())
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
    public static EnumFacing getFirstPropertyFacingValue(IBlockState state)
    {
        DirectionProperty prop = getFirstDirectionProperty(state);
        return prop != null ? state.get(prop) : null;
    }

    public static List<String> getFormattedBlockStateProperties(IBlockState state)
    {
        return getFormattedBlockStateProperties(state, ": ");
    }

    public static List<String> getFormattedBlockStateProperties(IBlockState state, String separator)
    {
        Collection<IProperty<?>> properties = state.getProperties();

        if (properties.size() > 0)
        {
            List<String> lines = new ArrayList<>();

            for (IProperty<?> prop : properties)
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
                else if (prop instanceof IntegerProperty)
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
