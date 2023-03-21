package malilib.render.inventory;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import malilib.config.value.HorizontalAlignment;
import malilib.config.value.VerticalAlignment;
import malilib.gui.icon.DefaultIcons;
import malilib.gui.icon.Icon;
import malilib.gui.icon.PositionedIcon;
import malilib.gui.util.GuiUtils;
import malilib.mixin.access.AbstractHorseEntityMixin;
import malilib.render.ItemRenderUtils;
import malilib.render.RenderContext;
import malilib.render.RenderUtils;
import malilib.render.ShapeRenderUtils;
import malilib.util.game.RayTraceUtils;
import malilib.util.game.WorldUtils;
import malilib.util.game.wrap.GameUtils;
import malilib.util.game.wrap.ItemWrap;
import malilib.util.inventory.ColoredVanillaInventoryView;
import malilib.util.inventory.CombinedInventoryView;
import malilib.util.inventory.EquipmentInventoryView;
import malilib.util.inventory.InventoryView;
import malilib.util.inventory.StorageItemInventoryUtils;
import malilib.util.inventory.VanillaInventoryView;
import malilib.util.position.Vec2i;

public class InventoryRenderUtils
{
    /**
     * Renders all the slots from the given inventory that exist in the given customSlotPositions map,
     * at their indicated offsets from the base xy-coordinate.
     */
    public static void renderCustomPositionedSlots(int x, int y, float z, InventoryView inv,
                                                   Int2ObjectOpenHashMap<Vec2i> customSlotPositions,
                                                   RenderContext ctx)
    {
        final int invSize = inv.getSize();

        RenderUtils.enableGuiItemLighting();
        // TODO 1.13+ port
        //GlStateManager.enableDepth();
        //GlStateManager.enableRescaleNormal();

        for (int slot : customSlotPositions.keySet())
        {
            if (slot >= 0 && slot < invSize)
            {
                ItemStack stack = inv.getStack(slot);
                Vec2i pos = customSlotPositions.get(slot);

                if (ItemWrap.notEmpty(stack) && pos != null)
                {
                    ItemRenderUtils.renderStackAt(stack, x + pos.x, y + pos.y, z, 1f, ctx);
                }
            }
        }
    }

    /**
     * Renders the given inventory slot ranges from the given inventory,
     * at their relative offsets from the base xy-coordinate.
     */
    public static void renderInventoryRanges(int x, int y, float z, InventoryView inv,
                                             List<InventoryRange> inventoryRanges,
                                             int backgroundTintColor,
                                             RenderContext ctx)
    {
        for (InventoryRange range : inventoryRanges)
        {
            int slotsPerRow = range.slotsPerRowFunction.applyAsInt(inv.getSize());
            int slotCount = range.slotCount;
            Vec2i startPos = range.startPos;

            if (slotCount < 0)
            {
                slotCount = inv.getSize() - range.startSlot;
            }

            if (range.renderSlotBackgrounds && slotsPerRow > 0 && slotCount > 0)
            {
                int tx = x + startPos.x - 1;
                int ty = y + startPos.y - 1;

                renderDynamicInventoryEmptySlotBackgrounds(tx, ty, z, backgroundTintColor, slotsPerRow, slotCount, ctx);
            }

            renderGenericInventoryItems(x, y, z + 100f, range.startSlot, slotCount, slotsPerRow, startPos, inv, ctx);
        }
    }

