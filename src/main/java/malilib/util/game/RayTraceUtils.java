package malilib.util.game;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import malilib.util.position.LayerRange;

public class RayTraceUtils
{
    /**
     * Get a ray trace from the point of view of the given entity (along its look vector)
     * @param world the world in which the ray trace is performed
     * @param entity the entity from whose view point the ray trace is performed
     * @param fluidHandling determines if the ray trace should hit fluid blocks
     * @param includeEntities determines if the ray trace should include entities, or only blocks
     * @param range the maximum distance to ray trace from the entity's eye position
     * @return the trace result, with type = MISS if the trace didn't hit anything
     */
    public static HitResult getRayTraceFromEntity(Level world, Entity entity,
                                                  RayTraceFluidHandling fluidHandling, boolean includeEntities, double range)
    {
        Vec3 eyesPos = entity.getEyePosition(1f);
        Vec3 rangedLook = entity.getViewVector(1f).scale(range);
        Vec3 lookEndPos = eyesPos.add(rangedLook);

        HitResult result = rayTraceBlocks(world, eyesPos, lookEndPos, fluidHandling, false, false, null, 1000);

        if (includeEntities)
        {
            AABB bb = entity.getBoundingBox()
                    .inflate(rangedLook.x, rangedLook.y, rangedLook.z).inflate(1d, 1d, 1d);
            List<Entity> list = world.getEntities(entity, bb);
            double closest = Double.MAX_VALUE;

            if (result != null && result.getType() == HitResult.Type.BLOCK)
            {
                closest = eyesPos.distanceToSqr(result.getLocation());
            }

            Entity targetEntity = null;
            Vec3 entityHitPos = null;

            for (Entity entityTmp : list)
            {
                bb = entityTmp.getBoundingBox();
                Optional<Vec3> hitPos = bb.clip(eyesPos, lookEndPos);

                if (hitPos.isPresent())
                {
                    Vec3 posTmp = hitPos.get();
                    double distance = eyesPos.distanceToSqr(posTmp);

                    if (distance < closest)
                    {
                        targetEntity = entityTmp;
                        entityHitPos = posTmp;
                        closest = distance;
                    }
                }
            }

            if (targetEntity != null)
            {
                result = new EntityHitResult(targetEntity, entityHitPos);
            }
        }

        if (result == null || eyesPos.distanceTo(result.getLocation()) > range)
        {
            result = BlockHitResult.miss(Vec3.ZERO, Direction.UP, BlockPos.ZERO);
        }

        return result;
    }

    /**
     * Ray trace to blocks along the given vector
     * @param world
     * @param start The start position of the trace
     * @param end The end position of the trace
     * @param fluidMode Whether or not to trace to fluids
     * @param ignoreNonCollidable If true, then blocks without a hard collision box are ignored
     * @param returnLastUncollidableBlock If true, then the last block position without a hard collision
     *                                    box is returned, if no other blocks were hit
     * @param layerRange The LayerRange within which to ray trace. Set to null if the trace should
     *                   not care about layer ranges.
     * @param maxSteps the maximum number of advance loops. Should be larger than the maximum
     *                 requested trace length in blocks.
     * @return the ray trace result, or null if the trace didn't hit any blocks
     */
    @Nullable
    public static HitResult rayTraceBlocks(Level world, Vec3 start, Vec3 end,
                                           RayTraceFluidHandling fluidMode, boolean ignoreNonCollidable,
                                           boolean returnLastUncollidableBlock, @Nullable LayerRange layerRange, int maxSteps)
    {
        return rayTraceBlocks(world, start, end, RayTraceCalculationData::checkRayCollision, fluidMode, BLOCK_FILTER_ANY,
                ignoreNonCollidable, returnLastUncollidableBlock, layerRange, maxSteps);
    }

