package malilib.gui.icon;

import malilib.MaLiLibReference;
import malilib.registry.Registry;
import malilib.util.data.Identifier;

public class DefaultIcons
{
    public static final BaseIcon EMPTY                          = register(  0,   0,  0,  0);
    public static final BaseIcon SMALL_ARROW_UP                 = register(  0,   0,  8,  8);
    public static final BaseIcon SMALL_ARROW_DOWN               = register(  0,   8,  8,  8);
    public static final BaseIcon SMALL_ARROW_RIGHT              = register(  0,  16,  8,  8);
    public static final BaseIcon SMALL_ARROW_LEFT               = register(  0,  24,  8,  8);
    public static final BaseIcon MEDIUM_ARROW_RIGHT             = register(  0,  32,  8,  8);
    public static final BaseIcon MEDIUM_ARROW_LEFT              = register(  0,  40,  8,  8);
    public static final BaseIcon THIN_DOUBLE_ARROW_LEFT         = register(  0,  48,  8,  8);
    public static final BaseIcon SMALL_DOUBLE_ARROW_LEFT        = register(  0,  56,  8,  8);
    public static final BaseIcon RADIO_BUTTON_UNSELECTED        = register(  0,  64,  8,  8);
    public static final BaseIcon RADIO_BUTTON_SELECTED          = register(  0,  72,  8,  8);
    public static final BaseIcon GROUP_EXPAND_PLUS              = register(  0,  80,  8,  8);
    public static final BaseIcon GROUP_COLLAPSE_MINUS           = register(  0,  88,  8,  8);

    public static final BaseIcon LIGHT_GREEN_OFF                = register( 40, 194,  8,  8, 0, 0);
    public static final BaseIcon LIGHT_GREEN_ON                 = register( 40, 202,  8,  8, 0, 0);
    public static final BaseIcon LIGHT_RED_OFF                  = register( 40, 210,  8,  8, 0, 0);
    public static final BaseIcon LIGHT_RED_ON                   = register( 40, 218,  8,  8, 0, 0);

    public static final BaseIcon LOCK_UNLOCKED                  = register( 24,   0,  9,  9, 0, 0);
    public static final BaseIcon LOCK_LOCKED                    = register( 33,   0,  9,  9, 0, 0);
    public static final BaseIcon LIST_ADD_PLUS_9                = register( 24,   9,  9,  9);
    public static final BaseIcon LIST_REMOVE_MINUS_9            = register( 24,  18,  9,  9);
    public static final BaseIcon CLOSE_BUTTON_9                 = register( 24,  27,  9,  9);

    public static final BaseIcon BTN_PLUSMINUS_10               = register( 51,   0, 10, 10);

    public static final BaseIcon INFO_11                        = register( 81,   0, 11, 11);
    public static final BaseIcon EXCLAMATION_11                 = register( 81,  11, 11, 11);
    public static final BaseIcon CHECKMARK_LIGHT_OFF            = register( 81,  22, 11, 11);
    public static final BaseIcon CHECKMARK_LIGHT_ON             = register( 81,  33, 11, 11);
    public static final BaseIcon CHECKMARK_DARK_OFF             = register( 81,  44, 11, 11);
    public static final BaseIcon CHECKMARK_DARK_ON_VARIANT_1    = register( 81,  55, 11, 11);
    public static final BaseIcon CHECKMARK_DARK_ON_VARIANT_2    = register( 81,  66, 11, 11);
    public static final BaseIcon CHECKMARK_DARK_ON_VARIANT_3    = register( 81,  77, 11, 11);
    public static final BaseIcon CHECKMARK_DARK_ON_VARIANT_4    = register( 81,  88, 11, 11);

    public static final BaseIcon FILE_BROWSER_DIR               = register(114,   0, 12, 12);
    public static final BaseIcon FILE_BROWSER_DIR_ROOT          = register(114,  12, 12, 12);
    public static final BaseIcon FILE_BROWSER_DIR_UP            = register(114,  24, 12, 12);
    public static final BaseIcon FILE_BROWSER_CREATE_DIR        = register(114,  36, 12, 12);
    public static final BaseIcon SEARCH                         = register(114,  48, 12, 12);
    public static final BaseIcon RESET_12                       = register(114,  60, 12, 12);

    public static final BaseIcon ARROW_UP                       = register(150,   0, 13, 13);
    public static final BaseIcon ARROW_DOWN                     = register(150,  13, 13, 13);
    public static final BaseIcon ARROW_RIGHT                    = register(150,  26, 13, 13);
    public static final BaseIcon ARROW_LEFT                     = register(150,  39, 13, 13);
    public static final BaseIcon LIST_ADD_PLUS_13               = register(150,  52, 13, 13);
    public static final BaseIcon LIST_REMOVE_MINUS_13           = register(150,  65, 13, 13);

    public static final BaseIcon BTN_PLUSMINUS_14               = register(189,   0, 14, 14);

