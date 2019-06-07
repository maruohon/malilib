package fi.dy.masa.malilib.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.collect.UnmodifiableIterator;
import fi.dy.masa.malilib.gui.GuiBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public class BlockUtils
{
    /**
     * Returns the first PropertyDirection property from the provided state, if any.
     * @param state
     * @return the first PropertyDirection, or null if there are no such properties
     */
    @Nullable
    public static PropertyDirection getFirstDirectionProperty(IBlockState state)
    {
        for (IProperty<?> prop : state.getProperties().keySet())
        {
            if (prop instanceof PropertyDirection)
            {
                return (PropertyDirection) prop;
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
        PropertyDirection prop = getFirstDirectionProperty(state);
        return prop != null ? state.getValue(prop) : null;
    }

    public static List<String> getFormattedBlockStateProperties(IBlockState state)
    {
        return getFormattedBlockStateProperties(state, ": ");
    }

    public static List<String> getFormattedBlockStateProperties(IBlockState state, String separator)
    {
        if (state.getProperties().size() > 0)
        {
            List<String> lines = new ArrayList<>();
            UnmodifiableIterator<Map.Entry<IProperty<?>, Comparable<?>>> iter = state.getProperties().entrySet().iterator();

            while (iter.hasNext())
            {
                Map.Entry<IProperty<?>, Comparable<?>> entry = iter.next();
                IProperty<?> key = entry.getKey();
                Comparable<?> val = entry.getValue();

                if (key instanceof PropertyBool)
                {
                    String pre = val.equals(Boolean.TRUE) ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
                    lines.add(key.getName() + separator + pre + val.toString());
                }
                else if (key instanceof PropertyDirection)
                {
                    lines.add(key.getName() + separator + GuiBase.TXT_GOLD + val.toString());
                }
                else if (key instanceof PropertyInteger)
                {
                    lines.add(key.getName() + separator + GuiBase.TXT_AQUA + val.toString());
                }
                else
                {
                    lines.add(key.getName() + separator + val.toString());
                }
            }

            return lines;
        }

        return Collections.emptyList();
    }
}
