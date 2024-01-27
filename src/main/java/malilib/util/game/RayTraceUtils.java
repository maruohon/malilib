package malilib.util.game;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import malilib.util.MathUtils;
import malilib.util.game.wrap.EntityWrap;
import malilib.util.position.BlockPos;
import malilib.util.position.Direction;
import malilib.util.position.HitResult;
import malilib.util.position.LayerRange;
import malilib.util.position.Vec3d;

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
    public static HitResult getRayTraceFromEntity(World world, Entity entity,
                                                  RayTraceFluidHandling fluidHandling, boolean includeEntities, double range)
    {
        Vec3d eyesPos = new Vec3d(entity.x, entity.y, entity.z);
        Vec3d rangedLook = MathUtils.getRotationVector(EntityWrap.getYaw(entity), EntityWrap.getPitch(entity)).scale(range);
        Vec3d lookEndPos = new Vec3d(entity.x + rangedLook.x, entity.y +rangedLook.y, entity.z + rangedLook.z);

        HitResult result = rayTraceBlocks(world, eyesPos, lookEndPos, fluidHandling, false, false, null, 1000);

        if (includeEntities)
        {
            Box bb = entity.shape
                                .expand(rangedLook.x, rangedLook.y, rangedLook.z).expand(1.0, 1.0, 1.0);
            @SuppressWarnings("unchecked")
            List<Entity> list = (List<Entity>) world.getEntities(entity, bb);

            double closest = result != null && result.type == HitResult.Type.BLOCK ?
                                     eyesPos.squareDistanceTo(result.pos) : Double.MAX_VALUE;
            net.minecraft.world.HitResult entityTrace = null;
            Entity targetEntity = null;

            for (Entity entityTmp : list)
            {
                bb = entityTmp.shape;
                net.minecraft.world.HitResult traceTmp = bb.clip(eyesPos.toVanilla(), lookEndPos.toVanilla());

                if (traceTmp != null)
                {
                    double distance = eyesPos.squareDistanceTo(traceTmp.x, traceTmp.y, traceTmp.z);

                    if (distance < closest)
                    {
                        targetEntity = entityTmp;
                        entityTrace = traceTmp;
                        closest = distance;
                    }
                }
            }

            if (targetEntity != null)
            {
                result = HitResult.entity(targetEntity, new Vec3d(entityTrace.x, entityTrace.y, entityTrace.z));
            }
        }

        if (result == null || eyesPos.distanceTo(result.pos) > range)
        {
            result = HitResult.miss();
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
    public static HitResult rayTraceBlocks(World world, Vec3d start, Vec3d end,
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
     * @param fluidMode Whether to trace to fluids
     * @param blockFilter A test to check if the block is valid for a hit result
     * @param ignoreNonCollidable If true, then blocks without a hard collision box are ignored
     * @param returnLastUncollidableBlock If true, then the last block position without a hard collision box is returned, if no other blocks were hit
     * @param layerRange The LayerRange within which to ray trace. Set to null if the trace should not care about layer ranges.
     * @param maxSteps the maximum number of advance loops. Should be larger than the maximum desired ray trace length in blocks.
     * @return the ray trace result, or null if the trace didn't hit any blocks
     */
    @Nullable
    public static HitResult rayTraceBlocks(World world, Vec3d start, Vec3d end,
            IRayPositionHandler handler, RayTraceFluidHandling fluidMode, BlockPredicate blockFilter, boolean ignoreNonCollidable,
            boolean returnLastUncollidableBlock, @Nullable LayerRange layerRange, int maxSteps)
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
            Vec3d pos = new Vec3d(data.currentX, data.currentY, data.currentZ);
            return new HitResult(HitResult.Type.MISS, data.mutablePos.toImmutable(), data.facing, pos, null);
        }

        return null;
    }

    /* TODO b1.7.3
    public static boolean checkRayCollision(RayTraceCalculationData data, World world, boolean ignoreNonCollidable)
    {
        if (data.isPositionWithinRange())
        {
            IBlockState state = world.getBlockState(data.mutablePos);

            if (data.isValidBlock(state) &&
                ((ignoreNonCollidable == false && state.getMaterial() != Material.AIR)
                    || state.getCollisionBoundingBox(world, data.mutablePos) != Block.NULL_AABB))
            {
                if (state.getBlock().canCollideCheck(state, false) || data.fluidMode.handled(state))
                {
                    HitResult traceTmp = state.collisionRayTrace(world, data.mutablePos.toImmutable(),
                                                                 data.start, data.end);

                    if (traceTmp != null)
                    {
                        data.trace = traceTmp;
                        return true;
                    }
                }
            }
        }

        return false;
    }
    */

    /*// 1.15.2 version
    @Nullable
    private static boolean traceLoopSteps(RayTraceCalcsData data, World world,
            BlockState blockState, FluidState fluidState, boolean ignoreBlockWithoutBoundingBox)
    {
        if (data.isPositionWithinRange() &&
            (ignoreBlockWithoutBoundingBox == false || blockState.getMaterial() == Material.PORTAL ||
             blockState.getCollisionShape(world, data.blockPos).isEmpty() == false))
        {
            VoxelShape blockShape = blockState.getOutlineShape(world, data.blockPos);
            boolean blockCollidable = ! blockShape.isEmpty();
            boolean fluidCollidable = data.fluidMode.handled(blockState);

            if (blockCollidable == false && fluidCollidable == false)
            {
                Vec3d pos = new Vec3d(data.currentX, data.currentY, data.currentZ);
                data.trace = BlockHitResult.createMissed(pos, data.facing, data.blockPos);
            }
            else
            {
                BlockHitResult traceTmp = null;

                if (blockCollidable)
                {
                    traceTmp = blockShape.rayTrace(data.start, data.end, data.blockPos);
                }

                if (traceTmp == null && fluidCollidable)
                {
                    traceTmp = fluidState.getShape(world, data.blockPos).rayTrace(data.start, data.end, data.blockPos);
                }

                if (traceTmp != null)
                {
                    data.trace = traceTmp;
                    return true;
                }
            }
        }

        return false;
    }
    */

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

        int x = MathUtils.floor(data.currentX) - (data.facing == Direction.EAST ? 1 : 0);
        int y = MathUtils.floor(data.currentY) - (data.facing == Direction.UP ?    1 : 0);
        int z = MathUtils.floor(data.currentZ) - (data.facing == Direction.SOUTH ? 1 : 0);
        data.setBlockPos(x, y, z);

        return false;
    }

    public static class RayTraceCalculationData
    {
        @Nullable protected final LayerRange range;
        public final RayTraceFluidHandling fluidMode;
        public final BlockPredicate blockFilter;
        public final BlockPos.MutBlockPos mutablePos = new BlockPos.MutBlockPos();
        public final Vec3d start;
        public final Vec3d end;
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

        public RayTraceCalculationData(Vec3d start, Vec3d end, RayTraceFluidHandling fluidMode, BlockPredicate blockFilter, @Nullable LayerRange range)
        {
            this.start = start;
            this.end = end;
            this.fluidMode = fluidMode;
            this.blockFilter = blockFilter;
            this.range = range;
            this.currentX = start.x;
            this.currentY = start.y;
            this.currentZ = start.z;
            this.endBlockX = MathUtils.floor(end.x);
            this.endBlockY = MathUtils.floor(end.y);
            this.endBlockZ = MathUtils.floor(end.z);
            this.setBlockPos(MathUtils.floor(start.x), MathUtils.floor(start.y), MathUtils.floor(start.z));
        }

        public void setBlockPos(int x, int y, int z)
        {
            this.blockX = x;
            this.blockY = y;
            this.blockZ = z;
            this.mutablePos.set(this.blockX, this.blockY, this.blockZ);
        }

        public boolean isValidBlock(@Nullable Block block, int meta)
        {
            return this.blockFilter.matches(block, meta);
        }

        public boolean isPositionWithinRange()
        {
            return this.range == null || this.range.isPositionWithinRange(this.blockX, this.blockY, this.blockZ);
        }

        public boolean checkRayCollision(World world, boolean ignoreNonCollidable)
        {
            /* TODO b1.7.3
            if (this.isPositionWithinRange() == false)
            {
                return false;
            }

            IBlockState state = world.getBlockState(this.mutablePos);

            if (state.getMaterial() == Material.AIR ||
                this.isValidBlock(state) == false ||
                (ignoreNonCollidable == false && state.getCollisionBoundingBox(world, this.mutablePos) == Block.NULL_AABB))
            {
                return false;
            }

            if (state.getBlock().canCollideCheck(state, false) || this.fluidMode.handled(state))
            {
                HitResult traceTmp = state.collisionRayTrace(world, this.mutablePos,
                                                                  this.start, this.end);

                if (traceTmp != null)
                {
                    this.trace = traceTmp;
                    return true;
                }
            }
            */

            return false;
        }
    }

    public static final BlockPredicate BLOCK_FILTER_ANY = (block, meta) -> true;
    public static final BlockPredicate BLOCK_FILTER_NON_AIR = (block, meta) -> block != null && block.material != Material.AIR;

    public enum RayTraceFluidHandling
    {
        NONE((block, meta) -> BlockUtils.isFluidBlock(block) == false),
        SOURCE_ONLY((block, meta) -> BlockUtils.isFluidSourceBlock(block)),
        ANY((block, meta) -> BlockUtils.isFluidBlock(block));

        private final BlockPredicate predicate;

        RayTraceFluidHandling(BlockPredicate predicate)
        {
            this.predicate = predicate;
        }

        public boolean handled(Block block, int meta)
        {
            return this.predicate.matches(block, meta);
        }
     }

    public interface IRayPositionHandler
    {
        /**
         * A handler method, usually for checking for a collision at the given position along the ray trace
         * @return true if the ray should stop here and the current trace result from the RayTraceCalcsData should be returned
         */
        boolean handleRayTracePosition(RayTraceCalculationData data, World world, boolean ignoreNonCollidable);
    }

    public interface BlockPredicate
    {
        boolean matches(Block block, int meta);
    }
}
