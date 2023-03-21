package malilib.util.game.wrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class RegistryUtils
{
    public static Block getBlockByIdStr(String name)
    {
        try
        {
            return Registry.BLOCK.get(new ResourceLocation(name));
        }
        catch (Exception e)
        {
            return Blocks.AIR;
        }
    }

    public static Block getBlockById(ResourceLocation id)
    {
        Block block = Registry.BLOCK.get(id);
        return block != null ? block : Blocks.AIR;
    }

    @Nullable
    public static ResourceLocation getBlockId(Block block)
    {
        return Registry.BLOCK.getKey(block);
    }

    @Nullable
    public static ResourceLocation getBlockId(BlockState state)
    {
        return Registry.BLOCK.getKey(state.getBlock());
    }

    public static String getBlockIdStr(Block block)
    {
        ResourceLocation id = Registry.BLOCK.getKey(block);
        return id != null ? id.toString() : "?";
    }

    public static String getBlockIdStr(BlockState state)
    {
        ResourceLocation id = Registry.BLOCK.getKey(state.getBlock());
        return id != null ? id.toString() : "?";
    }

    public static Collection<ResourceLocation> getRegisteredBlockIds()
    {
        return Registry.BLOCK.keySet();
    }

    public static List<Block> getSortedBlockList()
    {
        List<Block> blocks = new ArrayList<>();

        for (Block block : Registry.BLOCK)
        {
            blocks.add(block);
        }

        blocks.sort(Comparator.comparing(RegistryUtils::getBlockIdStr));

        return blocks;
    }

    public static Item getItemByIdStr(String name)
    {
        try
        {
            return Registry.ITEM.get(new ResourceLocation(name));
        }
        catch (Exception e)
        {
            return Items.AIR;
        }
    }

    public static Item getItemById(ResourceLocation id)
    {
        Item item = Registry.ITEM.get(id);
        return item != null ? item : Items.AIR;
    }

    @Nullable
    public static ResourceLocation getItemId(Item item)
    {
        return Registry.ITEM.getKey(item);
    }

    public static String getItemIdStr(Item item)
    {
        ResourceLocation id = Registry.ITEM.getKey(item);
        return id != null ? id.toString() : "?";
    }

    public static Collection<ResourceLocation> getRegisteredItemIds()
    {
        return Registry.ITEM.keySet();
    }

    public static List<Item> getSortedItemList()
    {
        List<Item> items = new ArrayList<>();

        for (Item item : Registry.ITEM)
        {
            items.add(item);
        }

        items.sort(Comparator.comparing(RegistryUtils::getItemIdStr));

        return items;
    }
}
