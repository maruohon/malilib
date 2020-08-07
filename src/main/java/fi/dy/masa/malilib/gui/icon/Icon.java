package fi.dy.masa.malilib.gui.icon;

import net.minecraft.util.ResourceLocation;

public interface Icon
{
    int getWidth();

    int getHeight();

    int getU();

    int getV();

    void renderAt(int x, int y, float zLevel, boolean enabled, boolean hovered);

    ResourceLocation getTexture();
}
