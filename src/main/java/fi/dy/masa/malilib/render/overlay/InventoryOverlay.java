package fi.dy.masa.malilib.render.overlay;

import org.lwjgl.opengl.GL11;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.render.ItemRenderUtils;
import fi.dy.masa.malilib.render.RenderUtils;

public class InventoryOverlay
{
    public static final ResourceLocation TEXTURE_BREWING_STAND = new ResourceLocation("textures/gui/container/brewing_stand.png");
    public static final ResourceLocation TEXTURE_DISPENSER = new ResourceLocation("textures/gui/container/dispenser.png");
    public static final ResourceLocation TEXTURE_DOUBLE_CHEST = new ResourceLocation("textures/gui/container/generic_54.png");
    public static final ResourceLocation TEXTURE_FURNACE = new ResourceLocation("textures/gui/container/furnace.png");
    public static final ResourceLocation TEXTURE_HOPPER = new ResourceLocation("textures/gui/container/hopper.png");
    public static final ResourceLocation TEXTURE_PLAYER_INV = new ResourceLocation("textures/gui/container/hopper.png");
    public static final ResourceLocation TEXTURE_SINGLE_CHEST = new ResourceLocation("textures/gui/container/shulker_box.png");

    public static final InventoryProperties INV_PROPS_TEMP = new InventoryProperties();

