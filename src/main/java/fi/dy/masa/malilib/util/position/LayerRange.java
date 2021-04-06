package fi.dy.masa.malilib.util.position;

import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;
import fi.dy.masa.malilib.config.value.LayerMode;
import fi.dy.masa.malilib.listener.LayerRangeChangeListener;
import fi.dy.masa.malilib.overlay.message.MessageUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.PositionUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.IntBoundingBox;

public class LayerRange
{
    public static final int WORLD_HORIZONTAL_SIZE_MAX =  30000000;
    public static final int WORLD_HORIZONTAL_SIZE_MIN = -30000000;
    public static final int WORLD_VERTICAL_SIZE_MAX = 255;
    public static final int WORLD_VERTICAL_SIZE_MIN = 0;

    protected final LayerRangeChangeListener refresher;
    protected LayerMode layerMode = LayerMode.ALL;
    protected EnumFacing.Axis axis = EnumFacing.Axis.Y;
    protected int layerSingle = 0;
    protected int layerAbove = 0;
    protected int layerBelow = 0;
    protected int layerRangeMin = 0;
    protected int layerRangeMax = 0;
    protected int playerFollowOffset = 0;
    protected boolean hotkeyRangeMin;
    protected boolean hotkeyRangeMax;
    protected boolean followPlayer;

    public LayerRange(LayerRangeChangeListener refresher)
    {
        this.refresher = refresher;
    }

    public LayerMode getLayerMode()
    {
        return this.layerMode;
    }

    public EnumFacing.Axis getAxis()
    {
        return this.axis;
    }

    public boolean getMoveLayerRangeMin()
    {
        return this.hotkeyRangeMin;
    }

    public boolean getMoveLayerRangeMax()
    {
        return this.hotkeyRangeMax;
    }

    public boolean shouldFollowPlayer()
    {
        return this.followPlayer;
    }

    public void toggleHotkeyMoveRangeMin()
    {
        this.hotkeyRangeMin = ! this.hotkeyRangeMin;
    }

    public void toggleHotkeyMoveRangeMax()
    {
        this.hotkeyRangeMax = ! this.hotkeyRangeMax;
    }

    public void toggleShouldFollowPlayer()
    {
        this.followPlayer = ! this.followPlayer;
    }

    public int getPlayerFollowOffset()
    {
        return this.playerFollowOffset;
    }

    public int getLayerSingle()
    {
        return this.layerSingle;
    }

    public int getLayerAbove()
    {
        return this.layerAbove;
    }

    public int getLayerBelow()
    {
        return this.layerBelow;
    }

    public int getLayerRangeMin()
    {
        return this.layerRangeMin;
    }

    public int getLayerRangeMax()
    {
        return this.layerRangeMax;
    }

    public int getLayerMin()
    {
        switch (this.layerMode)
        {
            case ALL:
            case ALL_BELOW:
                return getWorldMinValueForAxis(this.axis);
            case SINGLE_LAYER:
                return this.layerSingle;
            case ALL_ABOVE:
                return this.layerAbove;
            case LAYER_RANGE:
                return this.layerRangeMin;
            default:
                return WORLD_HORIZONTAL_SIZE_MIN;
        }
    }

    public int getLayerMax()
    {
        switch (this.layerMode)
        {
            case ALL:
            case ALL_ABOVE:
                return getWorldMaxValueForAxis(this.axis);
            case SINGLE_LAYER:
                return this.layerSingle;
            case ALL_BELOW:
                return this.layerBelow;
            case LAYER_RANGE:
                return this.layerRangeMax;
            default:
                return WORLD_HORIZONTAL_SIZE_MAX;
        }
    }

    public int getCurrentLayerValue(boolean isSecondValue)
    {
        switch (this.layerMode)
        {
            case SINGLE_LAYER:
                return this.layerSingle;

            case ALL_ABOVE:
                return this.layerAbove;

            case ALL_BELOW:
                return this.layerBelow;

            case LAYER_RANGE:
                return isSecondValue ? this.layerRangeMax : this.layerRangeMin;

            default:
                return 0;
        }
    }

