package fi.dy.masa.malilib.gui.icon;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.StringUtils;

public class DefaultIcons
{
    public static final BaseMultiIcon EMPTY                         = register(new BaseMultiIcon(  0,   0,  0,  0));
    public static final BaseMultiIcon SMALL_ARROW_UP                = register(new BaseMultiIcon(  0,   0,  8,  8));
    public static final BaseMultiIcon SMALL_ARROW_DOWN              = register(new BaseMultiIcon(  0,   8,  8,  8));
    public static final BaseMultiIcon SMALL_ARROW_RIGHT             = register(new BaseMultiIcon(  0,  16,  8,  8));
    public static final BaseMultiIcon SMALL_ARROW_LEFT              = register(new BaseMultiIcon(  0,  24,  8,  8));
    public static final BaseMultiIcon MEDIUM_ARROW_RIGHT            = register(new BaseMultiIcon(  0,  32,  8,  8));
    public static final BaseMultiIcon MEDIUM_ARROW_LEFT             = register(new BaseMultiIcon(  0,  40,  8,  8));
    public static final BaseMultiIcon THIN_DOUBLE_ARROW_LEFT        = register(new BaseMultiIcon(  0,  48,  8,  8));
    public static final BaseMultiIcon SMALL_DOUBLE_ARROW_LEFT       = register(new BaseMultiIcon(  0,  56,  8,  8));
    public static final BaseMultiIcon RADIO_BUTTON_UNSELECTED       = register(new BaseMultiIcon(  0,  64,  8,  8));
    public static final BaseMultiIcon RADIO_BUTTON_SELECTED         = register(new BaseMultiIcon(  0,  72,  8,  8));
    public static final BaseMultiIcon GROUP_EXPAND_PLUS             = register(new BaseMultiIcon(  0,  80,  8,  8));
    public static final BaseMultiIcon GROUP_COLLAPSE_MINUS          = register(new BaseMultiIcon(  0,  88,  8,  8));

    public static final BaseMultiIcon LIGHT_GREEN_OFF               = register(new BaseMultiIcon( 40, 194,  8,  8, 0, 0));
    public static final BaseMultiIcon LIGHT_GREEN_ON                = register(new BaseMultiIcon( 40, 202,  8,  8, 0, 0));
    public static final BaseMultiIcon LIGHT_RED_OFF                 = register(new BaseMultiIcon( 40, 210,  8,  8, 0, 0));
    public static final BaseMultiIcon LIGHT_RED_ON                  = register(new BaseMultiIcon( 40, 218,  8,  8, 0, 0));

    public static final BaseMultiIcon LOCK_UNLOCKED                 = register(new BaseMultiIcon( 24,   0,  9,  9, 0, 0));
    public static final BaseMultiIcon LOCK_LOCKED                   = register(new BaseMultiIcon( 33,   0,  9,  9, 0, 0));
    public static final BaseMultiIcon LIST_ADD_PLUS_9               = register(new BaseMultiIcon( 24,   9,  9,  9));
    public static final BaseMultiIcon LIST_REMOVE_MINUS_9           = register(new BaseMultiIcon( 24,  18,  9,  9));
    public static final BaseMultiIcon CLOSE_BUTTON_9                = register(new BaseMultiIcon( 24,  27,  9,  9));

    public static final BaseMultiIcon BTN_PLUSMINUS_10              = register(new BaseMultiIcon( 51,   0, 10, 10));

    public static final BaseMultiIcon INFO_ICON_11                  = register(new BaseMultiIcon( 81,   0, 11, 11));
    public static final BaseMultiIcon EXCLAMATION                   = register(new BaseMultiIcon( 81,  11, 11, 11));
    public static final BaseMultiIcon CHECKMARK_OFF                 = register(new BaseMultiIcon( 81,  22, 11, 11));
    public static final BaseMultiIcon CHECKMARK_ON                  = register(new BaseMultiIcon( 81,  33, 11, 11));

    public static final BaseMultiIcon FILE_BROWSER_DIR              = register(new BaseMultiIcon(114,   0, 12, 12));
    public static final BaseMultiIcon FILE_BROWSER_DIR_ROOT         = register(new BaseMultiIcon(114,  12, 12, 12));
    public static final BaseMultiIcon FILE_BROWSER_DIR_UP           = register(new BaseMultiIcon(114,  24, 12, 12));
    public static final BaseMultiIcon FILE_BROWSER_CREATE_DIR       = register(new BaseMultiIcon(114,  36, 12, 12));
    public static final BaseMultiIcon SEARCH                        = register(new BaseMultiIcon(114,  48, 12, 12));
    public static final BaseMultiIcon RESET_12                      = register(new BaseMultiIcon(114,  60, 12, 12));

