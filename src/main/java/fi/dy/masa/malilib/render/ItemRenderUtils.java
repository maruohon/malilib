package fi.dy.masa.malilib.render;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import fi.dy.masa.malilib.util.ItemUtils;
import fi.dy.masa.malilib.util.StringUtils;

public class ItemRenderUtils
{
    public static void renderStackAt(ItemStack stack, int x, int y, float z, float scale, Minecraft mc)
    {
        if (stack == null || ItemUtils.isEmpty(stack))
        {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);

        if (scale != 1f)
        {
            GlStateManager.scale(scale, scale, 1f);
        }

        GlStateManager.disableLighting();
        RenderUtils.enableGuiItemLighting();

        RenderItem itemRenderer = mc.getRenderItem();
        float oldZ = itemRenderer.zLevel;

        // Compensate for the extra z increments done in the RenderItem class.
        // The RenderItem essentially increases the z-level by 149.5, but if we
        // take all of that out, then the back side of the models already goes behind
        // the requested z-level.
        // -145 seems to work pretty well for things like boats where the issue occurs first,
        // but carpets actually need around -143 to not clip the back corner.
        itemRenderer.zLevel = z - 142f;
        itemRenderer.renderItemAndEffectIntoGUI(mc.player, stack, 0, 0);
        itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, stack, 0, 0, null);
        itemRenderer.zLevel = oldZ;

        //GlStateManager.disableBlend();
        RenderUtils.disableItemLighting();
        GlStateManager.popMatrix();
    }

    public static void renderStackToolTip(int x, int y, float zLevel, ItemStack stack, Minecraft mc)
    {
        if (stack == null || ItemUtils.isEmpty(stack))
        {
            return;
        }

        List<String> list = stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
            {
                list.set(i, stack.getRarity().color + list.get(i));
            }
            else
            {
                list.set(i, StringUtils.translate("malilib.hover.item_tooltip_lines", list.get(i)));
            }
        }

        TextRenderUtils.renderHoverText(x, y, zLevel, list);
    }
}
