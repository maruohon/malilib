package malilib.render.inventory;

import malilib.MaLiLibReference;
import malilib.gui.icon.BaseIcon;
import malilib.gui.icon.DefaultIcons;
import malilib.util.StringUtils;

public class BuiltinInventoryRenderDefinitions
{
    private static final BaseIcon PLAYER_MAIN_INV_BACKGROUND     = new BaseIcon(0, 0, 176, 64, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));
    private static final BaseIcon PLAYER_HOTBAR_BACKGROUND_1     = new BaseIcon(0,  6, 176, 19, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));
    private static final BaseIcon PLAYER_HOTBAR_BACKGROUND_2     = new BaseIcon(0, 61, 176,  7, StringUtils.identifier(MaLiLibReference.MOD_ID, "textures/gui/inventory_background_misc.png"));

    public static final InventoryRenderDefinition BREWING_STAND = InventoryRenderDefinition.builder()
                            .renderSize(117, 73)
                            .slotPosition(0, 47, 42)
                            .slotPosition(1, 70, 49)
                            .slotPosition(2, 93, 42)
                            .slotPosition(3, 70,  8)
                            .slotPosition(4,  8,  8)
                            .backgroundTexturePiece(0, 0, DefaultIcons.INV_BACKGROUND_BREWING_STAND).build();

    public static final InventoryRenderDefinition DROPPER = InventoryRenderDefinition.builder()
                            .slotsPerRow(3)
                            .renderSize(68, 68)
                            .backgroundTexturePiece(0, 0, DefaultIcons.INV_BACKGROUND_DROPPER).build();

    public static final InventoryRenderDefinition FURNACE = InventoryRenderDefinition.builder()
                            .slotPosition(0,  8,  8)
                            .slotPosition(1,  8, 44)
                            .slotPosition(2, 68, 26)
                            .renderSize(96, 68)
                            .backgroundTexturePiece(0, 0, DefaultIcons.INV_BACKGROUND_FURNACE).build();

    public static final InventoryRenderDefinition GENERIC_27 = InventoryRenderDefinition.builder()
                            .slotsPerRow(9)
                            .renderSize(176, 68)
                            .backgroundTexturePiece(0, 0, DefaultIcons.INV_BACKGROUND_GENERIC_27).build();

    public static final InventoryRenderDefinition GENERIC_54 = InventoryRenderDefinition.builder()
                            .slotsPerRow(9)
                            .renderSize(176, 122)
                            .backgroundTexturePiece(0, 0, DefaultIcons.INV_BACKGROUND_GENERIC_54).build();

    public static final InventoryRenderDefinition GENERIC = InventoryRenderDefinition.builder()
                            .renderSizeFunctions((slots) -> Math.min(9, slots) * 18 + 14,
                                                 (slots) -> ((int) Math.ceil(slots / 9.0)) * 18 + 14)
                            .build();

    public static final InventoryRenderDefinition HOPPER = InventoryRenderDefinition.builder()
                            .slotsPerRow(5)
                            .renderSize(104, 32)
                            .backgroundTexturePiece(0, 0, DefaultIcons.INV_BACKGROUND_HOPPER).build();

    public static final InventoryRenderDefinition HORSE = InventoryRenderDefinition.builder()
                            .renderSize(197, 86)
                            .inventoryRange(0, 6, 1,  8,  8)
                            .inventoryRange(6, 2, 1, 65,  8)
                            .inventoryRange(8, -1, (slots) -> (slots - 8) / 3, 101, 8, true)
                            .backgroundTexturePiece( 0,  0, DefaultIcons.INV_BACKGROUND_LIVING_ENTITY)
                            .backgroundTexturePiece(57,  0, DefaultIcons.INV_BACKGROUND_HORSE_EQUIPMENT)
                            .backgroundTexturePiece(93,  0, DefaultIcons.INV_BACKGROUND_HORSE_INVENTORY)
                            .emptySlotBackgroundTexture(0,  7,  7, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_HEAD)
                            .emptySlotBackgroundTexture(1,  7, 25, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_CHEST)
                            .emptySlotBackgroundTexture(2,  7, 43, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_LEGS)
                            .emptySlotBackgroundTexture(3,  7, 61, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_BOOTS)
                            .emptySlotBackgroundTexture(5, 28, 61, DefaultIcons.INV_SLOT_EQUIPMENT_SHIELD)
                            .emptySlotBackgroundTexture(6, 64,  7, DefaultIcons.INV_SLOT_HORSE_SADDLE)
                            .emptySlotBackgroundTexture(7, 64, 25, DefaultIcons.INV_SLOT_HORSE_ARMOR).build();

    public static final InventoryRenderDefinition LIVING_ENTITY = InventoryRenderDefinition.builder()
                            .renderSize(53, 86)
                            .inventoryRange(0, 4, 1,  8,  8)
                            .inventoryRange(4, 2, 1, 29, 44)
                            .backgroundTexturePiece(0, 0, DefaultIcons.INV_BACKGROUND_LIVING_ENTITY)
                            .emptySlotBackgroundTexture(0,  7,  7, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_HEAD)
                            .emptySlotBackgroundTexture(1,  7, 25, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_CHEST)
                            .emptySlotBackgroundTexture(2,  7, 43, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_LEGS)
                            .emptySlotBackgroundTexture(3,  7, 61, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_BOOTS)
                            .emptySlotBackgroundTexture(5, 28, 61, DefaultIcons.INV_SLOT_EQUIPMENT_SHIELD).build();

    // Identical to HORSE, except for the armor & saddle vs. carpet textures
    public static final InventoryRenderDefinition LLAMA = InventoryRenderDefinition.builder()
                            .renderSize(197, 86)
                            .inventoryRange(0, 6, 1,  8,  8)
                            .inventoryRange(6, 2, 1, 65,  8)
                            .inventoryRange(8, -1, (slots) -> (slots - 8) / 3, 101, 8, true)
                            .backgroundTexturePiece( 0,  0, DefaultIcons.INV_BACKGROUND_LIVING_ENTITY)
                            .backgroundTexturePiece(57,  0, DefaultIcons.INV_BACKGROUND_HORSE_EQUIPMENT)
                            .backgroundTexturePiece(93,  0, DefaultIcons.INV_BACKGROUND_HORSE_INVENTORY)
                            .emptySlotBackgroundTexture(0,  7,  7, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_HEAD)
                            .emptySlotBackgroundTexture(1,  7, 25, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_CHEST)
                            .emptySlotBackgroundTexture(2,  7, 43, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_LEGS)
                            .emptySlotBackgroundTexture(3,  7, 61, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_BOOTS)
                            .emptySlotBackgroundTexture(5, 28, 61, DefaultIcons.INV_SLOT_EQUIPMENT_SHIELD)
                            .emptySlotBackgroundTexture(6, 64, 25, DefaultIcons.INV_SLOT_LLAMA_CARPET).build();

    public static final InventoryRenderDefinition PLAYER_INVENTORY = InventoryRenderDefinition.builder()
                            .renderSize(176, 90)
                            .inventoryRange(9, 27, 9, 8,  8)
                            .inventoryRange(0,  9, 9, 8, 66)
                            .backgroundTexturePiece(0,  0, PLAYER_MAIN_INV_BACKGROUND)
                            .backgroundTexturePiece(0, 64, PLAYER_HOTBAR_BACKGROUND_1)
                            .backgroundTexturePiece(0, 83, PLAYER_HOTBAR_BACKGROUND_2).build();

    public static final InventoryRenderDefinition VILLAGER = InventoryRenderDefinition.builder()
                            .renderSize(108, 86)
                            .inventoryRange(0, 4, 1,  8,  8)
                            .inventoryRange(4, 2, 1, 29, 44)
                            .inventoryRange(6, 8, 2, 65,  8)
                            .backgroundTexturePiece( 0, 0, DefaultIcons.INV_BACKGROUND_LIVING_ENTITY)
                            .backgroundTexturePiece(57, 0, DefaultIcons.INV_BACKGROUND_VILLAGER_INV)
                            .emptySlotBackgroundTexture(0,  7,  7, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_HEAD)
                            .emptySlotBackgroundTexture(1,  7, 25, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_CHEST)
                            .emptySlotBackgroundTexture(2,  7, 43, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_LEGS)
                            .emptySlotBackgroundTexture(3,  7, 61, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_BOOTS)
                            .emptySlotBackgroundTexture(5, 28, 61, DefaultIcons.INV_SLOT_EQUIPMENT_SHIELD).build();

    /*
    public static final InventoryRenderDefinition VILLAGER_INVENTORY = InventoryRenderDefinition.builder()
                            .slotsPerRow(2)
                            .renderSize(50, 86)
                            .backgroundTexturePiece(0, 0, DefaultIcons.INV_BACKGROUND_VILLAGER_INV).build();
    */
}
