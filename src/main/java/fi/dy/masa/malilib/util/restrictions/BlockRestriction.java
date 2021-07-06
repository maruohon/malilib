package fi.dy.masa.malilib.util.restrictions;

import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.util.StringUtils;

public class BlockRestriction extends UsageRestriction<Block>
{
    @Override
    protected void setValuesForList(Set<Block> set, List<String> names)
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

            Block block = rl != null ? Registry.BLOCK.get(rl) : null;

            if (block != null)
            {
                set.add(block);
            }
            else
            {
                MaLiLib.logger.warn(StringUtils.translate("malilib.error.invalid_block_blacklist_entry", name));
            }
        }
    }
}