    public static int getWorldMinValueForAxis(EnumFacing.Axis axis)
    {
        switch (axis)
        {
            case Y:
                return WORLD_VERTICAL_SIZE_MIN;
            case X:
            case Z:
            default:
                return WORLD_HORIZONTAL_SIZE_MIN;
        }
    }

    public static int getWorldMaxValueForAxis(EnumFacing.Axis axis)
    {
        switch (axis)
        {
            case Y:
                return WORLD_VERTICAL_SIZE_MAX;
            case X:
            case Z:
            default:
                return WORLD_HORIZONTAL_SIZE_MAX;
        }
    }

    public void setLayerMode(LayerMode mode)
    {
        this.setLayerMode(mode, true);
    }

    public void setLayerMode(LayerMode mode, boolean printMessage)
    {
        this.layerMode = mode;

        this.refresher.updateAll();

        if (printMessage)
        {
            MessageUtils.printCustomActionbarMessage("malilib.message.set_layer_mode_to", mode.getDisplayName());
        }
    }

    public void setAxis(EnumFacing.Axis axis)
    {
        this.axis = axis;

        this.refresher.updateAll();
        MessageUtils.printCustomActionbarMessage("malilib.message.set_layer_axis_to", axis.getName());
    }

    public void setPlayerFollowOffset(int offset)
    {
        this.playerFollowOffset = offset;
    }

    public void setLayerSingle(int layer)
    {
        int old = this.layerSingle;
        layer = this.getWorldLimitsClampedValue(layer);

        if (layer != old)
        {
            this.markAffectedLayersForRenderUpdate();
            this.layerSingle = layer;
            this.markAffectedLayersForRenderUpdate();
        }
    }

    public void setLayerAbove(int layer)
    {
        int old = this.layerAbove;
        layer = this.getWorldLimitsClampedValue(layer);

        if (layer != old)
        {
            this.layerAbove = layer;
            this.updateLayersBetween(old, layer);
        }
    }

    public void setLayerBelow(int layer)
    {
        int old = this.layerBelow;
        layer = this.getWorldLimitsClampedValue(layer);

        if (layer != old)
        {
            this.layerBelow = layer;
            this.updateLayersBetween(old, layer);
        }
    }

    public boolean setLayerRangeMin(int layer)
    {
        return this.setLayerRangeMin(layer, false);
    }

    public boolean setLayerRangeMax(int layer)
    {
        return this.setLayerRangeMax(layer, false);
    }

    protected boolean setLayerRangeMin(int layer, boolean force)
    {
        int old = this.layerRangeMin;
        layer = this.getWorldLimitsClampedValue(layer);

        if (force == false)
        {
            layer = Math.min(layer, this.layerRangeMax);
        }

        if (layer != old)
        {
            this.layerRangeMin = layer;
            this.updateLayersBetween(old, layer);
        }

        return layer != old;
    }

    protected boolean setLayerRangeMax(int layer, boolean force)
    {
        int old = this.layerRangeMax;
        layer = this.getWorldLimitsClampedValue(layer);

        if (force == false)
        {
            layer = Math.max(layer, this.layerRangeMin);
        }

        if (layer != old)
        {
            this.layerRangeMax = layer;
            this.updateLayersBetween(old, layer);
        }

        return layer != old;
    }

    protected int getPositionFromEntity(Entity entity)
    {
        int pos = 0;

        switch (this.axis)
        {
            case X:
                pos = MathHelper.floor(entity.posX);
                break;
            case Y:
                pos = MathHelper.floor(entity.posY);
                break;
            case Z:
                pos = MathHelper.floor(entity.posZ);
                break;
        }

        return pos;
    }

    public void setSingleBoundaryToPosition(Entity entity)
    {
        int pos = this.getPositionFromEntity(entity);
        this.setSingleBoundaryToPosition(pos);
    }