    /**
     * Renders a dynamically sized inventory background texture for the given
     * inventory (slot count), with the given number of slots per row.
     * <br> Note that the maximum size is currently capped to 13x13 slots,
     * due to that being the maximum size of the original background image used.
     */
    public static void renderDynamicInventoryBackground(int x, int y, float z, int backgroundTintColor,
                                                        int slotsPerRow, int slotCount, RenderContext ctx)
    {
        if (slotCount <= 0 || slotsPerRow <= 0)
        {
            return;
        }

        slotsPerRow = Math.min(slotsPerRow, slotCount);
        slotsPerRow = Math.min(slotsPerRow, 13);

        Icon icon = DefaultIcons.INV_BACKGROUND_EMPTY_13_X_13;
        int rows = Math.min((int) Math.ceil((double) slotCount / slotsPerRow), 13);
        int w1 = slotsPerRow * 18 + 7;
        int h1 = rows * 18 + 7;
        int w2 = 7;
        int h2 = 7;

        int width = icon.getWidth();
        int height = icon.getHeight();
        int u = icon.getU();
        int v = icon.getV();
        float pw = icon.getTexturePixelWidth();
        float ph = icon.getTexturePixelHeight();

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.bindTexture(icon.getTexture());
        RenderUtils.setupBlend();

        // Main part (top left) with all the slots
        ShapeRenderUtils.renderScaledTintedTexturedRectangle(x, y, z, u, v, w1, h1,
                                                             w1, h1, pw, ph, backgroundTintColor);

        // The right edge strip
        ShapeRenderUtils.renderScaledTintedTexturedRectangle(x + w1, y, z, u + width - w2, v,
                                                             w2, h1, w2, h1, pw, ph, backgroundTintColor);

        // The bottom edge strip
        ShapeRenderUtils.renderScaledTintedTexturedRectangle(x, y + h1, z, u, v + height - h2,
                                                             w1, h2, w1, h2, pw, ph, backgroundTintColor);

        // The bottom right corner piece
        ShapeRenderUtils.renderScaledTintedTexturedRectangle(x + w1, y + h1, z, u + width - w2, v + height - h2,
                                                             w2, h2, w2, h2, pw, ph, backgroundTintColor);

        renderDynamicInventoryEmptySlotBackgrounds(x + 7, y + 7, z, backgroundTintColor, slotsPerRow, slotCount, ctx);
    }

    public static void renderDynamicInventoryEmptySlotBackgrounds(int x, int y, float z, int backgroundTintColor,
                                                                  int slotsPerRow, int slotCount, RenderContext ctx)
    {
        if (slotCount <= 0 || slotsPerRow <= 0)
        {
            return;
        }

        int fullWidthRows = Math.min(slotCount / slotsPerRow, 14);
        int totalRows = Math.min((int) Math.ceil((double) slotCount / slotsPerRow), 14);
        int lastRowSlots = slotCount % slotsPerRow;
        int xInc = 0;
        int yInc = 0;
        int w = 18;
        int h = 18;
        int loopCount;
        int color = backgroundTintColor;
        Icon icon;

        // More rows than slots per row -> use vertical columns instead of horizontal rows
        if (totalRows > slotsPerRow)
        {
            icon = DefaultIcons.INV_BACKGROUND_14_SLOTS_VERTICAL;
            loopCount = slotsPerRow;
            // The height of the columns to render is the number of full width rows,
            // ie. the continuous part of the inventory before the possible last partial row
            h = fullWidthRows * 18;
            xInc = 18;
        }
        else
        {
            icon = DefaultIcons.INV_BACKGROUND_14_SLOTS_HORIZONTAL;
            loopCount = fullWidthRows;
            w = slotsPerRow * 18;
            yInc = 18;
        }

        int tx = x;
        int ty = y;
        int u = icon.getU();
        int v = icon.getV();
        float pw = icon.getTexturePixelWidth();
        float ph = icon.getTexturePixelHeight();

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderUtils.disableItemLighting();
        RenderUtils.setupBlend();
        RenderUtils.bindTexture(icon.getTexture());

        for (int i = 0; i < loopCount; ++i)
        {
            ShapeRenderUtils.renderScaledTintedTexturedRectangle(tx, ty, z, u, v, w, h, w, h, pw, ph, color);
            tx += xInc;
            ty += yInc;
        }

        // There is one partial row at the bottom
        if (lastRowSlots > 0)
        {
            icon = DefaultIcons.INV_BACKGROUND_14_SLOTS_HORIZONTAL;
            ty = y + fullWidthRows * 18;
            w = lastRowSlots * 18;
            h = 18;
            u = icon.getU();
            v = icon.getV();
            pw = icon.getTexturePixelWidth();
            ph = icon.getTexturePixelHeight();

            RenderUtils.bindTexture(icon.getTexture());
            ShapeRenderUtils.renderScaledTintedTexturedRectangle(x, ty, z, u, v, w, h, w, h, pw, ph, color);
        }
    }

