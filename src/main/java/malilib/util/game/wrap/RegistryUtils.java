package malilib.util.game.wrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class RegistryUtils
{
    public static Block getBlockByIdStr(String name)
    {
        try
        {
            return getBlockById(new ResourceLocation(name));
        }
        catch (Exception e)
        {
            return Blocks.air;
        }
    }

    public static Block getBlockById(ResourceLocation id)
    {
        Block block = Block.blockRegistry.getObject(id);
        return block != null ? block : Blocks.air;
    }

    @Nullable
    public static ResourceLocation getBlockId(Block block)
    {
        return Block.blockRegistry.getNameForObject(block);
    }

    @Nullable
    public static ResourceLocation getBlockId(IBlockState state)
    {
        return getBlockId(state.getBlock());
    }

    public static String getBlockIdStr(Block block)
    {
        ResourceLocation id = getBlockId(block);
        return id != null ? id.toString() : "?";
    }

    public static String getBlockIdStr(IBlockState state)
    {
        return getBlockIdStr(state.getBlock());
    }

    public static Collection<ResourceLocation> getRegisteredBlockIds()
    {
        return Block.blockRegistry.getKeys();
    }

    public static List<Block> getSortedBlockList()
    {
        List<Block> blocks = new ArrayList<>();

        for (Block block : Block.blockRegistry)
        {
            blocks.add(block);
        }

        blocks.sort(Comparator.comparing(RegistryUtils::getBlockIdStr));

        return blocks;
    }

    @Nullable
    public static Item getItemByIdStr(String name)
    {
        try
        {
            return getItemById(new ResourceLocation(name));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Nullable
    public static Item getItemById(ResourceLocation id)
    {
        Item item = Item.itemRegistry.getObject(id);
        return item != null ? item : null;
    }

    @Nullable
    public static ResourceLocation getItemId(Item item)
    {
        return Item.itemRegistry.getNameForObject(item);
    }

    public static String getItemIdStr(Item item)
    {
        ResourceLocation id = getItemId(item);
        return id != null ? id.toString() : "?";
    }

    public static Collection<ResourceLocation> getRegisteredItemIds()
    {
        return Item.itemRegistry.getKeys();
    }

    public static List<Item> getSortedItemList()
    {
        List<Item> items = new ArrayList<>();

        for (Item item : Item.itemRegistry)
        {
            items.add(item);
        }

        items.sort(Comparator.comparing(RegistryUtils::getItemIdStr));

        return items;
    }
}
