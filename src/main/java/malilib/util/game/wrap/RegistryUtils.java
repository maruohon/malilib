package malilib.util.game.wrap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import malilib.util.StringUtils;
import malilib.util.game.BlockUtils;
import malilib.util.game.ItemUtils;

public class RegistryUtils
{
    @Nullable
    public static Block getBlockByIdStr(String name)
    {
        OptionalInt opt = StringUtils.tryParseToInt(name);

        if (opt.isPresent())
        {
            int id = opt.getAsInt();

            if (id < Block.BY_ID.length)
            {
                return Block.BY_ID[id];
            }
        }

        return null;
    }

    /* TODO b1.7.3
    public static Block getBlockById(ResourceLocation id)
    {
        Block block = Block.REGISTRY.getObject(id);
        return block != null ? block : Blocks.AIR;
    }

    @Nullable
    public static ResourceLocation getBlockId(Block block)
    {
        return Block.REGISTRY.getNameForObject(block);
    }

    @Nullable
    public static ResourceLocation getBlockId(IBlockState state)
    {
        return Block.REGISTRY.getNameForObject(state.getBlock());
    }

    public static String getBlockIdStr(Block block)
    {
        ResourceLocation id = Block.REGISTRY.getNameForObject(block);
        return id != null ? id.toString() : "?";
    }

    public static String getBlockIdStr(IBlockState state)
    {
        ResourceLocation id = Block.REGISTRY.getNameForObject(state.getBlock());
        return id != null ? id.toString() : "?";
    }

    public static Collection<ResourceLocation> getRegisteredBlockIds()
    {
        return Block.REGISTRY.getKeys();
    }
    */

    public static List<Block> getSortedBlockList()
    {
        List<Block> blocks = new ArrayList<>();

        for (Block block : Block.BY_ID)
        {
            if (block != null)
            {
                blocks.add(block);
            }
        }

        blocks.sort(Comparator.comparing(BlockUtils::getDisplayNameForBlock));

        return blocks;
    }

    @Nullable
    public static Item getItemByIdStr(String name)
    {
        OptionalInt opt = StringUtils.tryParseToInt(name);

        if (opt.isPresent())
        {
            int id = opt.getAsInt();

            if (id < Item.BY_ID.length)
            {
                return Item.BY_ID[id];
            }
        }

        return null;
    }

    /* TODO b1.7.3
    public static Item getItemById(ResourceLocation id)
    {
        Item item = Item.REGISTRY.getObject(id);
        return item != null ? item : Items.AIR;
    }

    @Nullable
    public static ResourceLocation getItemId(Item item)
    {
        return Item.REGISTRY.getNameForObject(item);
    }
    */

    public static String getItemIdStr(Item item)
    {
        return String.valueOf(item.id);
    }

    /*
    public static Collection<ResourceLocation> getRegisteredItemIds()
    {
        return Item.REGISTRY.getKeys();
    }
    */

    public static List<Item> getSortedItemList()
    {
        List<Item> items = new ArrayList<>();

        for (Item item : Item.BY_ID)
        {
            if (item != null)
            {
                items.add(item);
            }
        }

        items.sort(Comparator.comparing(ItemUtils::getDisplayNameForItem));

        return items;
    }
}
