package fi.dy.masa.malilib.util.restrictions;

import java.util.List;
import java.util.Set;
import fi.dy.masa.malilib.LiteModMaLiLib;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class BlockRestriction extends UsageRestriction<Block>
{
    @Override
    protected void setValuesForList(Set<Block> set, List<String> names)
    {
        for (String name : names)
        {
            Block block = Block.REGISTRY.getObject(new ResourceLocation(name));

            if (block != null)
            {
                set.add(block);
            }
            else
            {
                LiteModMaLiLib.logger.warn(StringUtils.translate("malilib.error.invalid_block_blacklist_entry", name));
            }
        }
    }
}