    protected void setSingleBoundaryToPosition(int pos)
    {
        switch (this.layerMode)
        {
            case SINGLE_LAYER:
                this.setLayerSingle(pos);
                break;
            case ALL_ABOVE:
                this.setLayerAbove(pos);
                break;
            case ALL_BELOW:
                this.setLayerBelow(pos);
                break;
            default:
        }
    }

    public void followPlayerIfEnabled(Entity entity)
    {
        if (this.followPlayer)
        {
            int newPos = this.getPositionFromEntity(entity) + this.playerFollowOffset;

            if (this.layerMode == LayerMode.LAYER_RANGE)
            {
                int rangeSize = this.layerRangeMax - this.layerRangeMin;

                if (this.layerRangeIsMinClosest(entity))
                {
                    this.setLayerRangeMax(newPos + rangeSize, true);
                    this.setLayerRangeMin(newPos, true);
                }
                else
                {
                    this.setLayerRangeMin(newPos - rangeSize, true);
                    this.setLayerRangeMax(newPos, true);
                }
            }
            else
            {
                this.setSingleBoundaryToPosition(newPos);
            }
        }
    }

    protected void markAffectedLayersForRenderUpdate()
    {
        int val1;
        int val2;

        switch (this.layerMode)
        {
            case ALL:
                this.refresher.updateAll();
                return;
            case SINGLE_LAYER:
            {
                val1 = this.layerSingle;
                val2 = this.layerSingle;
                break;
            }
            case ALL_ABOVE:
            {
                val1 = this.layerAbove;
                val2 = this.axis == EnumFacing.Axis.Y ? WORLD_VERTICAL_SIZE_MAX : WORLD_HORIZONTAL_SIZE_MAX;
                break;
            }
            case ALL_BELOW:
            {
                val1 = this.axis == EnumFacing.Axis.Y ? WORLD_VERTICAL_SIZE_MIN : WORLD_HORIZONTAL_SIZE_MIN;
                val2 = this.layerBelow;
                break;
            }
            case LAYER_RANGE:
            {
                val1 = this.layerRangeMin;
                val2 = this.layerRangeMax;
                break;
            }
            default:
                return;
        }

        this.updateLayersBetween(val1, val2);
    }

    protected void updateLayersBetween(int layer1, int layer2)
    {
        int layerMin = Math.min(layer1, layer2);
        int layerMax = Math.max(layer1, layer2);

        switch (this.axis)
        {
            case X:
                this.refresher.updateBetweenX(layerMin, layerMax);
                break;
            case Y:
                this.refresher.updateBetweenY(layerMin, layerMax);
                break;
            case Z:
                this.refresher.updateBetweenZ(layerMin, layerMax);
                break;
        }
    }

    public boolean moveLayer(int amount)
    {
        String axisName = this.axis.getName().toLowerCase();

        switch (this.layerMode)
        {
            case ALL:
                return false;
            case SINGLE_LAYER:
            {
                this.setLayerSingle(this.layerSingle + amount);
                MessageUtils.printCustomActionbarMessage("malilib.message.set_layer_to", axisName, this.layerSingle);
                break;
            }
            case ALL_ABOVE:
            {
                this.setLayerAbove(this.layerAbove + amount);
                MessageUtils.printCustomActionbarMessage("malilib.message.moved_min_layer_to", axisName, this.layerAbove);
                break;
            }
            case ALL_BELOW:
            {
                this.setLayerBelow(this.layerBelow + amount);
                MessageUtils.printCustomActionbarMessage("malilib.message.moved_max_layer_to", axisName, this.layerBelow);
                break;
            }
            case LAYER_RANGE:
            {
                EntityPlayer player = Minecraft.getMinecraft().player;

                if (player != null)
                {
                    this.moveLayerRange(amount, player);
                }

                break;
            }
            default:
        }

        return true;
    }