    public static final BaseMultiIcon ARROW_UP                      = register(new BaseMultiIcon(150,   0, 13, 13));
    public static final BaseMultiIcon ARROW_DOWN                    = register(new BaseMultiIcon(150,  13, 13, 13));
    public static final BaseMultiIcon ARROW_RIGHT                   = register(new BaseMultiIcon(150,  26, 13, 13));
    public static final BaseMultiIcon ARROW_LEFT                    = register(new BaseMultiIcon(150,  39, 13, 13));
    public static final BaseMultiIcon LIST_ADD_PLUS_13              = register(new BaseMultiIcon(150,  52, 13, 13));
    public static final BaseMultiIcon LIST_REMOVE_MINUS_13          = register(new BaseMultiIcon(150,  65, 13, 13));

    public static final BaseMultiIcon BTN_PLUSMINUS_14              = register(new BaseMultiIcon(189,   0, 14, 14));

    public static final BaseMultiIcon BTN_SLIDER                    = register(new BaseMultiIcon(195, 104, 16, 16));
    public static final BaseMultiIcon BTN_TXTFIELD                  = register(new BaseMultiIcon(195, 120, 16, 16));
    public static final BaseMultiIcon BTN_PLUSMINUS_16              = register(new BaseMultiIcon(195, 136, 16, 16));

    public static final BaseMultiIcon INFO_ICON_18                  = register(new BaseMultiIcon( 40, 176, 18, 18));

    public static final BaseMultiIcon SLIDER_RED                    = register(new BaseMultiIcon(114,  96,  6, 40));
    public static final BaseMultiIcon SLIDER_GREEN                  = register(new BaseMultiIcon(132,  96,  6, 40));


    public static final BaseMultiIcon BUTTON_BACKGROUND             = register(new BaseMultiIcon( 56, 196, 200, 20, 0, 20));

    public static final BaseIcon TOAST_BACKGROUND                   = register(new BaseIcon(0, 0, 256, 128, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/toasts.png")));

    public static final BaseIcon INV_BACKGROUND_EMPTY_13_X_13       = register(new BaseIcon(  0,   0, 248, 248, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_empty_13x13.png")));
    public static final BaseIcon INV_BACKGROUND_GENERIC_54          = register(new BaseIcon(  0,   0, 176, 122, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_generic_54.png")));

    public static final BaseIcon INV_BACKGROUND_BREWING_STAND       = register(new BaseIcon(128, 183, 117,  73, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png")));
    public static final BaseIcon INV_BACKGROUND_DROPPER             = register(new BaseIcon(176,   0,  68,  68, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png")));
    public static final BaseIcon INV_BACKGROUND_FURNACE             = register(new BaseIcon(  0, 188,  96,  68, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png")));
    public static final BaseIcon INV_BACKGROUND_GENERIC_27          = register(new BaseIcon(  0,   0, 176,  68, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png")));
    public static final BaseIcon INV_BACKGROUND_HOPPER              = register(new BaseIcon(  0,  68, 104,  32, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png")));
    public static final BaseIcon INV_BACKGROUND_HORSE_EQUIPMENT     = register(new BaseIcon( 96, 206,  32,  50, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png")));
    public static final BaseIcon INV_BACKGROUND_HORSE_INVENTORY     = register(new BaseIcon(103, 102, 105,  68, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png")));
    public static final BaseIcon INV_BACKGROUND_LIVING_ENTITY       = register(new BaseIcon(  0, 102,  53,  86, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png")));
    public static final BaseIcon INV_BACKGROUND_VILLAGER_INV        = register(new BaseIcon( 53, 102,  50,  86, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png")));

    public static final BaseIcon INV_BACKGROUND_14_SLOTS_HORIZONTAL = register(new BaseIcon(  4, 238, 252,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_generic_54.png")));
    public static final BaseIcon INV_BACKGROUND_14_SLOTS_VERTICAL   = register(new BaseIcon(238,   4,  18, 252, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_generic_54.png")));

    public static final BaseIcon INV_SLOT_EQUIPMENT_ARMOR_HEAD      = register(new BaseIcon(104,  68,  18,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png")));
    public static final BaseIcon INV_SLOT_EQUIPMENT_ARMOR_CHEST     = register(new BaseIcon(122,  68,  18,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png")));
    public static final BaseIcon INV_SLOT_EQUIPMENT_ARMOR_LEGS      = register(new BaseIcon(140,  68,  18,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png")));
    public static final BaseIcon INV_SLOT_EQUIPMENT_ARMOR_BOOTS     = register(new BaseIcon(158,  68,  18,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png")));
    public static final BaseIcon INV_SLOT_EQUIPMENT_SHIELD          = register(new BaseIcon(176,  68,  18,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png")));

    public static final BaseIcon INV_SLOT_HORSE_ARMOR               = register(new BaseIcon(194,  68,  18,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png")));
    public static final BaseIcon INV_SLOT_HORSE_SADDLE              = register(new BaseIcon(212,  68,  18,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png")));
    public static final BaseIcon INV_SLOT_LLAMA_CARPET              = register(new BaseIcon(230,  68,  18,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png")));

    public static <T extends Icon> T register(T icon)
    {
        Registry.ICON.registerModIcon(icon);
        return icon;
    }
}
