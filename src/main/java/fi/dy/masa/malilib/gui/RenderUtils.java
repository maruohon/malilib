package fi.dy.masa.malilib.gui;

import java.util.Collection;
import java.util.List;
import org.lwjgl.opengl.GL11;
import fi.dy.masa.malilib.config.HudAlignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;

public class RenderUtils
{
    public static final ResourceLocation TEXTURE_BREWING_STAND = new ResourceLocation("textures/gui/container/brewing_stand.png");
    public static final ResourceLocation TEXTURE_DISPENSER = new ResourceLocation("textures/gui/container/dispenser.png");
    public static final ResourceLocation TEXTURE_DOUBLE_CHEST = new ResourceLocation("textures/gui/container/generic_54.png");
    public static final ResourceLocation TEXTURE_FURNACE = new ResourceLocation("textures/gui/container/furnace.png");
    public static final ResourceLocation TEXTURE_HOPPER = new ResourceLocation("textures/gui/container/hopper.png");
    public static final ResourceLocation TEXTURE_PLAYER_INV = new ResourceLocation("textures/gui/container/hopper.png");
    public static final ResourceLocation TEXTURE_SINGLE_CHEST = new ResourceLocation("textures/gui/container/shulker_box.png");
    public static final ResourceLocation TEXTURE_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    //private static final Vec3d LIGHT0_POS = (new Vec3d( 0.2D, 1.0D, -0.7D)).normalize();
    //private static final Vec3d LIGHT1_POS = (new Vec3d(-0.2D, 1.0D,  0.7D)).normalize();

    public static void drawOutlinedBox(int x, int y, int width, int height, int colorBg, int colorBorder)
    {
        // Draw the background
        Gui.drawRect(x, y, x + width, y + height, colorBg);

        // Draw the border
        drawOutline(x, y, width, height, colorBorder);
    }

    public static void drawOutline(int x, int y, int width, int height, int colorBorder)
    {
        int right = x + width;
        int bottom = y + height;

        Gui.drawRect(x - 1,  y - 1,         x, bottom + 1, colorBorder); // left edge
        Gui.drawRect(right,  y - 1, right + 1, bottom + 1, colorBorder); // right edge
        Gui.drawRect(    x,  y - 1,     right,          y, colorBorder); // top edge
        Gui.drawRect(    x, bottom,     right, bottom + 1, colorBorder); // bottom edge
    }

    public static void drawOutline(int x, int y, int width, int height, int borderWidth, int colorBorder)
    {
        int right = x + width;
        int bottom = y + height;

        Gui.drawRect(x - borderWidth, y - borderWidth, x                  , bottom + borderWidth, colorBorder); // left edge
        Gui.drawRect(right          , y - borderWidth, right + borderWidth, bottom + borderWidth, colorBorder); // right edge
        Gui.drawRect(    x          , y - borderWidth, right              , y                   , colorBorder); // top edge
        Gui.drawRect(    x          , bottom         , right              , bottom + borderWidth, colorBorder); // bottom edge
    }

    public static void drawTexturedRect(int x, int y, int u, int v, int width, int height, float zLevel)
    {
        float pixelWidth = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        buffer.pos(x        , y + height, zLevel).tex( u          * pixelWidth, (v + height) * pixelWidth).endVertex();
        buffer.pos(x + width, y + height, zLevel).tex((u + width) * pixelWidth, (v + height) * pixelWidth).endVertex();
        buffer.pos(x + width, y         , zLevel).tex((u + width) * pixelWidth,  v           * pixelWidth).endVertex();
        buffer.pos(x        , y         , zLevel).tex( u          * pixelWidth,  v           * pixelWidth).endVertex();

        tessellator.draw();
    }