    protected void moveLayerRange(int amount, EntityPlayer player)
    {
        Pair<Boolean, Boolean> moveMinMax = this.getMoveMinMax(player);
        boolean moveMin = moveMinMax.getLeft();
        boolean moveMax = moveMinMax.getRight();
        boolean moved = false;
        boolean force = moveMin && moveMax;

        if (moveMin)
        {
            moved |= this.setLayerRangeMin(this.layerRangeMin + amount, force);
        }

        if (moveMax)
        {
            moved |= this.setLayerRangeMax(this.layerRangeMax + amount, force);
        }

        if (moved)
        {
            String axisName = this.axis.getName().toLowerCase();

            if (moveMin && moveMax)
            {
                MessageUtils.printCustomActionbarMessage("malilib.message.moved_layer_range", String.valueOf(amount), axisName);
            }
            else
            {
                String val1 = StringUtils.translate(moveMin ? "malilib.message.layer_range.range_min" : "malilib.message.layer_range.range_max");
                MessageUtils.printCustomActionbarMessage("malilib.message.moved_layer_range_boundary", val1, String.valueOf(amount), axisName);
            }
        }
    }

    protected Pair<Boolean, Boolean> getMoveMinMax(Entity entity)
    {
        boolean minClosest = this.layerRangeIsMinClosest(entity);
        boolean moveMin = this.hotkeyRangeMin || (minClosest          && this.hotkeyRangeMax == false);
        boolean moveMax = this.hotkeyRangeMax || (minClosest == false && this.hotkeyRangeMin == false);

        return Pair.of(moveMin,  moveMax);
    }

    protected boolean layerRangeIsMinClosest(Entity entity)
    {
        double playerPos = this.axis == Axis.Y ? entity.posY : (this.axis == Axis.X ? entity.posX : entity.posZ);
        double min = this.layerRangeMin + 0.5D;
        double max = this.layerRangeMax + 0.5D;

        return playerPos < min || (Math.abs(playerPos - min) < Math.abs(playerPos - max));
    }

    public String getCurrentLayerString()
    {
        switch (this.layerMode)
        {
            case SINGLE_LAYER:
                return String.valueOf(this.layerSingle);

            case ALL_ABOVE:
                return String.valueOf(this.layerAbove);

            case ALL_BELOW:
                return String.valueOf(this.layerBelow);

            case LAYER_RANGE:
                return String.format("%d ... %s", this.layerRangeMin, this.layerRangeMax);

            default:
                return "";
        }
    }

    protected int getWorldLimitsClampedValue(int value)
    {
        if (this.axis == EnumFacing.Axis.Y)
        {
            return MathHelper.clamp(value, WORLD_VERTICAL_SIZE_MIN, WORLD_VERTICAL_SIZE_MAX);
        }
        else
        {
            return MathHelper.clamp(value, WORLD_HORIZONTAL_SIZE_MIN, WORLD_HORIZONTAL_SIZE_MAX);
        }
    }

