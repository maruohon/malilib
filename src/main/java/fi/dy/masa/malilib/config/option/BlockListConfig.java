package fi.dy.masa.malilib.config.option;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class BlockListConfig extends ValueListConfig<Block>
{
    public BlockListConfig(String name, ImmutableList<Block> defaultValues, Function<Block, String> toStringConverter, Function<String, Block> fromStringConverter)
    {
        super(name, defaultValues, toStringConverter, fromStringConverter);
    }

    public BlockListConfig(String name, ImmutableList<Block> defaultValues, String comment, Function<Block, String> toStringConverter, Function<String, Block> fromStringConverter)
    {
        super(name, defaultValues, comment, toStringConverter, fromStringConverter);
    }

    @Override
    public BlockListConfig copy()
    {
        return new BlockListConfig(this.name, this.defaultValues, this.commentTranslationKey, this.toStringConverter, this.fromStringConverter);
    }

    @Nullable
    public static Block nameToBlock(String name)
    {
        try
        {
            return Block.REGISTRY.getObject(new ResourceLocation(name));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static String blockToName(Block block)
    {
        try
        {
            return Block.REGISTRY.getNameForObject(block).toString();
        }
        catch (Exception e)
        {
            return "?";
        }
    }

    public static BlockListConfig create(String cfgName, String... blockNames)
    {
        return create(cfgName, Arrays.asList(blockNames));
    }

    public static BlockListConfig create(String cfgName, List<String> blockNames)
    {
        ImmutableList.Builder<Block> builder = ImmutableList.builder();

        for (String name : blockNames)
        {
            Block block = nameToBlock(name);

            if (block != null)
            {
                builder.add(block);
            }
        }

        return new BlockListConfig(cfgName, builder.build(), BlockListConfig::blockToName, BlockListConfig::nameToBlock);
    }
}
