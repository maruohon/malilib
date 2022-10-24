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
                            .withRenderSize(117, 73)
                            .withSlotPosition(0, 47, 42)
                            .withSlotPosition(1, 70, 49)
                            .withSlotPosition(2, 93, 42)
                            .withSlotPosition(3, 70,  8)
                            .withSlotPosition(4,  8,  8)
                            .withBackgroundTexturePiece(0, 0, DefaultIcons.INV_BACKGROUND_BREWING_STAND).build();

    public static final InventoryRenderDefinition DROPPER = InventoryRenderDefinition.builder()
                            .withSlotsPerRow(3)
                            .withRenderSize(68, 68)
                            .withBackgroundTexturePiece(0, 0, DefaultIcons.INV_BACKGROUND_DROPPER).build();

    public static final InventoryRenderDefinition FURNACE = InventoryRenderDefinition.builder()
                            .withSlotPosition(0,  8,  8)
                            .withSlotPosition(1,  8, 44)
                            .withSlotPosition(2, 68, 26)
                            .withRenderSize(96, 68)
                            .withBackgroundTexturePiece(0, 0, DefaultIcons.INV_BACKGROUND_FURNACE).build();

    public static final InventoryRenderDefinition GENERIC_27 = InventoryRenderDefinition.builder()
                            .withSlotsPerRow(9)
                            .withRenderSize(176, 68)
                            .withBackgroundTexturePiece(0, 0, DefaultIcons.INV_BACKGROUND_GENERIC_27).build();

    public static final InventoryRenderDefinition GENERIC_54 = InventoryRenderDefinition.builder()
                            .withSlotsPerRow(9)
                            .withRenderSize(176, 122)
                            .withBackgroundTexturePiece(0, 0, DefaultIcons.INV_BACKGROUND_GENERIC_54).build();

    public static final InventoryRenderDefinition GENERIC = InventoryRenderDefinition.builder()
                            .withRenderSizeFunctions((slots) -> Math.min(9, slots) * 18 + 14,
                                                     (slots) -> ((int) Math.ceil(slots / 9.0)) * 18 + 14)
                            .build();

    public static final InventoryRenderDefinition HOPPER = InventoryRenderDefinition.builder()
                            .withSlotsPerRow(5)
                            .withRenderSize(104, 32)
                            .withBackgroundTexturePiece(0, 0, DefaultIcons.INV_BACKGROUND_HOPPER).build();

    public static final InventoryRenderDefinition HORSE = InventoryRenderDefinition.builder()
                            .withRenderSize(197, 86)
                            .withInventoryRange(0, 6, 1,  8,  8)
                            .withInventoryRange(6, 2, 1, 65,  8)
                            .withInventoryRange(8, -1, (slots) -> (slots - 8) / 3, 101, 8, true)
                            .withBackgroundTexturePiece( 0,  0, DefaultIcons.INV_BACKGROUND_LIVING_ENTITY)
                            .withBackgroundTexturePiece(57,  0, DefaultIcons.INV_BACKGROUND_HORSE_EQUIPMENT)
                            .withBackgroundTexturePiece(93,  0, DefaultIcons.INV_BACKGROUND_HORSE_INVENTORY)
                            .withEmptySlotBackgroundTexture(0,  7,  7, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_HEAD)
                            .withEmptySlotBackgroundTexture(1,  7, 25, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_CHEST)
                            .withEmptySlotBackgroundTexture(2,  7, 43, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_LEGS)
                            .withEmptySlotBackgroundTexture(3,  7, 61, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_BOOTS)
                            .withEmptySlotBackgroundTexture(5, 28, 61, DefaultIcons.INV_SLOT_EQUIPMENT_SHIELD)
                            .withEmptySlotBackgroundTexture(6, 64,  7, DefaultIcons.INV_SLOT_HORSE_SADDLE)
                            .withEmptySlotBackgroundTexture(7, 64, 25, DefaultIcons.INV_SLOT_HORSE_ARMOR).build();

    public static final InventoryRenderDefinition LIVING_ENTITY = InventoryRenderDefinition.builder()
                            .withRenderSize(53, 86)
                            .withInventoryRange(0, 4, 1,  8,  8)
                            .withInventoryRange(4, 2, 1, 29, 44)
                            .withBackgroundTexturePiece(0, 0, DefaultIcons.INV_BACKGROUND_LIVING_ENTITY)
                            .withEmptySlotBackgroundTexture(0,  7,  7, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_HEAD)
                            .withEmptySlotBackgroundTexture(1,  7, 25, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_CHEST)
                            .withEmptySlotBackgroundTexture(2,  7, 43, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_LEGS)
                            .withEmptySlotBackgroundTexture(3,  7, 61, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_BOOTS)
                            .withEmptySlotBackgroundTexture(5, 28, 61, DefaultIcons.INV_SLOT_EQUIPMENT_SHIELD).build();

    // Identical to HORSE, except for the armor & saddle vs. carpet textures
    public static final InventoryRenderDefinition LLAMA = InventoryRenderDefinition.builder()
                            .withRenderSize(197, 86)
                            .withInventoryRange(0, 6, 1,  8,  8)
                            .withInventoryRange(6, 2, 1, 65,  8)
                            .withInventoryRange(8, -1, (slots) -> (slots - 8) / 3, 101, 8, true)
                            .withBackgroundTexturePiece( 0,  0, DefaultIcons.INV_BACKGROUND_LIVING_ENTITY)
                            .withBackgroundTexturePiece(57,  0, DefaultIcons.INV_BACKGROUND_HORSE_EQUIPMENT)
                            .withBackgroundTexturePiece(93,  0, DefaultIcons.INV_BACKGROUND_HORSE_INVENTORY)
                            .withEmptySlotBackgroundTexture(0,  7,  7, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_HEAD)
                            .withEmptySlotBackgroundTexture(1,  7, 25, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_CHEST)
                            .withEmptySlotBackgroundTexture(2,  7, 43, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_LEGS)
                            .withEmptySlotBackgroundTexture(3,  7, 61, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_BOOTS)
                            .withEmptySlotBackgroundTexture(5, 28, 61, DefaultIcons.INV_SLOT_EQUIPMENT_SHIELD)
                            .withEmptySlotBackgroundTexture(6, 64, 25, DefaultIcons.INV_SLOT_LLAMA_CARPET).build();

    public static final InventoryRenderDefinition PLAYER_INVENTORY = InventoryRenderDefinition.builder()
                            .withRenderSize(176, 90)
                            .withInventoryRange(9, 27, 9, 8,  8)
                            .withInventoryRange(0,  9, 9, 8, 66)
                            .withBackgroundTexturePiece(0,  0, PLAYER_MAIN_INV_BACKGROUND)
                            .withBackgroundTexturePiece(0, 64, PLAYER_HOTBAR_BACKGROUND_1)
                            .withBackgroundTexturePiece(0, 83, PLAYER_HOTBAR_BACKGROUND_2).build();

    public static final InventoryRenderDefinition VILLAGER = InventoryRenderDefinition.builder()
                            .withRenderSize(108, 86)
                            .withInventoryRange(0, 4, 1,  8,  8)
                            .withInventoryRange(4, 2, 1, 29, 44)
                            .withInventoryRange(6, 8, 2, 65,  8)
                            .withBackgroundTexturePiece( 0, 0, DefaultIcons.INV_BACKGROUND_LIVING_ENTITY)
                            .withBackgroundTexturePiece(57, 0, DefaultIcons.INV_BACKGROUND_VILLAGER_INV)
                            .withEmptySlotBackgroundTexture(0,  7,  7, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_HEAD)
                            .withEmptySlotBackgroundTexture(1,  7, 25, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_CHEST)
                            .withEmptySlotBackgroundTexture(2,  7, 43, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_LEGS)
                            .withEmptySlotBackgroundTexture(3,  7, 61, DefaultIcons.INV_SLOT_EQUIPMENT_ARMOR_BOOTS)
                            .withEmptySlotBackgroundTexture(5, 28, 61, DefaultIcons.INV_SLOT_EQUIPMENT_SHIELD).build();

    /*
    public static final InventoryRenderDefinition VILLAGER_INVENTORY = InventoryRenderDefinition.builder()
                            .withSlotsPerRow(2)
                            .withRenderSize(50, 86)
                            .withBackgroundTexturePiece(0, 0, DefaultIcons.INV_BACKGROUND_VILLAGER_INV).build();
    */
}