    public static final BaseIcon BTN_SLIDER                     = register(195, 104, 16, 16);
    public static final BaseIcon BTN_TXTFIELD                   = register(195, 120, 16, 16);
    public static final BaseIcon BTN_PLUSMINUS_16               = register(195, 136, 16, 16);

    public static final BaseIcon INFO_ICON_18                   = register( 40, 176, 18, 18);

    public static final BaseIcon SLIDER_RED                     = register(114,  96,  6, 40);
    public static final BaseIcon SLIDER_GREEN                   = register(132,  96,  6, 40);


    public static final BaseIcon BUTTON_BACKGROUND              = register( 56, 196, 200, 20, 0, 20);

    public static final BaseIcon TOAST_BACKGROUND                   = register(  0,   0, 256, 128, "textures/gui/toasts.png");

    public static final BaseIcon INV_BACKGROUND_EMPTY_13_X_13       = register(  0,   0, 248, 248, "textures/gui/inventory_background_empty_13x13.png");
    public static final BaseIcon INV_BACKGROUND_GENERIC_54          = register(  0,   0, 176, 122, "textures/gui/inventory_background_generic_54.png");

    public static final BaseIcon INV_BACKGROUND_BREWING_STAND       = register(128, 183, 117,  73, "textures/gui/inventory_background_misc.png");
    public static final BaseIcon INV_BACKGROUND_DROPPER             = register(176,   0,  68,  68, "textures/gui/inventory_background_misc.png");
    public static final BaseIcon INV_BACKGROUND_FURNACE             = register(  0, 188,  96,  68, "textures/gui/inventory_background_misc.png");
    public static final BaseIcon INV_BACKGROUND_GENERIC_27          = register(  0,   0, 176,  68, "textures/gui/inventory_background_misc.png");
    public static final BaseIcon INV_BACKGROUND_HOPPER              = register(  0,  68, 104,  32, "textures/gui/inventory_background_misc.png");
    public static final BaseIcon INV_BACKGROUND_HORSE_EQUIPMENT     = register( 96, 206,  32,  50, "textures/gui/inventory_background_misc.png");
    public static final BaseIcon INV_BACKGROUND_HORSE_INVENTORY     = register(103, 102, 105,  68, "textures/gui/inventory_background_misc.png");
    public static final BaseIcon INV_BACKGROUND_LIVING_ENTITY       = register(  0, 102,  53,  86, "textures/gui/inventory_background_misc.png");
    public static final BaseIcon INV_BACKGROUND_VILLAGER_INV        = register( 53, 102,  50,  86, "textures/gui/inventory_background_misc.png");

    public static final BaseIcon INV_BACKGROUND_14_SLOTS_HORIZONTAL = register(  4, 238, 252,  18, "textures/gui/inventory_background_generic_54.png");
    public static final BaseIcon INV_BACKGROUND_14_SLOTS_VERTICAL   = register(238,   4,  18, 252, "textures/gui/inventory_background_generic_54.png");

    public static final BaseIcon INV_SLOT_EQUIPMENT_ARMOR_HEAD      = register(104,  68,  18,  18, "textures/gui/inventory_background_misc.png");
    public static final BaseIcon INV_SLOT_EQUIPMENT_ARMOR_CHEST     = register(122,  68,  18,  18, "textures/gui/inventory_background_misc.png");
    public static final BaseIcon INV_SLOT_EQUIPMENT_ARMOR_LEGS      = register(140,  68,  18,  18, "textures/gui/inventory_background_misc.png");
    public static final BaseIcon INV_SLOT_EQUIPMENT_ARMOR_BOOTS     = register(158,  68,  18,  18, "textures/gui/inventory_background_misc.png");
    public static final BaseIcon INV_SLOT_EQUIPMENT_SHIELD          = register(176,  68,  18,  18, "textures/gui/inventory_background_misc.png");

    public static final BaseIcon INV_SLOT_HORSE_ARMOR               = register(194,  68,  18,  18, "textures/gui/inventory_background_misc.png");
    public static final BaseIcon INV_SLOT_HORSE_SADDLE              = register(212,  68,  18,  18, "textures/gui/inventory_background_misc.png");
    public static final BaseIcon INV_SLOT_LLAMA_CARPET              = register(230,  68,  18,  18, "textures/gui/inventory_background_misc.png");

    private static BaseIcon register(int u, int v, int w, int h)
    {
        return register(u, v, w, h, w, 0);
    }

    private static BaseIcon register(int u, int v, int w, int h, int variantOffU, int variantOffV)
    {
        return register(new BaseIcon(u, v, w, h, variantOffU, variantOffV));
    }

    private static BaseIcon register(int u, int v, int w, int h, String texture)
    {
        return register(new BaseIcon(u, v, w, h, 0, 0, new Identifier(MaLiLibReference.MOD_ID, texture)));
    }

    public static <T extends Icon> T register(T icon)
    {
        return Registry.ICON.registerModIcon(icon);
    }
}