    /**
     * Renders an empty slot background image for the empty slots in the given inventory,
     * that exist in the give map of icons. The map index is the slot number.
     */
    public static void renderEmptySlotBackgrounds(int x, int y, float z, int backgroundTintColor, InventoryView inv,
                                                  Int2ObjectOpenHashMap<PositionedIcon> emptySlotTextures,
                                                  RenderContext ctx)
    {
        final int invSize = inv.getSize();

        for (Map.Entry<Integer, PositionedIcon> entry : emptySlotTextures.int2ObjectEntrySet())
        {
            int slotNum = entry.getKey();

            if (slotNum >= 0 && slotNum < invSize &&
                ItemWrap.isEmpty(inv.getStack(slotNum)))
            {
                PositionedIcon posIcon = entry.getValue();
                Vec2i position = posIcon.pos;
                Icon icon = posIcon.icon;
                int posX = x + position.x;
                int posY = y + position.y;

                if (backgroundTintColor == 0xFFFFFFFF)
                {
                    icon.renderAt(posX, posY, z);
                }
                else
                {
                    icon.renderTintedAt(posX, posY, z, backgroundTintColor);
                }
            }
        }
    }

    /**
     * Renders the items for a generic row-based inventory.
     * @param startSlot the slot number to start from
     * @param maxSlotCount maximum number of slots to render, starting from the startSlot.
     *                     -1 can be used for "all slots".
     *                     It's safe to give a number larger than the inventory size,
     *                     it will be clamped automatically.
     * @param slotsPerRow the number of slots to render per row
     * @param slotOffset the start offset of the first slot from the given base x and y coordinate.
     *                   Note that if startSlot is not 0, this is the offset of the first rendered slot,
     *                   not the would-be slot 0.
     * @param inv the inventory from which the slots are rendered
     */
    public static void renderGenericInventoryItems(int x, int y, float z, int startSlot, int maxSlotCount,
                                                   int slotsPerRow, Vec2i slotOffset, InventoryView inv,
                                                   RenderContext ctx)
    {
        final int invSize = inv.getSize();

        if (maxSlotCount < 0)
        {
            maxSlotCount = invSize;
        }

        maxSlotCount = Math.min(maxSlotCount, invSize - startSlot);

        if (startSlot < 0 || slotsPerRow <= 0 || maxSlotCount <= 0)
        {
            return;
        }

        final int endSlot = startSlot + maxSlotCount;
        final int startX = x + slotOffset.x;
        int slot = startSlot;

        x = startX;
        y += slotOffset.y;

        RenderUtils.enableGuiItemLighting();
        // TODO 1.13+ port
        //GlStateManager.enableDepth();
        //GlStateManager.enableRescaleNormal();

        for (int slotOnRow = 0; slot < endSlot; ++slot)
        {
            ItemStack stack = inv.getStack(slot);

            if (ItemWrap.notEmpty(stack))
            {
                ItemRenderUtils.renderStackAt(stack, x, y, z, 1f, ctx);
            }

            if (++slotOnRow >= slotsPerRow)
            {
                x = startX;
                y += 18;
                slotOnRow = 0;
            }
            else
            {
                x += 18;
            }
        }
    }

    public static void renderItemInventoryPreview(ItemStack stack, int baseX, int baseY, float z,
                                                  boolean useShulkerBackgroundColor, RenderContext ctx)
    {
        if (stack.hasTag())
        {
            int bgTintColor = 0xFFFFFFFF;

            if (useShulkerBackgroundColor && (stack.getItem() instanceof BlockItem) &&
                ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock)
            {
                ShulkerBoxBlock block = (ShulkerBoxBlock) ((BlockItem) stack.getItem()).getBlock();
                bgTintColor = getShulkerBoxBackgroundTintColor(block);
            }

            InventoryView inv = StorageItemInventoryUtils.getExactStoredItemsView(stack);

            if (inv == null || inv.getSize()  <= 0)
            {
                return;
            }

            InventoryRenderDefinition renderDefinition = InventoryRenderUtils.getInventoryType(stack);

            renderInventoryPreview(inv, renderDefinition, baseX, baseY, z, bgTintColor,
                                   HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM, ctx);
        }
    }

    public static void renderInventoryPreview(InventoryView inv,
                                              InventoryRenderDefinition renderDefinition,
                                              int baseX, int baseY, float z, int bgTintColor,
                                              HorizontalAlignment horizontalAlignment,
                                              VerticalAlignment verticalAlignment,
                                              RenderContext ctx)
    {
        int screenWidth = GuiUtils.getScaledWindowWidth();
        int screenHeight = GuiUtils.getScaledWindowHeight();
        int width = renderDefinition.getRenderWidth(inv);
        int height = renderDefinition.getRenderHeight(inv) + 8;
        int x = baseX + horizontalAlignment.getXStartOffsetForEdgeAlignment(width);
        int y = baseY + verticalAlignment.getYStartOffsetForEdgeAlignment(height);

        x = Mth.clamp(x, 0, screenWidth - width);
        y = Mth.clamp(y, 0, screenHeight - height);

        if (bgTintColor == 0xFFFFFFFF && inv instanceof ColoredVanillaInventoryView)
        {
            bgTintColor = ((ColoredVanillaInventoryView) inv).getBackgroundTintColor();
        }

        renderDefinition.renderInventory(x, y, z, bgTintColor, inv, ctx);
    }

