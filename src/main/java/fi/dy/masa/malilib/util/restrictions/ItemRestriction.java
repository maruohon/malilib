package fi.dy.masa.malilib.util.restrictions;

import java.util.List;
import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
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
            catch (Exception e)
            {
            }

            Item item = rl != null ? Registry.ITEM.get(rl) : null;

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
