package malilib.util.position;

import java.util.Comparator;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class ChunkSectionPos extends Vec3i
{
    public ChunkSectionPos(BlockPos pos)
    {
        this(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
    }

    public ChunkSectionPos(int x, int y, int z)
    {
        super(x, y, z);
    }

    public static class DistanceComparator implements Comparator<ChunkSectionPos>
    {
        private final ChunkSectionPos referencePosition;

        public DistanceComparator(ChunkSectionPos referencePosition)
        {
            this.referencePosition = referencePosition;
        }

        @Override
        public int compare(ChunkSectionPos pos1, ChunkSectionPos pos2)
        {
            int x = this.referencePosition.getX();
            int y = this.referencePosition.getY();
            int z = this.referencePosition.getZ();

            double dist1 = pos1.distanceSq(x, y, z);
            double dist2 = pos2.distanceSq(x, y, z);

            return Double.compare(dist1, dist2);
        }
    }
}