    /**
     * @return the background tint color fo the given Shulker Box block
     */
    public static int getShulkerBoxBackgroundTintColor(@Nullable ShulkerBoxBlock block)
    {
        // In 1.13+ there is the separate uncolored Shulker Box variant,
        // which returns null from getColor().
        // In that case don't tint the background.
        DyeColor dye = block != null ? block.getColor() : null;
        final float[] colors = dye.getTextureDiffuseColors();
        return  0xFF000000 | (int) ((colors[0] * 0xFF)) << 16 | (int) ((colors[1] * 0xFF)) << 8 | (int) (colors[2] * 0xFF);
    }

    public static InventoryRenderDefinition getInventoryType(Container inv)
    {
        if (inv instanceof ShulkerBoxBlockEntity)
        {
            return BuiltinInventoryRenderDefinitions.GENERIC_27;
        }
        else if (inv instanceof CompoundContainer)
        {
            return BuiltinInventoryRenderDefinitions.GENERIC_54;
        }
        else if (inv instanceof AbstractFurnaceBlockEntity)
        {
            return BuiltinInventoryRenderDefinitions.FURNACE;
        }
        else if (inv instanceof BrewingStandBlockEntity)
        {
            return BuiltinInventoryRenderDefinitions.BREWING_STAND;
        }
        else if (inv instanceof DispenserBlockEntity) // this includes the Dropper as a sub class
        {
            return BuiltinInventoryRenderDefinitions.DROPPER;
        }
        else if (inv instanceof HopperBlockEntity)
        {
            return BuiltinInventoryRenderDefinitions.HOPPER;
        }
        else if (inv.getClass() == SimpleContainer.class) // FIXME
        {
            return BuiltinInventoryRenderDefinitions.HORSE;
        }
        else
        {
            return BuiltinInventoryRenderDefinitions.GENERIC;
        }
    }

    public static InventoryRenderDefinition getInventoryType(ItemStack stack)
    {
        Item item = stack.getItem();

        if (item instanceof BlockItem)
        {
            Block block = ((BlockItem) item).getBlock();

            if (block instanceof ShulkerBoxBlock || block instanceof ChestBlock)
            {
                return BuiltinInventoryRenderDefinitions.GENERIC_27;
            }
            else if (block instanceof AbstractFurnaceBlock)
            {
                return BuiltinInventoryRenderDefinitions.FURNACE;
            }
            else if (block instanceof DispenserBlock) // this includes the Dropper as a sub class
            {
                return BuiltinInventoryRenderDefinitions.DROPPER;
            }
            else if (block instanceof HopperBlock)
            {
                return BuiltinInventoryRenderDefinitions.HOPPER;
            }
        }
        else if (item == Items.BREWING_STAND)
        {
            return BuiltinInventoryRenderDefinitions.BREWING_STAND;
        }

        return BuiltinInventoryRenderDefinitions.GENERIC;
    }

    @Nullable
    public static Pair<InventoryView, InventoryRenderDefinition> getPointedInventory()
    {
        Level world = WorldUtils.getBestWorld();
        Player clientPlayer = GameUtils.getClientPlayer();

        // We need to get the player from the server world,
        // so that the player itself won't be included in the ray trace
        Player player = world.getPlayerByUUID(clientPlayer.getUUID());

        if (player == null)
        {
            player = clientPlayer;
        }

        RayTraceUtils.RayTraceFluidHandling fluidHandling = RayTraceUtils.RayTraceFluidHandling.NONE;
        HitResult trace = RayTraceUtils.getRayTraceFromEntity(world, player, fluidHandling, true, 6.0);

        if (trace == null)
        {
            return null;
        }

        if (trace.getType() == HitResult.Type.BLOCK)
        {
            BlockPos pos = ((BlockHitResult) trace).getBlockPos();
            return getInventoryViewFromBlock(pos, world);
        }
        else if (trace.getType() == HitResult.Type.ENTITY)
        {
            return getInventoryViewFromEntity(((EntityHitResult) trace).getEntity());
        }

        return null;
    }

