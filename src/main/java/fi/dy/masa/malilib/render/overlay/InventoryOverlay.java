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
import fi.dy.masa.malilib.render.ShapeRenderUtils;

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

    public static void renderInventoryBackground(InventoryRenderType type, int x, int y, int z, int slotsPerRow, int totalSlots, Minecraft mc)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        if (type == InventoryRenderType.FURNACE)
        {
            RenderUtils.bindTexture(TEXTURE_FURNACE);
            ShapeRenderUtils.renderTexturedRectangle(x     , y     , z,   0,   0,   4,  64, buffer); // left (top)
            ShapeRenderUtils.renderTexturedRectangle(x +  4, y     , z,  84,   0,  92,   4, buffer); // top (right)
            ShapeRenderUtils.renderTexturedRectangle(x     , y + 64, z,   0, 162,  92,   4, buffer); // bottom (left)
            ShapeRenderUtils.renderTexturedRectangle(x + 92, y +  4, z, 172, 102,   4,  64, buffer); // right (bottom)
            ShapeRenderUtils.renderTexturedRectangle(x +  4, y +  4, z,  52,  13,  88,  60, buffer); // middle
        }
        else if (type == InventoryRenderType.BREWING_STAND)
        {
            RenderUtils.bindTexture(TEXTURE_BREWING_STAND);
            ShapeRenderUtils.renderTexturedRectangle(x      , y     , z,   0,   0,   4,  68, buffer); // left (top)
            ShapeRenderUtils.renderTexturedRectangle(x +   4, y     , z,  63,   0, 113,   4, buffer); // top (right)
            ShapeRenderUtils.renderTexturedRectangle(x      , y + 68, z,   0, 162, 113,   4, buffer); // bottom (left)
            ShapeRenderUtils.renderTexturedRectangle(x + 113, y +  4, z, 172,  98,   4,  68, buffer); // right (bottom)
            ShapeRenderUtils.renderTexturedRectangle(x +   4, y +  4, z,  13,  13, 109,  64, buffer); // middle
        }
        else if (type == InventoryRenderType.DISPENSER)
        {
            RenderUtils.bindTexture(TEXTURE_DISPENSER);
            ShapeRenderUtils.renderTexturedRectangle(x     , y     , z,   0,   0,   7,  61, buffer); // left (top)
            ShapeRenderUtils.renderTexturedRectangle(x +  7, y     , z, 115,   0,  61,   7, buffer); // top (right)
            ShapeRenderUtils.renderTexturedRectangle(x     , y + 61, z,   0, 159,  61,   7, buffer); // bottom (left)
            ShapeRenderUtils.renderTexturedRectangle(x + 61, y +  7, z, 169, 105,   7,  61, buffer); // right (bottom)
            ShapeRenderUtils.renderTexturedRectangle(x +  7, y +  7, z,  61,  16,  54,  54, buffer); // middle
        }
        else if (type == InventoryRenderType.HOPPER)
        {
            RenderUtils.bindTexture(TEXTURE_HOPPER);
            ShapeRenderUtils.renderTexturedRectangle(x      , y     , z,   0,   0,   7,  25, buffer); // left (top)
            ShapeRenderUtils.renderTexturedRectangle(x +   7, y     , z,  79,   0,  97,   7, buffer); // top (right)
            ShapeRenderUtils.renderTexturedRectangle(x      , y + 25, z,   0, 126,  97,   7, buffer); // bottom (left)
            ShapeRenderUtils.renderTexturedRectangle(x +  97, y +  7, z, 169, 108,   7,  25, buffer); // right (bottom)
            ShapeRenderUtils.renderTexturedRectangle(x +   7, y +  7, z,  43,  19,  90,  18, buffer); // middle
        }
        // Most likely a Villager, or possibly a Llama
        else if (type == InventoryRenderType.VILLAGER)
        {
            RenderUtils.bindTexture(TEXTURE_DOUBLE_CHEST);
            ShapeRenderUtils.renderTexturedRectangle(x     , y     , z,   0,   0,   7,  79, buffer); // left (top)
            ShapeRenderUtils.renderTexturedRectangle(x +  7, y     , z, 133,   0,  43,   7, buffer); // top (right)
            ShapeRenderUtils.renderTexturedRectangle(x     , y + 79, z,   0, 215,  43,   7, buffer); // bottom (left)
            ShapeRenderUtils.renderTexturedRectangle(x + 43, y +  7, z, 169, 143,   7,  79, buffer); // right (bottom)
            ShapeRenderUtils.renderTexturedRectangle(x +  7, y +  7, z,   7,  17,  36,  72, buffer); // 2x4 slots
        }
        else if (type == InventoryRenderType.FIXED_27)
        {
            renderInventoryBackground27(x, y, z, buffer, mc);
        }
        else if (type == InventoryRenderType.FIXED_54)
        {
            renderInventoryBackground54(x, y, z, buffer, mc);
        }
        else
        {
            RenderUtils.bindTexture(TEXTURE_DOUBLE_CHEST);

            // Draw the slot backgrounds according to how many slots there actually are
            int rows = (int) (Math.ceil((double) totalSlots / (double) slotsPerRow));
            int bgw = Math.min(totalSlots, slotsPerRow) * 18 + 7;
            int bgh = rows * 18 + 7;

            ShapeRenderUtils.renderTexturedRectangle(x      , y      , z,         0,         0,   7, bgh, buffer); // left (top)
            ShapeRenderUtils.renderTexturedRectangle(x +   7, y      , z, 176 - bgw,         0, bgw,   7, buffer); // top (right)
            ShapeRenderUtils.renderTexturedRectangle(x      , y + bgh, z,         0,       215, bgw,   7, buffer); // bottom (left)
            ShapeRenderUtils.renderTexturedRectangle(x + bgw, y +   7, z,       169, 222 - bgh,   7, bgh, buffer); // right (bottom)

            for (int row = 0; row < rows; row++)
            {
                int rowLen = MathHelper.clamp(totalSlots - (row * slotsPerRow), 1, slotsPerRow);
                ShapeRenderUtils.renderTexturedRectangle(x + 7, y + row * 18 + 7, z, 7, 17, rowLen * 18, 18, buffer);

                // Render the background for the last non-existing slots on the last row,
                // in two strips of the background texture from the double chest texture's top part.
                if (rows > 1 && rowLen < slotsPerRow)
                {
                    ShapeRenderUtils.renderTexturedRectangle(x + rowLen * 18 + 7, y + row * 18 +  7, z, 7, 3, (slotsPerRow - rowLen) * 18, 9, buffer);
                    ShapeRenderUtils.renderTexturedRectangle(x + rowLen * 18 + 7, y + row * 18 + 16, z, 7, 3, (slotsPerRow - rowLen) * 18, 9, buffer);
                }
            }
        }

        tessellator.draw();
    }

    public static void renderInventoryBackground27(int x, int y, int z, BufferBuilder buffer, Minecraft mc)
    {
        RenderUtils.bindTexture(TEXTURE_SINGLE_CHEST);
        ShapeRenderUtils.renderTexturedRectangle(x      , y     , z,   0,   0,   7,  61, buffer); // left (top)
        ShapeRenderUtils.renderTexturedRectangle(x +   7, y     , z,   7,   0, 169,   7, buffer); // top (right)
        ShapeRenderUtils.renderTexturedRectangle(x      , y + 61, z,   0, 159, 169,   7, buffer); // bottom (left)
        ShapeRenderUtils.renderTexturedRectangle(x + 169, y +  7, z, 169, 105,   7,  61, buffer); // right (bottom)
        ShapeRenderUtils.renderTexturedRectangle(x +   7, y +  7, z,   7,  17, 162,  54, buffer); // middle
    }

    public static void renderInventoryBackground54(int x, int y, int z, BufferBuilder buffer, Minecraft mc)
    {
        RenderUtils.bindTexture(TEXTURE_DOUBLE_CHEST);
        ShapeRenderUtils.renderTexturedRectangle(x      , y      , z,   0,   0,   7, 115, buffer); // left (top)
        ShapeRenderUtils.renderTexturedRectangle(x +   7, y      , z,   7,   0, 169,   7, buffer); // top (right)
        ShapeRenderUtils.renderTexturedRectangle(x      , y + 115, z,   0, 215, 169,   7, buffer); // bottom (left)
        ShapeRenderUtils.renderTexturedRectangle(x + 169, y +   7, z, 169, 107,   7, 115, buffer); // right (bottom)
        ShapeRenderUtils.renderTexturedRectangle(x +   7, y +   7, z,   7,  17, 162, 108, buffer); // middle
    }

    public static void renderEquipmentOverlayBackground(int x, int y, int z, EntityLivingBase entity)
    {
        RenderUtils.color(1f, 1f, 1f, 1f);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        RenderUtils.bindTexture(TEXTURE_DISPENSER);

        ShapeRenderUtils.renderTexturedRectangle(x     , y     , z,   0,   0, 50, 83, buffer); // top-left (main part)
        ShapeRenderUtils.renderTexturedRectangle(x + 50, y     , z, 173,   0,  3, 83, buffer); // right edge top
        ShapeRenderUtils.renderTexturedRectangle(x     , y + 83, z,   0, 163, 50,  3, buffer); // bottom edge left
        ShapeRenderUtils.renderTexturedRectangle(x + 50, y + 83, z, 173, 163,  3,  3, buffer); // bottom right corner

        for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18)
        {
            ShapeRenderUtils.renderTexturedRectangle(x + xOff, y + yOff, z, 61, 16, 18, 18, buffer);
        }

        // Main hand and offhand
        ShapeRenderUtils.renderTexturedRectangle(x + 28, y + 2 * 18 + 7, z, 61, 16, 18, 18, buffer);
        ShapeRenderUtils.renderTexturedRectangle(x + 28, y + 3 * 18 + 7, z, 61, 16, 18, 18, buffer);

        tessellator.draw();

        RenderUtils.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        if (entity.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).isEmpty())
        {
            String texture = "minecraft:items/empty_armor_slot_shield";
            RenderUtils.renderSprite(x + 28 + 1, y + 3 * 18 + 7 + 1, z, 16, 16, texture);
        }

        for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18)
        {
            final EntityEquipmentSlot eqSlot = ARMOR_EQUIPMENT_SLOTS[i];

            if (entity.getItemStackFromSlot(eqSlot).isEmpty())
            {
                String texture = ItemArmor.EMPTY_SLOT_NAMES[eqSlot.getIndex()];
                RenderUtils.renderSprite(x + xOff + 1, y + yOff + 1, z, 16, 16, texture);
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

    public static void renderInventoryStacks(InventoryRenderType type, IInventory inv, int x, int y, float z,
                                             int slotsPerRow, int startSlot, int maxSlots, Minecraft mc)
    {
        if (type == InventoryRenderType.FURNACE)
        {
            ItemRenderUtils.renderStackAt(inv.getStackInSlot(0), x +   8, y +  8, z, 1f, mc);
            ItemRenderUtils.renderStackAt(inv.getStackInSlot(1), x +   8, y + 44, z, 1f, mc);
            ItemRenderUtils.renderStackAt(inv.getStackInSlot(2), x +  68, y + 26, z, 1f, mc);
        }
        else if (type == InventoryRenderType.BREWING_STAND)
        {
            ItemRenderUtils.renderStackAt(inv.getStackInSlot(0), x +  47, y + 42, z, 1f, mc);
            ItemRenderUtils.renderStackAt(inv.getStackInSlot(1), x +  70, y + 49, z, 1f, mc);
            ItemRenderUtils.renderStackAt(inv.getStackInSlot(2), x +  93, y + 42, z, 1f, mc);
            ItemRenderUtils.renderStackAt(inv.getStackInSlot(3), x +  70, y +  8, z, 1f, mc);
            ItemRenderUtils.renderStackAt(inv.getStackInSlot(4), x +   8, y +  8, z, 1f, mc);
        }
        else
        {
            final int slots = inv.getSizeInventory();
            int tmpX = x;
            int tmpY = y;

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
                        ItemRenderUtils.renderStackAt(stack, tmpX, tmpY, z, 1f, mc);
                    }

                    tmpX += 18;
                }

                tmpX = x;
                tmpY += 18;
            }
        }
    }

    public static void renderEquipmentStacks(EntityLivingBase entity, int x, int y, float z, Minecraft mc)
    {
        for (int i = 0, xOff = 7, yOff = 7; i < 4; ++i, yOff += 18)
        {
            final EntityEquipmentSlot eqSlot = ARMOR_EQUIPMENT_SLOTS[i];
            ItemStack stack = entity.getItemStackFromSlot(eqSlot);

            if (stack.isEmpty() == false)
            {
                ItemRenderUtils.renderStackAt(stack, x + xOff + 1, y + yOff + 1, z, 1f, mc);
            }
        }

        ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);

        if (stack.isEmpty() == false)
        {
            ItemRenderUtils.renderStackAt(stack, x + 28, y + 2 * 18 + 7 + 1, z, 1f, mc);
        }

        stack = entity.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);

        if (stack.isEmpty() == false)
        {
            ItemRenderUtils.renderStackAt(stack, x + 28, y + 3 * 18 + 7 + 1, z, 1f, mc);
        }
    }

    public static void renderItemStacks(NonNullList<ItemStack> items, int x, int y, float z, int slotsPerRow, int startSlot, int maxSlots, Minecraft mc)
    {
        final int slots = items.size();
        int tmpX = x;
        int tmpY = y;

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
                    ItemRenderUtils.renderStackAt(stack, tmpX, tmpY, z, 1f, mc);
                }

                tmpX += 18;
            }

            tmpX = x;
            tmpY += 18;
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