    public static void drawHoverText(int x, int y, List<String> textLines)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (textLines.isEmpty() == false && mc.currentScreen != null)
        {
            FontRenderer font = mc.fontRenderer;
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int maxLineLength = 0;
            int maxWidth = mc.currentScreen.width;
            int maxHeight = mc.currentScreen.height;

            for (String s : textLines)
            {
                int length = font.getStringWidth(s);

                if (length > maxLineLength)
                {
                    maxLineLength = length;
                }
            }

            int textStartX = x + 12;
            int textStartY = y - 12;
            int textHeight = 8;

            if (textLines.size() > 1)
            {
                textHeight += 2 + (textLines.size() - 1) * 10;
            }

            if (textStartX + maxLineLength > maxWidth)
            {
                textStartX -= 28 + maxLineLength;
            }

            if (textStartY + textHeight + 6 > maxHeight)
            {
                textStartY = maxHeight - textHeight - 6;
            }

            double zLevel = 300;
            int borderColor = 0xF0100010;
            drawGradientRect(textStartX - 3, textStartY - 4, textStartX + maxLineLength + 3, textStartY - 3, zLevel, borderColor, borderColor);
            drawGradientRect(textStartX - 3, textStartY + textHeight + 3, textStartX + maxLineLength + 3, textStartY + textHeight + 4, zLevel, borderColor, borderColor);
            drawGradientRect(textStartX - 3, textStartY - 3, textStartX + maxLineLength + 3, textStartY + textHeight + 3, zLevel, borderColor, borderColor);
            drawGradientRect(textStartX - 4, textStartY - 3, textStartX - 3, textStartY + textHeight + 3, zLevel, borderColor, borderColor);
            drawGradientRect(textStartX + maxLineLength + 3, textStartY - 3, textStartX + maxLineLength + 4, textStartY + textHeight + 3, zLevel, borderColor, borderColor);

            int fillColor1 = 0x505000FF;
            int fillColor2 = 0x5028007F;
            drawGradientRect(textStartX - 3, textStartY - 3 + 1, textStartX - 3 + 1, textStartY + textHeight + 3 - 1, zLevel, fillColor1, fillColor2);
            drawGradientRect(textStartX + maxLineLength + 2, textStartY - 3 + 1, textStartX + maxLineLength + 3, textStartY + textHeight + 3 - 1, zLevel, fillColor1, fillColor2);
            drawGradientRect(textStartX - 3, textStartY - 3, textStartX + maxLineLength + 3, textStartY - 3 + 1, zLevel, fillColor1, fillColor1);
            drawGradientRect(textStartX - 3, textStartY + textHeight + 2, textStartX + maxLineLength + 3, textStartY + textHeight + 3, zLevel, fillColor2, fillColor2);

            for (int i = 0; i < textLines.size(); ++i)
            {
                String str = textLines.get(i);
                font.drawStringWithShadow(str, textStartX, textStartY, 0xFFFFFFFF);

                if (i == 0)
                {
                    textStartY += 2;
                }

                textStartY += 10;
            }

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, double zLevel, int startColor, int endColor)
    {
        float sa = (float)(startColor >> 24 & 0xFF) / 255.0F;
        float sr = (float)(startColor >> 16 & 0xFF) / 255.0F;
        float sg = (float)(startColor >>  8 & 0xFF) / 255.0F;
        float sb = (float)(startColor & 0xFF) / 255.0F;

        float ea = (float)(endColor >> 24 & 0xFF) / 255.0F;
        float er = (float)(endColor >> 16 & 0xFF) / 255.0F;
        float eg = (float)(endColor >>  8 & 0xFF) / 255.0F;
        float eb = (float)(endColor & 0xFF) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        bufferbuilder.pos(right, top,    zLevel).color(sr, sg, sb, sa).endVertex();
        bufferbuilder.pos(left,  top,    zLevel).color(sr, sg, sb, sa).endVertex();
        bufferbuilder.pos(left,  bottom, zLevel).color(er, eg, eb, ea).endVertex();
        bufferbuilder.pos(right, bottom, zLevel).color(er, eg, eb, ea).endVertex();

        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void renderInventoryBackground(int x, int y, int slotsPerRow, int totalSlots, IInventory inv, Minecraft mc)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (inv instanceof TileEntityFurnace)
        {
            mc.getTextureManager().bindTexture(TEXTURE_FURNACE);
            mc.ingameGUI.drawTexturedModalRect(x, y      , 0,   0, 176,  80);
            mc.ingameGUI.drawTexturedModalRect(x, y +  80, 0, 163, 176,   3);
        }
        else if (inv instanceof TileEntityBrewingStand)
        {
            mc.getTextureManager().bindTexture(TEXTURE_BREWING_STAND);
            mc.ingameGUI.drawTexturedModalRect(x, y      , 0,   0, 176,  80);
            mc.ingameGUI.drawTexturedModalRect(x, y +  80, 0, 163, 176,   3);
        }
        else if (totalSlots <= 5)
        {
            mc.getTextureManager().bindTexture(TEXTURE_HOPPER);
            mc.ingameGUI.drawTexturedModalRect(x, y      , 0,   0, 176,  50);
            mc.ingameGUI.drawTexturedModalRect(x, y +  50, 0, 127, 176,   6);
        }
        else if (totalSlots <= 9)
        {
            mc.getTextureManager().bindTexture(TEXTURE_DISPENSER);
            mc.ingameGUI.drawTexturedModalRect(x, y      , 0,   0, 176,  83);
            mc.ingameGUI.drawTexturedModalRect(x, y +  83, 0, 163, 176,   3);
        }
        else if (totalSlots <= 27)
        {
            mc.getTextureManager().bindTexture(TEXTURE_SINGLE_CHEST);
            mc.ingameGUI.drawTexturedModalRect(x, y      , 0,   0, 176,  83);
            mc.ingameGUI.drawTexturedModalRect(x, y +  83, 0, 161, 176,   5);
        }
        else if (totalSlots <= 36)
        {
            mc.getTextureManager().bindTexture(TEXTURE_DOUBLE_CHEST);
            mc.ingameGUI.drawTexturedModalRect(x, y     , 0,   0, 176,  89);
            mc.ingameGUI.drawTexturedModalRect(x, y + 89, 0,   4, 176,   6);
            mc.ingameGUI.drawTexturedModalRect(x, y + 95, 0, 219, 176,   3);
        }
        else
        {
            mc.getTextureManager().bindTexture(TEXTURE_DOUBLE_CHEST);
            mc.ingameGUI.drawTexturedModalRect(x, y      , 0,   0, 176, 139);
            mc.ingameGUI.drawTexturedModalRect(x, y + 139, 0, 219, 176,   3);
        }
    }