    private static final EntityEquipmentSlot[] ARMOR_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] { EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET };

    public static void renderInventoryBackground(InventoryRenderType type, int x, int y, int zLevel, int slotsPerRow, int totalSlots, Minecraft mc)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        if (type == InventoryRenderType.FURNACE)
        {
            RenderUtils.bindTexture(TEXTURE_FURNACE);
            RenderUtils.drawTexturedRectBatched(x     , y     ,   0,   0,   4,  64, zLevel, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(x +  4, y     ,  84,   0,  92,   4, zLevel, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(x     , y + 64,   0, 162,  92,   4, zLevel, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(x + 92, y +  4, 172, 102,   4,  64, zLevel, buffer); // right (bottom)
            RenderUtils.drawTexturedRectBatched(x +  4, y +  4,  52,  13,  88,  60, zLevel, buffer); // middle
        }
        else if (type == InventoryRenderType.BREWING_STAND)
        {
            RenderUtils.bindTexture(TEXTURE_BREWING_STAND);
            RenderUtils.drawTexturedRectBatched(x      , y     ,   0,   0,   4,  68, zLevel, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(x +   4, y     ,  63,   0, 113,   4, zLevel, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(x      , y + 68,   0, 162, 113,   4, zLevel, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(x + 113, y +  4, 172,  98,   4,  68, zLevel, buffer); // right (bottom)
            RenderUtils.drawTexturedRectBatched(x +   4, y +  4,  13,  13, 109,  64, zLevel, buffer); // middle
        }
        else if (type == InventoryRenderType.DISPENSER)
        {
            RenderUtils.bindTexture(TEXTURE_DISPENSER);
            RenderUtils.drawTexturedRectBatched(x     , y     ,   0,   0,   7,  61, zLevel, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(x +  7, y     , 115,   0,  61,   7, zLevel, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(x     , y + 61,   0, 159,  61,   7, zLevel, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(x + 61, y +  7, 169, 105,   7,  61, zLevel, buffer); // right (bottom)
            RenderUtils.drawTexturedRectBatched(x +  7, y +  7,  61,  16,  54,  54, zLevel, buffer); // middle
        }
        else if (type == InventoryRenderType.HOPPER)
        {
            RenderUtils.bindTexture(TEXTURE_HOPPER);
            RenderUtils.drawTexturedRectBatched(x      , y     ,   0,   0,   7,  25, zLevel, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(x +   7, y     ,  79,   0,  97,   7, zLevel, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(x      , y + 25,   0, 126,  97,   7, zLevel, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(x +  97, y +  7, 169, 108,   7,  25, zLevel, buffer); // right (bottom)
            RenderUtils.drawTexturedRectBatched(x +   7, y +  7,  43,  19,  90,  18, zLevel, buffer); // middle
        }
        // Most likely a Villager, or possibly a Llama
        else if (type == InventoryRenderType.VILLAGER)
        {
            RenderUtils.bindTexture(TEXTURE_DOUBLE_CHEST);
            RenderUtils.drawTexturedRectBatched(x     , y     ,   0,   0,   7,  79, zLevel, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(x +  7, y     , 133,   0,  43,   7, zLevel, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(x     , y + 79,   0, 215,  43,   7, zLevel, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(x + 43, y +  7, 169, 143,   7,  79, zLevel, buffer); // right (bottom)
            RenderUtils.drawTexturedRectBatched(x +  7, y +  7,   7,  17,  36,  72, zLevel, buffer); // 2x4 slots
        }
        else if (type == InventoryRenderType.FIXED_27)
        {
            renderInventoryBackground27(x, y, zLevel, buffer, mc);
        }
        else if (type == InventoryRenderType.FIXED_54)
        {
            renderInventoryBackground54(x, y, zLevel, buffer, mc);
        }
        else
        {
            RenderUtils.bindTexture(TEXTURE_DOUBLE_CHEST);

            // Draw the slot backgrounds according to how many slots there actually are
            int rows = (int) (Math.ceil((double) totalSlots / (double) slotsPerRow));
            int bgw = Math.min(totalSlots, slotsPerRow) * 18 + 7;
            int bgh = rows * 18 + 7;

            RenderUtils.drawTexturedRectBatched(x      , y      ,         0,         0,   7, bgh, zLevel, buffer); // left (top)
            RenderUtils.drawTexturedRectBatched(x +   7, y      , 176 - bgw,         0, bgw,   7, zLevel, buffer); // top (right)
            RenderUtils.drawTexturedRectBatched(x      , y + bgh,         0,       215, bgw,   7, zLevel, buffer); // bottom (left)
            RenderUtils.drawTexturedRectBatched(x + bgw, y +   7,       169, 222 - bgh,   7, bgh, zLevel, buffer); // right (bottom)

            for (int row = 0; row < rows; row++)
            {
                int rowLen = MathHelper.clamp(totalSlots - (row * slotsPerRow), 1, slotsPerRow);
                RenderUtils.drawTexturedRectBatched(x + 7, y + row * 18 + 7, 7, 17, rowLen * 18, 18, zLevel, buffer);

                // Render the background for the last non-existing slots on the last row,
                // in two strips of the background texture from the double chest texture's top part.
                if (rows > 1 && rowLen < slotsPerRow)
                {
                    RenderUtils.drawTexturedRectBatched(x + rowLen * 18 + 7, y + row * 18 +  7, 7, 3, (slotsPerRow - rowLen) * 18, 9, zLevel, buffer);
                    RenderUtils.drawTexturedRectBatched(x + rowLen * 18 + 7, y + row * 18 + 16, 7, 3, (slotsPerRow - rowLen) * 18, 9, zLevel, buffer);
                }
            }
        }

        tessellator.draw();
    }

    public static void renderInventoryBackground27(int x, int y, int zLevel, BufferBuilder buffer, Minecraft mc)
    {
        RenderUtils.bindTexture(TEXTURE_SINGLE_CHEST);
        RenderUtils.drawTexturedRectBatched(x      , y     ,   0,   0,   7,  61, zLevel, buffer); // left (top)
        RenderUtils.drawTexturedRectBatched(x +   7, y     ,   7,   0, 169,   7, zLevel, buffer); // top (right)
        RenderUtils.drawTexturedRectBatched(x      , y + 61,   0, 159, 169,   7, zLevel, buffer); // bottom (left)
        RenderUtils.drawTexturedRectBatched(x + 169, y +  7, 169, 105,   7,  61, zLevel, buffer); // right (bottom)
        RenderUtils.drawTexturedRectBatched(x +   7, y +  7,   7,  17, 162,  54, zLevel, buffer); // middle
    }

    public static void renderInventoryBackground54(int x, int y, int zLevel, BufferBuilder buffer, Minecraft mc)
    {
        RenderUtils.bindTexture(TEXTURE_DOUBLE_CHEST);
        RenderUtils.drawTexturedRectBatched(x      , y      ,   0,   0,   7, 115, zLevel, buffer); // left (top)
        RenderUtils.drawTexturedRectBatched(x +   7, y      ,   7,   0, 169,   7, zLevel, buffer); // top (right)
        RenderUtils.drawTexturedRectBatched(x      , y + 115,   0, 215, 169,   7, zLevel, buffer); // bottom (left)
        RenderUtils.drawTexturedRectBatched(x + 169, y +   7, 169, 107,   7, 115, zLevel, buffer); // right (bottom)
        RenderUtils.drawTexturedRectBatched(x +   7, y +   7,   7,  17, 162, 108, zLevel, buffer); // middle
    }

    public static void renderEquipmentOverlayBackground(int x, int y, int zLevel, EntityLivingBase entity)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        RenderUtils.bindTexture(TEXTURE_DISPENSER);

        RenderUtils.drawTexturedRectBatched(x     , y     ,   0,   0, 50, 83, zLevel, buffer); // top-left (main part)
        RenderUtils.drawTexturedRectBatched(x + 50, y     , 173,   0,  3, 83, zLevel, buffer); // right edge top
        RenderUtils.drawTexturedRectBatched(x     , y + 83,   0, 163, 50,  3, zLevel, buffer); // bottom edge left
        RenderUtils.drawTexturedRectBatched(x + 50, y + 83, 173, 163,  3,  3, zLevel, buffer); // bottom right corner

        for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18)
        {
            RenderUtils.drawTexturedRectBatched(x + xOff, y + yOff, 61, 16, 18, 18, zLevel, buffer);
        }

        // Main hand and offhand
        RenderUtils.drawTexturedRectBatched(x + 28, y + 2 * 18 + 7, 61, 16, 18, 18, zLevel, buffer);
        RenderUtils.drawTexturedRectBatched(x + 28, y + 3 * 18 + 7, 61, 16, 18, 18, zLevel, buffer);

        tessellator.draw();

        RenderUtils.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        if (entity.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).isEmpty())
        {
            String texture = "minecraft:items/empty_armor_slot_shield";
            RenderUtils.renderSprite(x + 28 + 1, y + 3 * 18 + 7 + 1, 16, 16, zLevel, texture);
        }

        for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18)
        {
            final EntityEquipmentSlot eqSlot = ARMOR_EQUIPMENT_SLOTS[i];

            if (entity.getItemStackFromSlot(eqSlot).isEmpty())
            {
                String texture = ItemArmor.EMPTY_SLOT_NAMES[eqSlot.getIndex()];
                RenderUtils.renderSprite(x + xOff + 1, y + yOff + 1, 16, 16, zLevel, texture);
            }
        }
    }

    public static InventoryRenderType getInventoryType(IInventory inv)
    {
        if (inv instanceof TileEntityShulkerBox)
        {
            return InventoryRenderType.FIXED_27;
        }
        else if (inv instanceof InventoryLargeChest)
        {
            return InventoryRenderType.FIXED_54;
        }
        else if (inv instanceof TileEntityFurnace)
        {
            return InventoryRenderType.FURNACE;
        }
        else if (inv instanceof TileEntityBrewingStand)
        {
            return InventoryRenderType.BREWING_STAND;
        }
        else if (inv instanceof TileEntityDispenser) // this includes the Dropper as a sub class
        {
            return InventoryRenderType.DISPENSER;
        }
        else if (inv instanceof TileEntityHopper)
        {
            return InventoryRenderType.HOPPER;
        }
        else if (inv instanceof ContainerHorseChest)
        {
            return InventoryRenderType.HORSE;
        }
        else
        {
            return InventoryRenderType.GENERIC;
        }
    }

    public static InventoryRenderType getInventoryType(ItemStack stack)
    {
        Item item = stack.getItem();

        if (item instanceof ItemBlock)
        {
            Block block = ((ItemBlock) item).getBlock();

            if (block instanceof BlockShulkerBox || block instanceof BlockChest)
            {
                return InventoryRenderType.FIXED_27;
            }
            else if (block instanceof BlockFurnace)
            {
                return InventoryRenderType.FURNACE;
            }
            else if (block instanceof BlockDispenser) // this includes the Dropper as a sub class
            {
                return InventoryRenderType.DISPENSER;
            }
            else if (block instanceof BlockHopper)
            {
                return InventoryRenderType.HOPPER;
            }
        }
        else if (item == Items.BREWING_STAND)
        {
            return InventoryRenderType.BREWING_STAND;
        }

        return InventoryRenderType.GENERIC;
    }

    /**
     * Returns the instance of the shared/temporary properties instance,
     * with the values set for the type of inventory provided.
     * Don't hold on to the instance, as the values will mutate when this
     * method is called again!
     * @param type
     * @param totalSlots
     * @return
     */
    public static InventoryProperties getInventoryPropsTemp(InventoryRenderType type, int totalSlots)
    {
        INV_PROPS_TEMP.totalSlots = totalSlots;

        if (type == InventoryRenderType.FURNACE)
        {
            INV_PROPS_TEMP.slotsPerRow = 1;
            INV_PROPS_TEMP.slotOffsetX = 0;
            INV_PROPS_TEMP.slotOffsetY = 0;
            INV_PROPS_TEMP.width = 96;
            INV_PROPS_TEMP.height = 68;
        }
        else if (type == InventoryRenderType.BREWING_STAND)
        {
            INV_PROPS_TEMP.slotsPerRow = 9;
            INV_PROPS_TEMP.slotOffsetX = 0;
            INV_PROPS_TEMP.slotOffsetY = 0;
            INV_PROPS_TEMP.width = 127;
            INV_PROPS_TEMP.height = 72;
        }
        else if (type == InventoryRenderType.DISPENSER)
        {
            INV_PROPS_TEMP.slotsPerRow = 3;
            INV_PROPS_TEMP.slotOffsetX = 8;
            INV_PROPS_TEMP.slotOffsetY = 8;
            INV_PROPS_TEMP.width = 68;
            INV_PROPS_TEMP.height = 68;
        }
        else if (type == InventoryRenderType.HORSE)
        {
            INV_PROPS_TEMP.slotsPerRow = Math.max(1, totalSlots / 3);
            INV_PROPS_TEMP.slotOffsetX = 8;
            INV_PROPS_TEMP.slotOffsetY = 8;
            INV_PROPS_TEMP.width = totalSlots * 18 / 3 + 14;
            INV_PROPS_TEMP.height = 68;
        }
        else if (type == InventoryRenderType.HOPPER)
        {
            INV_PROPS_TEMP.slotsPerRow = 5;
            INV_PROPS_TEMP.slotOffsetX = 8;
            INV_PROPS_TEMP.slotOffsetY = 8;
            INV_PROPS_TEMP.width = 105;
            INV_PROPS_TEMP.height = 32;
        }
        else if (type == InventoryRenderType.VILLAGER)
        {
            INV_PROPS_TEMP.slotsPerRow = 2;
            INV_PROPS_TEMP.slotOffsetX = 8;
            INV_PROPS_TEMP.slotOffsetY = 8;
            INV_PROPS_TEMP.width = 50;
            INV_PROPS_TEMP.height = 86;
        }
        else
        {
            if (type == InventoryRenderType.FIXED_27)
            {
                totalSlots = 27;
            }
            else if (type == InventoryRenderType.FIXED_54)
            {
                totalSlots = 54;
            }

            INV_PROPS_TEMP.slotsPerRow = 9;
            INV_PROPS_TEMP.slotOffsetX = 8;
            INV_PROPS_TEMP.slotOffsetY = 8;
            int rows = (int) (Math.ceil((double) totalSlots / (double) INV_PROPS_TEMP.slotsPerRow));
            INV_PROPS_TEMP.width = Math.min(INV_PROPS_TEMP.slotsPerRow, totalSlots) * 18 + 14;
            INV_PROPS_TEMP.height = rows * 18 + 14;
        }

        return INV_PROPS_TEMP;
    }

    public static void renderInventoryStacks(InventoryRenderType type, IInventory inv, int startX, int startY, float zLevel,
            int slotsPerRow, int startSlot, int maxSlots, Minecraft mc)
    {
        if (type == InventoryRenderType.FURNACE)
        {
            ItemRenderUtils.renderStackAt(inv.getStackInSlot(0), startX +   8, startY +  8, zLevel, 1f, mc);
            ItemRenderUtils.renderStackAt(inv.getStackInSlot(1), startX +   8, startY + 44, zLevel, 1f, mc);
            ItemRenderUtils.renderStackAt(inv.getStackInSlot(2), startX +  68, startY + 26, zLevel, 1f, mc);
        }
        else if (type == InventoryRenderType.BREWING_STAND)
        {
            ItemRenderUtils.renderStackAt(inv.getStackInSlot(0), startX +  47, startY + 42, zLevel, 1f, mc);
            ItemRenderUtils.renderStackAt(inv.getStackInSlot(1), startX +  70, startY + 49, zLevel, 1f, mc);
            ItemRenderUtils.renderStackAt(inv.getStackInSlot(2), startX +  93, startY + 42, zLevel, 1f, mc);
            ItemRenderUtils.renderStackAt(inv.getStackInSlot(3), startX +  70, startY +  8, zLevel, 1f, mc);
            ItemRenderUtils.renderStackAt(inv.getStackInSlot(4), startX +   8, startY +  8, zLevel, 1f, mc);
        }
        else
        {
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
                        ItemRenderUtils.renderStackAt(stack, x, y, zLevel, 1f, mc);
                    }

                    x += 18;
                }

                x = startX;
                y += 18;
            }
        }
    }

    public static void renderEquipmentStacks(EntityLivingBase entity, int x, int y, float zLevel, Minecraft mc)
    {
        for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18)
        {
            final EntityEquipmentSlot eqSlot = ARMOR_EQUIPMENT_SLOTS[i];
            ItemStack stack = entity.getItemStackFromSlot(eqSlot);

            if (stack.isEmpty() == false)
            {
                ItemRenderUtils.renderStackAt(stack, x + xOff + 1, y + yOff + 1, zLevel, 1f, mc);
            }
        }

        ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);

        if (stack.isEmpty() == false)
        {
            ItemRenderUtils.renderStackAt(stack, x + 28, y + 2 * 18 + 7 + 1, zLevel, 1f, mc);
        }

        stack = entity.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);

        if (stack.isEmpty() == false)
        {
            ItemRenderUtils.renderStackAt(stack, x + 28, y + 3 * 18 + 7 + 1, zLevel, 1f, mc);
        }
    }

    public static void renderItemStacks(NonNullList<ItemStack> items, int startX, int startY, int slotsPerRow, int startSlot, int maxSlots, float zLevel, Minecraft mc)
    {
        final int slots = items.size();
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
                ItemStack stack = items.get(slot);

                if (stack.isEmpty() == false)
                {
                    ItemRenderUtils.renderStackAt(stack, x, y, zLevel, 1f, mc);
                }

                x += 18;
            }

            x = startX;
            y += 18;
        }
    }



    public static class InventoryProperties
    {
        public int totalSlots = 1;
        public int width = 176;
        public int height = 83;
        public int slotsPerRow = 9;
        public int slotOffsetX = 8;
        public int slotOffsetY = 8;
    }

    public enum InventoryRenderType
    {
        BREWING_STAND,
        DISPENSER,
        FURNACE,
        HOPPER,
        HORSE,
        FIXED_27,
        FIXED_54,
        VILLAGER,
        GENERIC;
    }
}
