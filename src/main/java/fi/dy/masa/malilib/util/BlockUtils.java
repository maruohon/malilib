package fi.dy.masa.malilib.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.base.Splitter;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.gui.GuiBase;

public class BlockUtils
{
    private static final Splitter COMMA_SPLITTER = Splitter.on(',');
    private static final Splitter EQUAL_SPLITTER = Splitter.on('=').limit(2);

    /**
     * Parses the provided string into the full block state.<br>
     * The string should be in either one of the following formats:<br>
     * 'minecraft:stone' or 'minecraft:smooth_stone_slab[half=top,waterlogged=false]'
     * @param str
     * @return
     */
    @Nullable
    public static IBlockState getBlockStateFromString(String str)
    {
        int index = str.indexOf("["); // [f=b]
        String blockName = index != -1 ? str.substring(0, index) : str;
        ResourceLocation id = new ResourceLocation(blockName);

        if (Block.REGISTRY.containsKey(id))
        {
            Block block = Block.REGISTRY.getObject(id);
            IBlockState state = block.getDefaultState();

            if (index != -1 && str.length() > (index + 4) && str.charAt(str.length() - 1) == ']')
            {
                BlockStateContainer blockState = block.getBlockState();
                String propStr = str.substring(index + 1, str.length() - 1);
                Iterator<String> propIter = COMMA_SPLITTER.split(propStr).iterator();

                while (propIter.hasNext())
                {
                    String propAndVal = propIter.next();
                    Iterator<String> valIter = EQUAL_SPLITTER.split(propAndVal).iterator();

                    if (valIter.hasNext() == false)
                    {
                        continue;
                    }

                    IProperty<?> prop = blockState.getProperty(valIter.next());

                    if (prop == null || valIter.hasNext() == false)
                    {
                        continue;
                    }

                    Comparable<?> val = getPropertyValueByName(prop, valIter.next());

                    if (val != null)
                    {
                        state = getBlockStateWithProperty(state, prop, val);
                    }
                }
            }

            return state;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> IBlockState getBlockStateWithProperty(IBlockState state, IProperty<T> prop, Comparable<?> value)
    {
        return state.withProperty(prop, (T) value);
    }

    @Nullable
    public static <T extends Comparable<T>> T getPropertyValueByName(IProperty<T> prop, String valStr)
    {
        return (T) (prop.parseValue(valStr).orNull());
    }

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
