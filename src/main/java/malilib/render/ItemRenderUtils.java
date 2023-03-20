package malilib.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import malilib.util.StringUtils;
import malilib.util.game.wrap.GameUtils;
import malilib.util.game.wrap.ItemWrap;

public class ItemRenderUtils
{
    public static void renderStackAt(ItemStack stack, int x, int y, float z, float scale, RenderContext ctx)
    {
        /* TODO 1.13+ port
        if (stack == null || ItemWrap.isEmpty(stack))
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

        MinecraftClient mc = GameUtils.getClient();
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
        */
    }

    public static void renderStackToolTip(int x, int y, float zLevel, ItemStack stack, RenderContext ctx)
    {
        if (stack == null || ItemWrap.isEmpty(stack))
        {
            return;
        }

        List<Component> list = stack.getTooltipLines(GameUtils.getClientPlayer(), GameUtils.getOptions().advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
            {
                //lines.add(stack.getRarity().getStyleModifier().apply(Style.EMPTY).getInsertion() + list.get(i).getString());
                lines.add(stack.getRarity().color + list.get(i).getString());
            }
            else
            {
                lines.add(StringUtils.translate("malilib.hover.item_tooltip_lines", list.get(i).getString()));
            }
        }

        TextRenderUtils.renderHoverText(x, y, zLevel, lines, ctx);
    }
}
