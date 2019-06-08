package fi.dy.masa.malilib.util.restrictions;

import java.util.List;
import java.util.Set;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class ItemRestriction extends UsageRestriction<Item>
{
    @Override
    protected void setValuesForList(Set<Item> set, List<String> names)
    {
        for (String name : names)
        {
            ResourceLocation rl = null;

            try
            {
                rl = new ResourceLocation(name);
            }
            catch (Exception e)
            {
            }

            Item item = rl != null ? IRegistry.ITEM.get(rl) : null;

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
