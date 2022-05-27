package fi.dy.masa.malilib.util.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemUtils
{
    public static String getItemRegistryName(Item item)
    {
        try
        {
            return Item.REGISTRY.getNameForObject(item).toString();
        }
        catch (Exception e)
        {
            return "?";
        }
    }

    @Nullable
    public static Item getItemByRegistryName(String name)
    {
        try
        {
            return Item.REGISTRY.getObject(new ResourceLocation(name));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static List<Item> getSortedItemList()
    {
        List<Item> items = new ArrayList<>();

        for (Item item : Item.REGISTRY)
        {
            items.add(item);
        }

        items.sort(Comparator.comparing(ItemUtils::getItemRegistryName));

        return items;
    }
}