    public static void renderInventoryStacks(IInventory inv, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, Minecraft mc)
    {
        if (inv instanceof TileEntityFurnace)
        {
            renderStackAt(inv.getStackInSlot(0), startX +  56, startY + 17, 1, mc);
            renderStackAt(inv.getStackInSlot(1), startX +  56, startY + 53, 1, mc);
            renderStackAt(inv.getStackInSlot(2), startX + 116, startY + 35, 1, mc);
            return;
        }
        else if (inv instanceof TileEntityBrewingStand)
        {
            renderStackAt(inv.getStackInSlot(0), startX +  56, startY + 51, 1, mc);
            renderStackAt(inv.getStackInSlot(1), startX +  79, startY + 58, 1, mc);
            renderStackAt(inv.getStackInSlot(2), startX + 102, startY + 51, 1, mc);
            renderStackAt(inv.getStackInSlot(3), startX +  79, startY + 17, 1, mc);
            renderStackAt(inv.getStackInSlot(4), startX +  17, startY + 17, 1, mc);
            return;
        }

        final int slots = inv.getSizeInventory();
        int x = startX;
        int y = startY;

        if (maxSlots < 0)
        {
            maxSlots = slots;
        }

        for (int slot = startSlot, i = 0; slot < slots && i < maxSlots;)
        {
            for (int column = 0; column < slotsPerRow && slot < slots && i < maxSlots; ++column, ++slot, ++i)
            {
                ItemStack stack = inv.getStackInSlot(slot);

                if (stack.isEmpty() == false)
                {
                    renderStackAt(stack, x, y, 1, mc);
                }

                x += 18;
            }

            x = startX;
            y += 18;
        }
    }

