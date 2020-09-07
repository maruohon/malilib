package fi.dy.masa.malilib.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.item.Item;
import fi.dy.masa.malilib.gui.widget.ItemStackWidget;

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

    public static List<Item> getSortedItemList()
    {
        List<Item> items = new ArrayList<>();

        for (Item item : Item.REGISTRY)
        {
            items.add(item);
        }

        items.sort(Comparator.comparing(ItemStackWidget::getItemDisplayName));

        return items;
    }
}
