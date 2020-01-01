package fi.dy.masa.malilib.util;

import net.minecraft.util.text.TextFormatting;

public enum DyeColorCode
{
    WHITE       ( 0, TextFormatting.WHITE),
    ORANGE      ( 1, TextFormatting.GOLD),
    MAGENTA     ( 2, TextFormatting.LIGHT_PURPLE),
    LIGHT_BLUE  ( 3, TextFormatting.BLUE),
    YELLOW      ( 4, TextFormatting.YELLOW),
    LIME        ( 5, TextFormatting.GREEN),
    PINK        ( 6, TextFormatting.LIGHT_PURPLE),
    GRAY        ( 7, TextFormatting.DARK_GRAY),
    SILVER      ( 8, TextFormatting.GRAY),
    CYAN        ( 9, TextFormatting.DARK_AQUA),
    PURPLE      (10, TextFormatting.DARK_PURPLE),
    BLUE        (11, TextFormatting.DARK_BLUE),
    BROWN       (12, TextFormatting.GOLD),
    GREEN       (13, TextFormatting.DARK_GREEN),
    RED         (14, TextFormatting.RED),
    BLACK       (15, TextFormatting.BLACK);

    private static final DyeColorCode[] COLOR_CODES_BY_META = DyeColorCode.values();

    private final int meta;
    private final TextFormatting textColor;

    DyeColorCode(int meta, TextFormatting textColor)
    {
        this.meta = meta;
        this.textColor = textColor;
    }

    public int getMetadata()
    {
        return this.meta;
    }

    public TextFormatting getTextColor()
    {
        return this.textColor;
    }

    public static DyeColorCode getByMeta(int meta)
    {
        if (meta < 0 || meta > 15)
        {
            meta = 0;
        }

        return COLOR_CODES_BY_META[meta];
    }
}