    public static void renderStackAt(ItemStack stack, float x, float y, float scale, Minecraft mc)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1);
        GlStateManager.disableLighting();

        //Gui.drawRect(0, 0, 16, 16, 0x20FFFFFF); // light background for the item

        RenderHelper.enableGUIStandardItemLighting();

        mc.getRenderItem().zLevel += 100;
        mc.getRenderItem().renderItemAndEffectIntoGUI(mc.player, stack, 0, 0);
        mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, stack, 0, 0, null);
        mc.getRenderItem().zLevel -= 100;

        //GlStateManager.disableBlend();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    public static void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color);
    }

    public static void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float)x, (float)y, color);
    }

    public static void drawHorizontalLine(int startX, int endX, int y, int color)
    {
        if (endX < startX)
        {
            int i = startX;
            startX = endX;
            endX = i;
        }

        Gui.drawRect(startX, y, endX + 1, y + 1, color);
    }

    public static void drawVerticalLine(int x, int startY, int endY, int color)
    {
        if (endY < startY)
        {
            int i = startY;
            startY = endY;
            endY = i;
        }

        Gui.drawRect(x, startY + 1, x + 1, endY, color);
    }

    public static void renderText(int x, int y, int color, List<String> lines, FontRenderer font)
    {
        if (lines.isEmpty() == false)
        {
            for (String line : lines)
            {
                font.drawString(line, x, y, color);
                y += font.FONT_HEIGHT + 2;
            }
        }
    }

    public static int renderText(Minecraft mc, int xOff, int yOff, double scale, int textColor, int bgColor,
            HudAlignment alignment, boolean useBackground, boolean useShadow, List<String> lines)
    {
        FontRenderer fontRenderer = mc.fontRenderer;
        ScaledResolution res = new ScaledResolution(mc);
        final int lineHeight = fontRenderer.FONT_HEIGHT + 2;
        final int bgMargin = 2;
        double posX = xOff + bgMargin;
        double posY = yOff + bgMargin;

        // Only Chuck Norris can divide by zero
        if (scale == 0d)
        {
            return (int) yOff;
        }

        if (alignment == HudAlignment.TOP_RIGHT)
        {
            Collection<PotionEffect> effects = mc.player.getActivePotionEffects();

            if (effects.isEmpty() == false)
            {
                int y1 = 0;
                int y2 = 0;

                for (PotionEffect effect : effects)
                {
                    Potion potion = effect.getPotion();

                    if (effect.doesShowParticles() && potion.hasStatusIcon())
                    {
                        if (potion.isBeneficial())
                        {
                            y1 = 26;
                        }
                        else
                        {
                            y2 = 52;
                            break;
                        }
                    }
                }

                posY += Math.max(y1, y2) / scale;
            }
        }

        switch (alignment)
        {
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                posY = res.getScaledHeight() / scale - (lines.size() * lineHeight) - yOff + 2;
                break;
            case CENTER:
                posY = (res.getScaledHeight() / scale / 2.0d) - (lines.size() * lineHeight / 2.0d) + yOff;
                break;
            default:
        }

        if (scale != 1d)
        {
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, 0);
        }

        for (String line : lines)
        {
            final int width = fontRenderer.getStringWidth(line);

            switch (alignment)
            {
                case TOP_RIGHT:
                case BOTTOM_RIGHT:
                    posX = (res.getScaledWidth() / scale) - width - xOff - bgMargin;
                    break;
                case CENTER:
                    posX = (res.getScaledWidth() / scale / 2) - (width / 2) - xOff;
                    break;
                default:
            }

            final int x = (int) posX;
            final int y = (int) posY;
            posY += (double) lineHeight;

            if (useBackground)
            {
                Gui.drawRect(x - bgMargin, y - bgMargin, x + width + bgMargin, y + fontRenderer.FONT_HEIGHT, bgColor);
            }

            if (useShadow)
            {
                fontRenderer.drawStringWithShadow(line, x, y, textColor);
            }
            else
            {
                fontRenderer.drawString(line, x, y, textColor);
            }
        }

        if (scale != 1d)
        {
            GlStateManager.popMatrix();
        }

        return (int) Math.ceil(posY);
    }

    /*
    public static void enableGUIStandardItemLighting(float scale)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);

        enableStandardItemLighting(scale);

        GlStateManager.popMatrix();
    }

    public static void enableStandardItemLighting(float scale)
    {
        GlStateManager.enableLighting();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        GlStateManager.enableColorMaterial();
        GlStateManager.colorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
        GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, RenderHelper.setColorBuffer((float) LIGHT0_POS.x, (float) LIGHT0_POS.y, (float) LIGHT0_POS.z, 0.0f));

        float lightStrength = 0.3F * scale;
        GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(lightStrength, lightStrength, lightStrength, 1.0F));
        GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, RenderHelper.setColorBuffer((float) LIGHT1_POS.x, (float) LIGHT1_POS.y, (float) LIGHT1_POS.z, 0.0f));
        GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(lightStrength, lightStrength, lightStrength, 1.0F));
        GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));

        GlStateManager.shadeModel(GL11.GL_FLAT);

        float ambientLightStrength = 0.4F;
        GlStateManager.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, RenderHelper.setColorBuffer(ambientLightStrength, ambientLightStrength, ambientLightStrength, 1.0F));
    }
    */
}
