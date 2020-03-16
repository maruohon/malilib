package fi.dy.masa.malilib.util;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RayTraceUtils
{
    public static RayTraceResult getRayTraceFromEntity(World world, Entity entity,
            RayTraceFluidHandling fluidHandling, boolean includeEntities, double range)
    {
        Vec3d eyesPos = entity.getPositionEyes(1f);
        Vec3d rangedLookRot = entity.getLook(1f).scale(range);
        Vec3d lookEndPos = eyesPos.add(rangedLookRot);

        RayTraceResult result = rayTraceBlocks(world, eyesPos, lookEndPos, fluidHandling, true, false, null, 1000);

        if (includeEntities)
        {
            AxisAlignedBB bb = entity.getEntityBoundingBox().expand(rangedLookRot.x, rangedLookRot.y, rangedLookRot.z).expand(1d, 1d, 1d);
            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(entity, bb);

            double closest = result != null && result.typeOfHit == RayTraceResult.Type.BLOCK ? eyesPos.distanceTo(result.hitVec) : Double.MAX_VALUE;
            RayTraceResult entityTrace = null;
            Entity targetEntity = null;

            for (int i = 0; i < list.size(); i++)
            {
                Entity entityTmp = list.get(i);
                bb = entityTmp.getEntityBoundingBox();
                RayTraceResult traceTmp = bb.calculateIntercept(lookEndPos, eyesPos);

                if (traceTmp != null)
                {
                    double distance = eyesPos.distanceTo(traceTmp.hitVec);

                    if (distance <= closest)
                    {
                        targetEntity = entityTmp;
                        entityTrace = traceTmp;
                        closest = distance;
                    }
                }
            }

            if (targetEntity != null)
            {
                result = new RayTraceResult(targetEntity, entityTrace.hitVec);
            }
        }

        if (result == null || eyesPos.distanceTo(result.hitVec) > range)
        {
            result = new RayTraceResult(RayTraceResult.Type.MISS, Vec3d.ZERO, EnumFacing.UP, BlockPos.ORIGIN);
        }

        return result;
    }

    /**
     * Mostly copy pasted from World#rayTraceBlocks() except for the added maxSteps argument and the layer range check
     * @param world
     * @param start The start position of the trace
     * @param end The end position of the trace
     * @param fluidMode Whether or not to trace to fluids
     * @param ignoreNonCollidable If true, then blocks without a hard collision box are ignored
     * @param returnLastUncollidableBlock If true, then the last block position without a hard collision box is returned, if no other blocks were hit
     * @param layerRange The LayerRange within which to ray trace. Set to null if the trace should not care about layer ranges.
     * @param maxSteps the maximum number of advance loops. Should be larger than the maximum desired maximum ray trace length in blocks.
     * @return
     */
    @Nullable
    public static RayTraceResult rayTraceBlocks(World world, Vec3d start, Vec3d end,
            RayTraceFluidHandling fluidMode, boolean ignoreNonCollidable,
            boolean returnLastUncollidableBlock, @Nullable LayerRange layerRange, int maxSteps)
    {
        return rayTraceBlocks(world, start, end, fluidMode, BLOCK_FILTER_ANY,
                ignoreNonCollidable, returnLastUncollidableBlock, layerRange, maxSteps);
    }

    /**
     * Mostly copy pasted from World#rayTraceBlocks() except for the added maxSteps argument and the layer range check
     * @param world
     * @param start The start position of the trace
     * @param end The end position of the trace
     * @param fluidMode Whether or not to trace to fluids
     * @param blockFilter A test to check if the block is valid for a hit result
     * @param ignoreNonCollidable If true, then blocks without a hard collision box are ignored
     * @param returnLastUncollidableBlock If true, then the last block position without a hard collision box is returned, if no other blocks were hit
     * @param layerRange The LayerRange within which to ray trace. Set to null if the trace should not care about layer ranges.
     * @param maxSteps the maximum number of advance loops. Should be larger than the maximum desired maximum ray trace length in blocks.
     * @return
     */
    @Nullable
    public static RayTraceResult rayTraceBlocks(World world, Vec3d start, Vec3d end,
            RayTraceFluidHandling fluidMode, Predicate<IBlockState> blockFilter, boolean ignoreNonCollidable,
            boolean returnLastUncollidableBlock, @Nullable LayerRange layerRange, int maxSteps)
    {
        if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z) ||
            Double.isNaN(end.x) || Double.isNaN(end.y) || Double.isNaN(end.z))
        {
            return null;
        }

        RayTraceCalcsData data = new RayTraceCalcsData(start, end, fluidMode, blockFilter, layerRange);

        IBlockState blockState = world.getBlockState(data.blockPos);
        //FluidState fluidState = world.getFluidState(data.blockPos);

        while (--maxSteps >= 0)
        {
            blockState = world.getBlockState(data.blockPos);
            //fluidState = world.getFluidState(data.blockPos);

            if (checkCollision(data, world, blockState, /*fluidState,*/ ignoreNonCollidable))
            {
                //System.out.printf("checkCollision() - steps: %d, trace: %s\n", maxSteps, data.trace);
                return data.trace;
            }

            if (rayTraceStep(data))
            {
                //System.out.printf("rayTraceStep() - steps: %d, trace: %s\n", maxSteps, data.trace);
                break;
            }
        }

        if (returnLastUncollidableBlock)
        {
            Vec3d pos = new Vec3d(data.currentX, data.currentY, data.currentZ);
            return new RayTraceResult(RayTraceResult.Type.MISS, pos, data.facing, data.blockPos);
        }

        return null;
    }

    @Nullable
    public static boolean checkCollision(RayTraceCalcsData data,
            World world, IBlockState blockState,// FluidState fluidState,
            boolean ignoreNonCollidable)
    {
        if (data.isPositionWithinRange() && data.isValidBlock(blockState) &&
            (ignoreNonCollidable == false ||
            blockState.getCollisionBoundingBox(world, data.blockPos) != Block.NULL_AABB))
        {
            boolean blockCollidable = blockState.getBlock().canCollideCheck(blockState, false);
            boolean fluidCollidable = data.fluidMode.handled(blockState);

            if (blockCollidable || fluidCollidable)
            {
                RayTraceResult traceTmp = blockState.collisionRayTrace(world, data.blockPos, data.start, data.end);

                if (traceTmp != null)
                {
                    data.trace = traceTmp;
                    return true;
                }
            }
        }

        return false;
    }

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

    public static boolean rayTraceStep(RayTraceCalcsData data)
    {
        boolean xDiffers = true;
        boolean yDiffers = true;
        boolean zDiffers = true;
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
            nextX = (double) data.blockX + 1.0D;
        }
        else if (data.endBlockX < data.blockX)
        {
            nextX = (double) data.blockX + 0.0D;
        }
        else
        {
            xDiffers = false;
        }

        if (data.endBlockY > data.blockY)
        {
            nextY = (double) data.blockY + 1.0D;
        }
        else if (data.endBlockY < data.blockY)
        {
            nextY = (double) data.blockY + 0.0D;
        }
        else
        {
            yDiffers = false;
        }

        if (data.endBlockZ > data.blockZ)
        {
            nextZ = (double) data.blockZ + 1.0D;
        }
        else if (data.endBlockZ < data.blockZ)
        {
            nextZ = (double) data.blockZ + 0.0D;
        }
        else
        {
            zDiffers = false;
        }

        double relStepX = 999.0D;
        double relStepY = 999.0D;
        double relStepZ = 999.0D;
        double distToEndX = data.end.x - data.currentX;
        double distToEndY = data.end.y - data.currentY;
        double distToEndZ = data.end.z - data.currentZ;

        if (xDiffers)
        {
            relStepX = (nextX - data.currentX) / distToEndX;
        }

        if (yDiffers)
        {
            relStepY = (nextY - data.currentY) / distToEndY;
        }

        if (zDiffers)
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
            data.facing = data.endBlockX > data.blockX ? EnumFacing.WEST : EnumFacing.EAST;
            data.currentX = nextX;
            data.currentY += distToEndY * relStepX;
            data.currentZ += distToEndZ * relStepX;
        }
        else if (relStepY < relStepZ)
        {
            data.facing = data.endBlockY > data.blockY ? EnumFacing.DOWN : EnumFacing.UP;
            data.currentX += distToEndX * relStepY;
            data.currentY = nextY;
            data.currentZ += distToEndZ * relStepY;
        }
        else
        {
            data.facing = data.endBlockZ > data.blockZ ? EnumFacing.NORTH : EnumFacing.SOUTH;
            data.currentX += distToEndX * relStepZ;
            data.currentY += distToEndY * relStepZ;
            data.currentZ = nextZ;
        }

        int x = MathHelper.floor(data.currentX) - (data.facing == EnumFacing.EAST ?  1 : 0);
        int y = MathHelper.floor(data.currentY) - (data.facing == EnumFacing.UP ?    1 : 0);
        int z = MathHelper.floor(data.currentZ) - (data.facing == EnumFacing.SOUTH ? 1 : 0);
        data.setBlockPos(x, y, z);

        return false;
    }

    public static class RayTraceCalcsData
    {
        @Nullable protected final LayerRange range;
        public final RayTraceFluidHandling fluidMode;
        public final Predicate<IBlockState> blockFilter;
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
        public BlockPos blockPos;
        public EnumFacing facing;
        @Nullable public RayTraceResult trace;

        public RayTraceCalcsData(Vec3d start, Vec3d end, RayTraceFluidHandling fluidMode, Predicate<IBlockState> blockFilter, @Nullable LayerRange range)
        {
            this.start = start;
            this.end = end;
            this.fluidMode = fluidMode;
            this.blockFilter = blockFilter;
            this.range = range;
            this.currentX = start.x;
            this.currentY = start.y;
            this.currentZ = start.z;
            this.endBlockX = MathHelper.floor(end.x);
            this.endBlockY = MathHelper.floor(end.y);
            this.endBlockZ = MathHelper.floor(end.z);
            this.setBlockPos(MathHelper.floor(start.x), MathHelper.floor(start.y), MathHelper.floor(start.z));
        }

        public void setBlockPos(int x, int y, int z)
        {
            this.blockX = x;
            this.blockY = y;
            this.blockZ = z;
            this.blockPos = new BlockPos(this.blockX, this.blockY, this.blockZ);
        }

        public boolean isValidBlock(IBlockState state)
        {
            return this.blockFilter.test(state);
        }

        public boolean isPositionWithinRange()
        {
            return this.range == null || this.range.isPositionWithinRange(this.blockX, this.blockY, this.blockZ);
        }
    }

    public static final Predicate<IBlockState> BLOCK_FILTER_ANY = (state) -> true;
    public static final Predicate<IBlockState> BLOCK_FILTER_NON_AIR = (state) -> state.getMaterial() != Material.AIR;

    public static enum RayTraceFluidHandling
    {
        NONE((blockState) -> {
            return blockState.getMaterial().isLiquid() == false;
        }),
        SOURCE_ONLY((blockState) -> {
            return blockState.getMaterial().isLiquid() &&
                   blockState.getBlock() instanceof BlockLiquid &&
                   blockState.getValue(BlockLiquid.LEVEL).intValue() == 0;
        }),
        ANY((blockState) -> {
            return blockState.getMaterial().isLiquid();
        });

        private final Predicate<IBlockState> predicate;

        private RayTraceFluidHandling(Predicate<IBlockState> predicate)
        {
            this.predicate = predicate;
        }

        public boolean handled(IBlockState blockState)
        {
            return this.predicate.test(blockState);
        }
     }
}
