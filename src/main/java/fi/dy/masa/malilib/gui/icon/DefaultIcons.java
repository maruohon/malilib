package fi.dy.masa.malilib.gui.icon;

import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.util.StringUtils;

public class DefaultIcons
{
    public static final BaseMultiIcon EMPTY                         = new BaseMultiIcon(  0,   0,  0,  0);
    public static final BaseMultiIcon SMALL_ARROW_UP                = new BaseMultiIcon(  0,   0,  8,  8);
    public static final BaseMultiIcon SMALL_ARROW_DOWN              = new BaseMultiIcon(  0,   8,  8,  8);
    public static final BaseMultiIcon SMALL_ARROW_RIGHT             = new BaseMultiIcon(  0,  16,  8,  8);
    public static final BaseMultiIcon SMALL_ARROW_LEFT              = new BaseMultiIcon(  0,  24,  8,  8);
    public static final BaseMultiIcon MEDIUM_ARROW_RIGHT            = new BaseMultiIcon(  0,  32,  8,  8);
    public static final BaseMultiIcon MEDIUM_ARROW_LEFT             = new BaseMultiIcon(  0,  40,  8,  8);
    public static final BaseMultiIcon THIN_DOUBLE_ARROW_LEFT        = new BaseMultiIcon(  0,  48,  8,  8);
    public static final BaseMultiIcon SMALL_DOUBLE_ARROW_LEFT       = new BaseMultiIcon(  0,  56,  8,  8);
    public static final BaseMultiIcon RADIO_BUTTON_UNSELECTED       = new BaseMultiIcon(  0,  64,  8,  8);
    public static final BaseMultiIcon RADIO_BUTTON_SELECTED         = new BaseMultiIcon(  0,  72,  8,  8);
    public static final BaseMultiIcon GROUP_EXPAND_PLUS             = new BaseMultiIcon(  0,  80,  8,  8);
    public static final BaseMultiIcon GROUP_COLLAPSE_MINUS          = new BaseMultiIcon(  0,  88,  8,  8);

    public static final BaseMultiIcon LIGHT_GREEN_OFF               = new BaseMultiIcon( 40, 194,  8,  8, 0, 0);
    public static final BaseMultiIcon LIGHT_GREEN_ON                = new BaseMultiIcon( 40, 202,  8,  8, 0, 0);
    public static final BaseMultiIcon LIGHT_RED_OFF                 = new BaseMultiIcon( 40, 210,  8,  8, 0, 0);
    public static final BaseMultiIcon LIGHT_RED_ON                  = new BaseMultiIcon( 40, 218,  8,  8, 0, 0);

    public static final BaseMultiIcon LOCK_UNLOCKED                 = new BaseMultiIcon( 24,   0,  9,  9, 0, 0);
    public static final BaseMultiIcon LOCK_LOCKED                   = new BaseMultiIcon( 33,   0,  9,  9, 0, 0);
    public static final BaseMultiIcon LIST_ADD_PLUS_9               = new BaseMultiIcon( 24,   9,  9,  9);
    public static final BaseMultiIcon LIST_REMOVE_MINUS_9           = new BaseMultiIcon( 24,  18,  9,  9);

    public static final BaseMultiIcon BTN_PLUSMINUS_10              = new BaseMultiIcon( 51,   0, 10, 10);

    public static final BaseMultiIcon INFO_ICON_11                  = new BaseMultiIcon( 81,   0, 11, 11);
    public static final BaseMultiIcon EXCLAMATION                   = new BaseMultiIcon( 81,  11, 11, 11);
    public static final BaseMultiIcon CHECKMARK_OFF                 = new BaseMultiIcon( 81,  22, 11, 11);
    public static final BaseMultiIcon CHECKMARK_ON                  = new BaseMultiIcon( 81,  33, 11, 11);

    public static final BaseMultiIcon FILE_BROWSER_DIR              = new BaseMultiIcon(114,   0, 12, 12);
    public static final BaseMultiIcon FILE_BROWSER_DIR_ROOT         = new BaseMultiIcon(114,  12, 12, 12);
    public static final BaseMultiIcon FILE_BROWSER_DIR_UP           = new BaseMultiIcon(114,  24, 12, 12);
    public static final BaseMultiIcon FILE_BROWSER_CREATE_DIR       = new BaseMultiIcon(114,  36, 12, 12);
    public static final BaseMultiIcon SEARCH                        = new BaseMultiIcon(114,  48, 12, 12);
    public static final BaseMultiIcon RESET_12                      = new BaseMultiIcon(114,  60, 12, 12);