    @Nullable
    public static Pair<InventoryView, InventoryRenderDefinition> getInventoryViewFromBlock(BlockPos pos, Level world)
    {
        @SuppressWarnings("deprecation")
        boolean isLoaded = world.hasChunkAt(pos);

        if (isLoaded == false)
        {
            return null;
        }

        // The method in World now checks that the caller is from the same thread...
        BlockEntity be = world.getChunkAt(pos).getBlockEntity(pos);

        if (be instanceof Container)
        {
            Container inv = (Container) be;
            BlockState state = world.getBlockState(pos);

            // Prevent loot generation attempt from crashing due to NPEs
            // TODO 1.13+ check if this is still needed
            /*
            if (te instanceof TileEntityLockableLoot && (world instanceof ServerWorld) == false)
            {
                ((TileEntityLockableLoot) te).setLootTable(null, 0);
            }
            */

            if (state.getBlock() instanceof ChestBlock && be instanceof ChestBlockEntity)
            {
                ChestType type = state.getValue(ChestBlock.TYPE);

                if (type != ChestType.SINGLE)
                {
                    BlockPos posAdj = pos.relative(ChestBlock.getConnectedDirection(state));
                    @SuppressWarnings("deprecation")
                    boolean isLoadedAdj = world.hasChunkAt(posAdj);

                    if (isLoadedAdj)
                    {
                        BlockState stateAdj = world.getBlockState(posAdj);
                        // The method in World now checks that the caller is from the same thread...
                        BlockEntity te2 = world.getChunkAt(posAdj).getBlockEntity(posAdj);

                        if (stateAdj.getBlock() == state.getBlock() &&
                                    te2 instanceof ChestBlockEntity &&
                                    stateAdj.getValue(ChestBlock.TYPE) != ChestType.SINGLE &&
                                    stateAdj.getValue(ChestBlock.FACING) == state.getValue(ChestBlock.FACING))
                        {
                            Container invRight = type == ChestType.RIGHT ?             inv : (Container) te2;
                            Container invLeft  = type == ChestType.RIGHT ? (Container) te2 :             inv;
                            inv = new CompoundContainer(invRight, invLeft);
                        }
                    }
                }
            }

            Block block = world.getBlockState(pos).getBlock();

            if (block instanceof ShulkerBoxBlock)
            {
                ShulkerBoxBlock shulkerBoxBlock = (ShulkerBoxBlock) block;
                int bgColor = InventoryRenderUtils.getShulkerBoxBackgroundTintColor(shulkerBoxBlock);
                return Pair.of(new ColoredVanillaInventoryView(inv, bgColor), getInventoryType(inv));
            }

            return Pair.of(new VanillaInventoryView(inv), getInventoryType(inv));
        }

        return null;
    }

    @Nullable
    public static Pair<InventoryView, InventoryRenderDefinition> getInventoryViewFromEntity(Entity entity)
    {
        if (entity instanceof Villager)
        {
            Container inv = ((Villager) entity).getInventory();
            InventoryView equipmentInv = new EquipmentInventoryView((Villager) entity);
            InventoryView mainInventory = new VanillaInventoryView(inv);

            return Pair.of(new CombinedInventoryView(equipmentInv, mainInventory),
                           BuiltinInventoryRenderDefinitions.VILLAGER);
        }
        else if (entity instanceof AbstractHorse)
        {
            Container inv = ((AbstractHorseEntityMixin) entity).malilib_getHorseChest();
            InventoryView equipmentInv = new EquipmentInventoryView((AbstractHorse) entity);
            InventoryView mainInventory = new VanillaInventoryView(inv);
            InventoryRenderDefinition def = (entity instanceof Llama) ?
                                                    BuiltinInventoryRenderDefinitions.LLAMA :
                                                    BuiltinInventoryRenderDefinitions.HORSE;

            return Pair.of(new CombinedInventoryView(equipmentInv, mainInventory), def);
        }
        else if (entity instanceof Container)
        {
            return Pair.of(new VanillaInventoryView((Container) entity),
                           BuiltinInventoryRenderDefinitions.GENERIC);
        }
        else if (entity instanceof LivingEntity)
        {
            return Pair.of(new EquipmentInventoryView((LivingEntity) entity),
                           BuiltinInventoryRenderDefinitions.LIVING_ENTITY);
        }

        return null;
    }
}