    public boolean isPositionWithinRange(BlockPos pos)
    {
        return this.isPositionWithinRange(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean isPositionWithinRange(int x, int y, int z)
    {
        switch (this.layerMode)
        {
            case ALL:
                return true;

            case SINGLE_LAYER:
            {
                switch (this.axis)
                {
                    case X:
                        return x == this.layerSingle;
                    case Y:
                        return y == this.layerSingle;
                    case Z:
                        return z == this.layerSingle;
                }

                break;
            }

            case ALL_ABOVE:
            {
                switch (this.axis)
                {
                    case X:
                        return x >= this.layerAbove;
                    case Y:
                        return y >= this.layerAbove;
                    case Z:
                        return z >= this.layerAbove;
                }

                break;
            }

            case ALL_BELOW:
            {
                switch (this.axis)
                {
                    case X:
                        return x <= this.layerBelow;
                    case Y:
                        return y <= this.layerBelow;
                    case Z:
                        return z <= this.layerBelow;
                }

                break;
            }

            case LAYER_RANGE:
            {
                switch (this.axis)
                {
                    case X:
                        return x >= this.layerRangeMin && x <= this.layerRangeMax;
                    case Y:
                        return y >= this.layerRangeMin && y <= this.layerRangeMax;
                    case Z:
                        return z >= this.layerRangeMin && z <= this.layerRangeMax;
                }

                break;
            }
        }

        return false;
    }

    public boolean isPositionAtRenderEdgeOnSide(BlockPos pos, EnumFacing side)
    {
        switch (this.axis)
        {
            case X:
                return (side == EnumFacing.WEST && pos.getX() == this.getLayerMin()) || (side == EnumFacing.EAST && pos.getX() == this.getLayerMax());
            case Y:
                return (side == EnumFacing.DOWN && pos.getY() == this.getLayerMin()) || (side == EnumFacing.UP && pos.getY() == this.getLayerMax());
            case Z:
                return (side == EnumFacing.NORTH && pos.getZ() == this.getLayerMin()) || (side == EnumFacing.SOUTH && pos.getZ() == this.getLayerMax());
            default:
                return false;
        }
    }

    public boolean intersects(SubChunkPos pos)
    {
        switch (this.axis)
        {
            case X:
            {
                final int xMin = (pos.getX() << 4);
                final int xMax = (pos.getX() << 4) + 15;
                return (xMax < this.getLayerMin() || xMin > this.getLayerMax()) == false;
            }
            case Y:
            {
                final int yMin = (pos.getY() << 4);
                final int yMax = (pos.getY() << 4) + 15;
                return (yMax < this.getLayerMin() || yMin > this.getLayerMax()) == false;
            }
            case Z:
            {
                final int zMin = (pos.getZ() << 4);
                final int zMax = (pos.getZ() << 4) + 15;
                return (zMax < this.getLayerMin() || zMin > this.getLayerMax()) == false;
            }
            default:
                return false;
        }
    }

    public boolean intersects(IntBoundingBox box)
    {
        return this.intersectsBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    public boolean intersectsBox(BlockPos pos1, BlockPos pos2)
    {
        BlockPos posMin = PositionUtils.getMinCorner(pos1, pos2);
        BlockPos posMax = PositionUtils.getMaxCorner(pos1, pos2);
        return this.intersectsBox(posMin.getX(), posMin.getY(), posMin.getZ(), posMax.getX(), posMax.getY(), posMax.getZ());
    }

    public boolean intersectsBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
    {
        switch (this.axis)
        {
            case X:
            {
                return (maxX < this.getLayerMin() || minX > this.getLayerMax()) == false;
            }
            case Y:
            {
                return (maxY < this.getLayerMin() || minY > this.getLayerMax()) == false;
            }
            case Z:
            {
                return (maxZ < this.getLayerMin() || minZ > this.getLayerMax()) == false;
            }
            default:
                return false;
        }
    }

    public int getClampedValue(int value, EnumFacing.Axis type)
    {
        if (this.axis == type)
        {
            return MathHelper.clamp(value, this.getLayerMin(), this.getLayerMax());
        }

        return MathHelper.clamp(value, getWorldMinValueForAxis(type), getWorldMaxValueForAxis(type));
    }

    /**
     * Clamps the given box to the layer range bounds.
     * @param box
     * @return the clamped box, or null, if the range does not intersect the original box
     */
    @Nullable
    public IntBoundingBox getClampedBox(IntBoundingBox box)
    {
        return this.getClampedArea(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    /**
     * Clamps the given box to the layer range bounds.
     * @return the clamped box, or null, if the range does not intersect the original box
     */
    @Nullable
    public IntBoundingBox getClampedArea(BlockPos posMin, BlockPos posMax)
    {
        return this.getClampedArea(posMin.getX(), posMin.getY(), posMin.getZ(), posMax.getX(), posMax.getY(), posMax.getZ());
    }

    /**
     * Clamps the given box to the layer range bounds.
     * @return the clamped box, or null, if the range does not intersect the original box
     */
    @Nullable
    public IntBoundingBox getClampedArea(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
    {
        if (this.intersectsBox(minX, minY, minZ, maxX, maxY, maxZ) == false)
        {
            return null;
        }

        switch (this.axis)
        {
            case X:
            {
                final int clampedMinX = Math.max(minX, this.getLayerMin());
                final int clampedMaxX = Math.min(maxX, this.getLayerMax());
                return IntBoundingBox.createProper(clampedMinX, minY, minZ, clampedMaxX, maxY, maxZ);
            }
            case Y:
            {
                final int clampedMinY = Math.max(minY, this.getLayerMin());
                final int clampedMaxY = Math.min(maxY, this.getLayerMax());
                return IntBoundingBox.createProper(minX, clampedMinY, minZ, maxX, clampedMaxY, maxZ);
            }
            case Z:
            {
                final int clampedMinZ = Math.max(minZ, this.getLayerMin());
                final int clampedMaxZ = Math.min(maxZ, this.getLayerMax());
                return IntBoundingBox.createProper(minX, minY, clampedMinZ, maxX, maxY, clampedMaxZ);
            }
            default:
                return null;
        }
    }

    public LayerRange copy()
    {
        LayerRange newRange = new LayerRange(this.refresher);

        newRange.layerMode = this.layerMode;
        newRange.axis = this.axis;
        newRange.layerSingle = this.layerSingle;
        newRange.layerAbove = this.layerAbove;
        newRange.layerBelow = this.layerBelow;
        newRange.layerRangeMin = this.layerRangeMin;
        newRange.layerRangeMax = this.layerRangeMax;
        newRange.hotkeyRangeMin = this.hotkeyRangeMin;
        newRange.hotkeyRangeMax = this.hotkeyRangeMax;

        return newRange;
    }

    public JsonObject toJson()
    {
        JsonObject obj = new JsonObject();

        obj.add("mode", new JsonPrimitive(this.layerMode.name()));
        obj.add("axis", new JsonPrimitive(this.axis.name()));
        obj.add("follow_player", new JsonPrimitive(this.followPlayer));
        obj.add("layer_single", new JsonPrimitive(this.layerSingle));
        obj.add("layer_above", new JsonPrimitive(this.layerAbove));
        obj.add("layer_below", new JsonPrimitive(this.layerBelow));
        obj.add("layer_range_min", new JsonPrimitive(this.layerRangeMin));
        obj.add("layer_range_max", new JsonPrimitive(this.layerRangeMax));
        obj.add("player_follow_offset", new JsonPrimitive(this.playerFollowOffset));
        obj.add("hotkey_range_min", new JsonPrimitive(this.hotkeyRangeMin));
        obj.add("hotkey_range_max", new JsonPrimitive(this.hotkeyRangeMax));

        return obj;
    }

    public static LayerRange createFromJson(JsonObject obj, LayerRangeChangeListener refresher)
    {
        LayerRange range = new LayerRange(refresher);
        range.fromJson(obj);
        return range;
    }

    public void fromJson(JsonObject obj)
    {
        this.layerMode = BaseOptionListConfigValue.findValueByName(JsonUtils.getString(obj, "mode"), LayerMode.VALUES);
        this.axis = EnumFacing.Axis.byName(JsonUtils.getString(obj, "axis"));
        if (this.axis == null) { this.axis = EnumFacing.Axis.Y; }

        this.followPlayer = JsonUtils.getBoolean(obj, "follow_player");
        this.layerSingle = JsonUtils.getInteger(obj, "layer_single");
        this.layerAbove = JsonUtils.getInteger(obj, "layer_above");
        this.layerBelow = JsonUtils.getInteger(obj, "layer_below");
        this.layerRangeMin = JsonUtils.getInteger(obj, "layer_range_min");
        this.layerRangeMax = JsonUtils.getInteger(obj, "layer_range_max");
        this.playerFollowOffset = JsonUtils.getInteger(obj, "player_follow_offset");
        this.hotkeyRangeMin = JsonUtils.getBoolean(obj, "hotkey_range_min");
        this.hotkeyRangeMax = JsonUtils.getBoolean(obj, "hotkey_range_max");
    }
}
