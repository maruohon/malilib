package fi.dy.masa.malilib.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.util.data.Constants;

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

    /**
     * Parses the provided string into a compound tag representing the block state.<br>
     * The tag is in the format that the vanilla util class uses for reading/writing states to NBT
     * data, for example in the Chunk block state palette.<br>
     * The string should be in either one of the following formats:<br>
     * 'minecraft:stone' or 'minecraft:smooth_stone_slab[half=top,waterlogged=false]'.<br>
     * None of the values are checked for validity here, and this can be used for
     * parsing strings for states from another Minecraft version, such as 1.12 <-> 1.13+.
     * @param stateString
     * @return
     */
    public static NBTTagCompound getBlockStateTagFromString(String stateString)
    {
        int index = stateString.indexOf("["); // [f=b]
        String blockName = index != -1 ? stateString.substring(0, index) : stateString;
        NBTTagCompound tag = new NBTTagCompound();

        tag.setString("Name", blockName);

        if (index != -1 && stateString.length() > (index + 4) && stateString.charAt(stateString.length() - 1) == ']')
        {
            NBTTagCompound propsTag = new NBTTagCompound();
            String propStr = stateString.substring(index + 1, stateString.length() - 1);
            Iterator<String> propIter = COMMA_SPLITTER.split(propStr).iterator();

            while (propIter.hasNext())
            {
                String propAndVal = propIter.next();
                Iterator<String> valIter = EQUAL_SPLITTER.split(propAndVal).iterator();

                if (valIter.hasNext() == false)
                {
                    continue;
                }

                String propName = valIter.next();

                if (valIter.hasNext() == false)
                {
                    continue;
                }

                String valStr = valIter.next();

                propsTag.setString(propName, valStr);
            }

            tag.setTag("Properties", propsTag);
        }

        return tag;
    }

    /**
     * Parses the input tag representing a block state, and produces a string
     * in the same format as the toString() method in the vanilla block state.
     * This string format is what the Sponge schematic format uses in the palette.
     * @param stateTag
     * @return an equivalent of IBlockState.toString() of the given tag representing a block state
     */
    public static String getBlockStateStringFromTag(NBTTagCompound stateTag)
    {
        String name = stateTag.getString("Name");

        if (stateTag.hasKey("Properties", Constants.NBT.TAG_COMPOUND) == false)
        {
            return name;
        }

        NBTTagCompound propTag = stateTag.getCompoundTag("Properties");
        ArrayList<Pair<String, String>> props = new ArrayList<>();

        for (String key : propTag.getKeySet())
        {
            props.add(Pair.of(key, propTag.getString(key)));
        }

        final int size = props.size();

        if (size > 0)
        {
            props.sort(Comparator.comparing(Pair::getLeft));

            StringBuilder sb = new StringBuilder();
            sb.append(name).append('[');
            Pair<String, String> pair = props.get(0);

            sb.append(pair.getLeft()).append('=').append(pair.getRight());

            for (int i = 1; i < size; ++i)
            {
                pair = props.get(i);
                sb.append(',').append(pair.getLeft()).append('=').append(pair.getRight());
            }

            sb.append(']');

            return sb.toString();
        }

        return name;
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

    public static <T extends Comparable<T>> List<String> getFormattedBlockStateProperties(IBlockState state, String separator)
    {
        if (state.getProperties().size() > 0)
        {
            List<String> lines = new ArrayList<>();

            try
            {
                ImmutableMap<IProperty<?>, Comparable<?>> properties = state.getProperties();

                for (Map.Entry<IProperty<?>, Comparable<?>> entry : properties.entrySet())
                {
                    @SuppressWarnings("unchecked")
                    IProperty<T> prop = (IProperty<T>) entry.getKey();
                    @SuppressWarnings("unchecked")
                    Comparable<T> val = (Comparable<T>) entry.getValue();

                    String propName = prop.getName();
                    @SuppressWarnings("unchecked")
                    String valStr = prop.getName((T) val);

                    if (prop instanceof PropertyBool)
                    {
                        String pre = val.equals(Boolean.TRUE) ? BaseScreen.TXT_GREEN : BaseScreen.TXT_RED;
                        lines.add(propName + separator + pre + valStr);
                    }
                    else if (prop instanceof PropertyDirection)
                    {
                        lines.add(propName + separator + BaseScreen.TXT_GOLD + valStr);
                    }
                    else if (prop instanceof PropertyInteger)
                    {
                        lines.add(propName + separator + BaseScreen.TXT_AQUA + valStr);
                    }
                    else
                    {
                        lines.add(propName + separator + valStr);
                    }
                }
            }
            catch (Exception ignore) {}

            return lines;
        }

        return Collections.emptyList();
    }
}
