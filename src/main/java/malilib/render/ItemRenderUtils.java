package malilib.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;

import malilib.render.text.StyledTextLine;
import malilib.util.game.wrap.GameWrap;
import malilib.util.game.wrap.ItemWrap;
import malilib.util.game.wrap.RenderWrap;

public class ItemRenderUtils
{
    public static void renderStackAt(ItemStack stack, int x, int y, float z, float scale, RenderContext ctx)
    {
        if (stack == null || ItemWrap.isEmpty(stack))
        {
            return;
        }

        RenderWrap.pushMatrix(ctx);
        RenderWrap.translate(x, y, 0, ctx);

        if (scale != 1f)
        {
            RenderWrap.scale(scale, scale, 1f, ctx);
        }

        RenderWrap.disableLighting();
        RenderWrap.enableGuiItemLighting(ctx);

        Minecraft mc = GameWrap.getClient();
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

        //RenderWrap.disableBlend();
        RenderWrap.disableItemLighting();
        RenderWrap.popMatrix(ctx);
    }

    public static void renderStackToolTip(int x, int y, float zLevel, ItemStack stack, RenderContext ctx)
    {
        if (stack == null || ItemWrap.isEmpty(stack))
        {
            return;
        }

        List<String> list = stack.getTooltip(GameWrap.getClientPlayer(), GameWrap.getOptions().advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
        List<StyledTextLine> textLines = new ArrayList<>();

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
            {
                StyledTextLine.parseLines(textLines, stack.getRarity().color + list.get(i));
            }
            else
            {
                StyledTextLine.translate(textLines, "malilib.hover.item_tooltip_lines", list.get(i));
            }
        }

        TextRenderUtils.renderStyledHoverText(x, y, zLevel, textLines, ctx);
    }
}