    public static final BaseMultiIcon ARROW_UP                      = new BaseMultiIcon(150,   0, 13, 13);
    public static final BaseMultiIcon ARROW_DOWN                    = new BaseMultiIcon(150,  13, 13, 13);
    public static final BaseMultiIcon ARROW_RIGHT                   = new BaseMultiIcon(150,  26, 13, 13);
    public static final BaseMultiIcon ARROW_LEFT                    = new BaseMultiIcon(150,  39, 13, 13);
    public static final BaseMultiIcon LIST_ADD_PLUS_13              = new BaseMultiIcon(150,  52, 13, 13);
    public static final BaseMultiIcon LIST_REMOVE_MINUS_13          = new BaseMultiIcon(150,  65, 13, 13);

    public static final BaseMultiIcon BTN_PLUSMINUS_14              = new BaseMultiIcon(189,   0, 14, 14);

    public static final BaseMultiIcon BTN_SLIDER                    = new BaseMultiIcon(195, 104, 16, 16);
    public static final BaseMultiIcon BTN_TXTFIELD                  = new BaseMultiIcon(195, 120, 16, 16);
    public static final BaseMultiIcon BTN_PLUSMINUS_16              = new BaseMultiIcon(195, 136, 16, 16);

    public static final BaseMultiIcon INFO_ICON_18                  = new BaseMultiIcon( 40, 176, 18, 18);

    public static final BaseMultiIcon SLIDER_RED                    = new BaseMultiIcon(114,  96,  6, 40);
    public static final BaseMultiIcon SLIDER_GREEN                  = new BaseMultiIcon(132,  96,  6, 40);


    public static final BaseMultiIcon BUTTON_BACKGROUND             = new BaseMultiIcon( 56, 196, 200, 20, 0, 20);

    public static final BaseIcon TOAST_BACKGROUND                   = new BaseIcon(0, 0, 256, 128, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/toasts.png"));

    public static final BaseIcon INV_BACKGROUND_EMPTY_13_X_13       = new BaseIcon(  0,   0, 248, 248, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_empty_13x13.png"));
    public static final BaseIcon INV_BACKGROUND_GENERIC_54          = new BaseIcon(  0,   0, 176, 122, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_generic_54.png"));

    public static final BaseIcon INV_BACKGROUND_BREWING_STAND       = new BaseIcon(128, 183, 117,  73, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));
    public static final BaseIcon INV_BACKGROUND_DROPPER             = new BaseIcon(176,   0,  68,  68, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));
    public static final BaseIcon INV_BACKGROUND_FURNACE             = new BaseIcon(  0, 188,  96,  68, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));
    public static final BaseIcon INV_BACKGROUND_GENERIC_27          = new BaseIcon(  0,   0, 176,  68, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));
    public static final BaseIcon INV_BACKGROUND_HOPPER              = new BaseIcon(  0,  68, 104,  32, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));
    public static final BaseIcon INV_BACKGROUND_HORSE_EQUIPMENT     = new BaseIcon( 96, 206,  32,  50, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));
    public static final BaseIcon INV_BACKGROUND_HORSE_INVENTORY     = new BaseIcon(103, 102, 105,  68, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));
    public static final BaseIcon INV_BACKGROUND_LIVING_ENTITY       = new BaseIcon(  0, 102,  53,  86, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));
    public static final BaseIcon INV_BACKGROUND_VILLAGER_INV        = new BaseIcon( 53, 102,  50,  86, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));

    public static final BaseIcon INV_BACKGROUND_14_SLOTS_HORIZONTAL = new BaseIcon(  4, 238, 252,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_generic_54.png"));
    public static final BaseIcon INV_BACKGROUND_14_SLOTS_VERTICAL   = new BaseIcon(238,   4,  18, 252, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_generic_54.png"));

    public static final BaseIcon INV_SLOT_EQUIPMENT_ARMOR_HEAD      = new BaseIcon(104,  68,  18,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));
    public static final BaseIcon INV_SLOT_EQUIPMENT_ARMOR_CHEST     = new BaseIcon(122,  68,  18,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));
    public static final BaseIcon INV_SLOT_EQUIPMENT_ARMOR_LEGS      = new BaseIcon(140,  68,  18,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));
    public static final BaseIcon INV_SLOT_EQUIPMENT_ARMOR_BOOTS     = new BaseIcon(158,  68,  18,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));
    public static final BaseIcon INV_SLOT_EQUIPMENT_SHIELD          = new BaseIcon(176,  68,  18,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));

    public static final BaseIcon INV_SLOT_HORSE_ARMOR               = new BaseIcon(194,  68,  18,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));
    public static final BaseIcon INV_SLOT_HORSE_SADDLE              = new BaseIcon(212,  68,  18,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));
    public static final BaseIcon INV_SLOT_LLAMA_CARPET              = new BaseIcon(230,  68,  18,  18, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));
}