    /**
     * Ray trace to blocks along the given vector
     * @param world
     * @param start The start position of the trace
     * @param end The end position of the trace
     * @param fluidMode Whether or not to trace to fluids
     * @param blockFilter A test to check if the block is valid for a hit result
     * @param ignoreNonCollidable If true, then blocks without a hard collision box are ignored
     * @param returnLastUncollidableBlock If true, then the last block position without a hard collision box is returned, if no other blocks were hit
     * @param layerRange The LayerRange within which to ray trace. Set to null if the trace should not care about layer ranges.
     * @param maxSteps the maximum number of advance loops. Should be larger than the maximum desired maximum ray trace length in blocks.
     * @return the ray trace result, or null if the trace didn't hit any blocks
     */
    @Nullable
    public static HitResult rayTraceBlocks(Level world,
                                           Vec3 start,
                                           Vec3 end,
                                           IRayPositionHandler handler,
                                           RayTraceFluidHandling fluidMode,
                                           Predicate<BlockState> blockFilter,
                                           boolean ignoreNonCollidable,
                                           boolean returnLastUncollidableBlock,
                                           @Nullable LayerRange layerRange,
                                           int maxSteps)
    {
        if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z) ||
            Double.isNaN(end.x) || Double.isNaN(end.y) || Double.isNaN(end.z))
        {
            return null;
        }

        RayTraceCalculationData data = new RayTraceCalculationData(start, end, fluidMode, blockFilter, layerRange);

        while (--maxSteps >= 0)
        {
            if (handler.handleRayTracePosition(data, world, ignoreNonCollidable))
            {
                //System.out.printf("checkCollision() - steps: %d, trace: %s\n", maxSteps, data.trace);
                return data.trace;
            }

            if (rayTraceAdvance(data))
            {
                //System.out.printf("rayTraceStep() - steps: %d, trace: %s\n", maxSteps, data.trace);
                break;
            }
        }

        if (returnLastUncollidableBlock)
        {
            Vec3 pos = new Vec3(data.currentX, data.currentY, data.currentZ);
            return BlockHitResult.miss(pos, data.facing, data.blockPosMutable.immutable());
        }

        return null;
    }

    public static boolean checkRayCollision(RayTraceCalculationData data, Level world, boolean ignoreNonCollidable)
    {
        if (data.isPositionWithinRange())
        {
            BlockState state = world.getBlockState(data.blockPosMutable);

            if (data.isValidBlock(state)  == false ||
                state.getMaterial() == Material.AIR ||
                (ignoreNonCollidable && state.getCollisionShape(world, data.blockPosMutable).isEmpty()) ||
                data.fluidMode.handled(state) == false)
            {
                return false;
            }

            VoxelShape blockShape = state.getShape(world, data.blockPosMutable);
            HitResult traceTmp = blockShape.clip(data.start, data.end, data.blockPosMutable);

            if (traceTmp != null)
            {
                data.trace = traceTmp;
                return true;
            }
        }

        return false;
    }

    public static boolean rayTraceAdvance(RayTraceCalculationData data)
    {
        boolean hasDistToEndX = true;
        boolean hasDistToEndY = true;
        boolean hasDistToEndZ = true;
        double nextX = 999.0D;
        double nextY = 999.0D;
        double nextZ = 999.0D;

        if (Double.isNaN(data.currentX) || Double.isNaN(data.currentY) || Double.isNaN(data.currentZ))
        {
            data.trace = null;
            return true;
        }

        if (data.blockX == data.endBlockX && data.blockY == data.endBlockY && data.blockZ == data.endBlockZ)
        {
            return true;
        }

        if (data.endBlockX > data.blockX)
        {
            nextX = data.blockX + 1.0D;
        }
        else if (data.endBlockX < data.blockX)
        {
            nextX = data.blockX + 0.0D;
        }
        else
        {
            hasDistToEndX = false;
        }

        if (data.endBlockY > data.blockY)
        {
            nextY = data.blockY + 1.0D;
        }
        else if (data.endBlockY < data.blockY)
        {
            nextY = data.blockY + 0.0D;
        }
        else
        {
            hasDistToEndY = false;
        }

        if (data.endBlockZ > data.blockZ)
        {
            nextZ = data.blockZ + 1.0D;
        }
        else if (data.endBlockZ < data.blockZ)
        {
            nextZ = data.blockZ + 0.0D;
        }
        else
        {
            hasDistToEndZ = false;
        }

        double relStepX = 999.0D;
        double relStepY = 999.0D;
        double relStepZ = 999.0D;
        double distToEndX = data.end.x - data.currentX;
        double distToEndY = data.end.y - data.currentY;
        double distToEndZ = data.end.z - data.currentZ;

        if (hasDistToEndX)
        {
            relStepX = (nextX - data.currentX) / distToEndX;
        }

        if (hasDistToEndY)
        {
            relStepY = (nextY - data.currentY) / distToEndY;
        }

        if (hasDistToEndZ)
        {
            relStepZ = (nextZ - data.currentZ) / distToEndZ;
        }

        if (relStepX == -0.0D)
        {
            relStepX = -1.0E-4D;
        }

        if (relStepY == -0.0D)
        {
            relStepY = -1.0E-4D;
        }

        if (relStepZ == -0.0D)
        {
            relStepZ = -1.0E-4D;
        }

        if (relStepX < relStepY && relStepX < relStepZ)
        {
            data.facing = data.endBlockX > data.blockX ? Direction.WEST : Direction.EAST;
            data.currentX = nextX;
            data.currentY += distToEndY * relStepX;
            data.currentZ += distToEndZ * relStepX;
        }
        else if (relStepY < relStepZ)
        {
            data.facing = data.endBlockY > data.blockY ? Direction.DOWN : Direction.UP;
            data.currentX += distToEndX * relStepY;
            data.currentY = nextY;
            data.currentZ += distToEndZ * relStepY;
        }
        else
        {
            data.facing = data.endBlockZ > data.blockZ ? Direction.NORTH : Direction.SOUTH;
            data.currentX += distToEndX * relStepZ;
            data.currentY += distToEndY * relStepZ;
            data.currentZ = nextZ;
        }

        int x = Mth.floor(data.currentX) - (data.facing == Direction.EAST ?  1 : 0);
        int y = Mth.floor(data.currentY) - (data.facing == Direction.UP ?    1 : 0);
        int z = Mth.floor(data.currentZ) - (data.facing == Direction.SOUTH ? 1 : 0);
        data.setBlockPos(x, y, z);

        return false;
    }

    public static class RayTraceCalculationData
    {
        @Nullable protected final LayerRange range;
        public final RayTraceFluidHandling fluidMode;
        public final Predicate<BlockState> blockFilter;
        public final BlockPos.MutableBlockPos blockPosMutable = new BlockPos.MutableBlockPos();
        public final Vec3 start;
        public final Vec3 end;
        public final int endBlockX;
        public final int endBlockY;
        public final int endBlockZ;
        public int blockX;
        public int blockY;
        public int blockZ;
        public double currentX;
        public double currentY;
        public double currentZ;
        public Direction facing;
        @Nullable public HitResult trace;

        public RayTraceCalculationData(Vec3 start,
                                       Vec3 end,
                                       RayTraceFluidHandling fluidMode,
                                       Predicate<BlockState> blockFilter,
                                       @Nullable LayerRange range)
        {
            this.start = start;
            this.end = end;
            this.fluidMode = fluidMode;
            this.blockFilter = blockFilter;
            this.range = range;
            this.currentX = start.x;
            this.currentY = start.y;
            this.currentZ = start.z;
            this.endBlockX = Mth.floor(end.x);
            this.endBlockY = Mth.floor(end.y);
            this.endBlockZ = Mth.floor(end.z);
            this.setBlockPos(Mth.floor(start.x), Mth.floor(start.y), Mth.floor(start.z));
        }

        public void setBlockPos(int x, int y, int z)
        {
            this.blockX = x;
            this.blockY = y;
            this.blockZ = z;
            this.blockPosMutable.set(this.blockX, this.blockY, this.blockZ);
        }

        public boolean isValidBlock(BlockState state)
        {
            return this.blockFilter.test(state);
        }

        public boolean isPositionWithinRange()
        {
            return this.range == null || this.range.isPositionWithinRange(this.blockX, this.blockY, this.blockZ);
        }

        public boolean checkRayCollision(Level world, boolean ignoreBlockWithoutBoundingBox)
        {
            if (this.isPositionWithinRange() == false)
            {
                return false;
            }

            BlockState state = world.getBlockState(this.blockPosMutable);

            if (state.getMaterial() != Material.AIR &&
                this.isValidBlock(state) &&
                (ignoreBlockWithoutBoundingBox == false ||
                 state.getCollisionShape(world, this.blockPosMutable).isEmpty() == false))
            {
                VoxelShape blockShape = state.getShape(world, this.blockPosMutable);
                FluidState fluidState = state.getFluidState();
                boolean blockCollidable = ! blockShape.isEmpty();
                boolean fluidCollidable = this.fluidMode.handled(state); // TODO 1.13+ port should this use the fluid state like it used to?

                if (blockCollidable || fluidCollidable)
                {
                    BlockHitResult trace = null;

                    if (blockCollidable)
                    {
                        trace = blockShape.clip(this.start, this.end, this.blockPosMutable);
                    }

                    if (trace == null && fluidCollidable)
                    {
                        trace = fluidState.getShape(world, this.blockPosMutable)
                                .clip(this.start, this.end, this.blockPosMutable);
                    }

                    if (trace != null)
                    {
                        this.trace = trace;
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static final Predicate<BlockState> BLOCK_FILTER_ANY = (state) -> true;
    public static final Predicate<BlockState> BLOCK_FILTER_NON_AIR = (state) -> state.getMaterial() != Material.AIR;

    public enum RayTraceFluidHandling
    {
        NONE((blockState) -> BlockUtils.isFluidBlock(blockState) == false),
        SOURCE_ONLY(BlockUtils::isFluidSourceBlock),
        ANY(BlockUtils::isFluidBlock);

        private final Predicate<BlockState> predicate;

        RayTraceFluidHandling(Predicate<BlockState> predicate)
        {
            this.predicate = predicate;
        }

        public boolean handled(BlockState blockState)
        {
            return this.predicate.test(blockState);
        }
     }

    public interface IRayPositionHandler
    {
        /**
         * A handler method, usually for checking for a collision at the given position along the ray trace
         * @return true if the ray should stop here and the current trace result from the RayTraceCalcsData should be returned
         */
        boolean handleRayTracePosition(RayTraceCalculationData data, Level world, boolean ignoreNonCollidable);
    }
}
