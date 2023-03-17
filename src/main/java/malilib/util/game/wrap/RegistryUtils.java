package malilib.util.game.wrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RegistryUtils
{
    public static Block getBlockByIdStr(String name)
    {
        try
        {
            return Registry.BLOCK.get(new Identifier(name));
        }
        catch (Exception e)
        {
            return Blocks.AIR;
        }
    }

    public static Block getBlockById(Identifier id)
    {
        Block block = Registry.BLOCK.get(id);
        return block != null ? block : Blocks.AIR;
    }

    @Nullable
    public static Identifier getBlockId(Block block)
    {
        return Registry.BLOCK.getId(block);
    }

    @Nullable
    public static Identifier getBlockId(BlockState state)
    {
        return Registry.BLOCK.getId(state.getBlock());
    }

    public static String getBlockIdStr(Block block)
    {
        Identifier id = Registry.BLOCK.getId(block);
        return id != null ? id.toString() : "?";
    }

    public static String getBlockIdStr(BlockState state)
    {
        Identifier id = Registry.BLOCK.getId(state.getBlock());
        return id != null ? id.toString() : "?";
    }

    public static Collection<Identifier> getRegisteredBlockIds()
    {
        return Registry.BLOCK.getIds();
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
            return Registry.ITEM.get(new Identifier(name));
        }
        catch (Exception e)
        {
            return Items.AIR;
        }
    }

    public static Item getItemById(Identifier id)
    {
        Item item = Registry.ITEM.get(id);
        return item != null ? item : Items.AIR;
    }

    @Nullable
    public static Identifier getItemId(Item item)
    {
        return Registry.ITEM.getId(item);
    }

    public static String getItemIdStr(Item item)
    {
        Identifier id = Registry.ITEM.getId(item);
        return id != null ? id.toString() : "?";
    }

    public static Collection<Identifier> getRegisteredItemIds()
    {
        return Registry.ITEM.getIds();
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
