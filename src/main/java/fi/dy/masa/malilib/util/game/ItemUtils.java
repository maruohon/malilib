package fi.dy.masa.malilib.util.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemUtils
{
    public static String getItemRegistryName(Item item)
    {
        try
        {
            return Registry.ITEM.getId(item).toString();
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
            return Registry.ITEM.get(new Identifier(name));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static List<Item> getSortedItemList()
    {
        List<Item> items = new ArrayList<>();

        for (Item item : Registry.ITEM)
        {
            items.add(item);
        }

        items.sort(Comparator.comparing(ItemUtils::getItemRegistryName));

        return items;
    }
}
