package fi.dy.masa.malilib.util.restrictions;

import java.util.List;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.StringUtils;

public class ItemRestriction extends UsageRestriction<Item>
{
    @Override
    protected void setValuesForList(Set<Item> set, List<String> names)
    {
        for (String name : names)
        {
            Identifier rl = null;

            try
            {
                rl = new Identifier(name);
            }
            catch (Exception ignore) {}

            Item item = rl != null ? Registries.ITEM.get(rl) : null;

            if (item != null)
            {
                set.add(item);
            }
            else
            {
                MaLiLib.logger.warn(StringUtils.translate("malilib.error.invalid_item_blacklist_entry", name));
            }
        }
    }
}
