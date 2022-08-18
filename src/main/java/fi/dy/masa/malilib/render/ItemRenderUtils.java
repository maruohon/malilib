package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.game.wrap.GameUtils;
import fi.dy.masa.malilib.util.game.wrap.ItemWrap;

public class ItemRenderUtils
{
    public static void renderStackAt(ItemStack stack, int x, int y, float z, float scale, MinecraftClient mc)
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

    public static void renderStackToolTip(int x, int y, float zLevel,
                                          ItemStack stack, RenderContext ctx)
    {
        if (stack == null || ItemWrap.isEmpty(stack))
        {
            return;
        }

        MinecraftClient mc = GameUtils.getClient();
        List<Text> list = stack.getTooltip(mc.player, mc.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
            {
                lines.add(stack.getRarity().formatting + list.get(i).getString());
            }
            else
            {
                lines.add(StringUtils.translate("malilib.hover.item_tooltip_lines", list.get(i).getString()));
            }
        }

        TextRenderUtils.renderHoverText(x, y, zLevel, lines, ctx);
    }
}
