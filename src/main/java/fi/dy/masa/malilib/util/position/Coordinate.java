package fi.dy.masa.malilib.util.position;

import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public enum Coordinate
{
    X(Vec3i::getX, (v) -> v.x, (n, o) -> new Vec3i(n       , o.getY(), o.getZ()), (n, o) -> new BlockPos(n       , o.getY(), o.getZ()), (n, o) -> new Vec3d(n  , o.y, o.z), (n, o) -> new Vec2i(n, o.y)),
    Y(Vec3i::getY, (v) -> v.y, (n, o) -> new Vec3i(o.getX(), n       , o.getZ()), (n, o) -> new BlockPos(o.getX(), n       , o.getZ()), (n, o) -> new Vec3d(o.x, n  , o.z), (n, o) -> new Vec2i(o.x, n)),
    Z(Vec3i::getZ, (v) -> v.z, (n, o) -> new Vec3i(o.getX(), o.getY(), n       ), (n, o) -> new BlockPos(o.getX(), o.getY(), n       ), (n, o) -> new Vec3d(o.x, o.y, n  ), (n, o) -> o);

    private final ToIntFunction<Vec3i> toIntFunction;
    private final ToDoubleFunction<Vec3d> toDoubleFunction;
    private final Vec3iModifier vec3iModifier;
    private final Vec3dModifier vec3dModifier;
    private final Vec2iModifier vec2iModifier;
    private final BlockPosModifier blockPosModifier;

    Coordinate(ToIntFunction<Vec3i> toIntFunction,
               ToDoubleFunction<Vec3d> toDoubleFunction,
               Vec3iModifier vec3iModifier,
               BlockPosModifier blockPosModifier,
               Vec3dModifier vec3dModifier,
               Vec2iModifier vec2iModifier)
    {
        this.toIntFunction = toIntFunction;
        this.toDoubleFunction = toDoubleFunction;
        this.vec3iModifier = vec3iModifier;
        this.blockPosModifier = blockPosModifier;
        this.vec3dModifier = vec3dModifier;
        this.vec2iModifier = vec2iModifier;
    }

    public int asInt(Vec3i pos)
    {
        return this.toIntFunction.applyAsInt(pos);
    }

    public double asDouble(Vec3d pos)
    {
        return this.toDoubleFunction.applyAsDouble(pos);
    }

    public Vec3i modifyVec3i(int newValue, Vec3i oldVec)
    {
        return this.vec3iModifier.modify(newValue, oldVec);
    }

    public Vec2i modifyVec2i(int newValue, Vec2i oldVec)
    {
        return this.vec2iModifier.modify(newValue, oldVec);
    }

    public Vec3d modifyVec3d(double newValue, Vec3d oldVec)
    {
        return this.vec3dModifier.modify(newValue, oldVec);
    }

    public BlockPos modifyBlockPos(int newValue, Vec3i oldVec)
    {
        return this.blockPosModifier.modify(newValue, oldVec);
    }

    public Vec3i offsetVec3i(int offset, Vec3i oldVec)
    {
        int newValue = this.toIntFunction.applyAsInt(oldVec) + offset;
        return this.vec3iModifier.modify(newValue, oldVec);
    }

    public Vec3d offsetVec3d(double offset, Vec3d oldVec)
    {
        double newValue = this.toDoubleFunction.applyAsDouble(oldVec) + offset;
        return this.vec3dModifier.modify(newValue, oldVec);
    }

    public BlockPos offsetBlockPos(int offset, BlockPos oldPos)
    {
        int newValue = this.toIntFunction.applyAsInt(oldPos) + offset;
        return this.blockPosModifier.modify(newValue, oldPos);
    }

    public String asIntString(Vec3i pos)
    {
        return String.valueOf(this.asInt(pos));
    }

    public String asDoubleString(Vec3d pos)
    {
        return String.valueOf(this.asDouble(pos));
    }

    public interface Vec3iModifier
    {
        Vec3i modify(int newValue, Vec3i oldVec);
    }

    public interface Vec3dModifier
    {
        Vec3d modify(double newValue, Vec3d oldVec);
    }

    public interface BlockPosModifier
    {
        BlockPos modify(int newValue, Vec3i oldPos);
    }

    public interface Vec2iModifier
    {
        Vec2i modify(int newValue, Vec2i oldVec);
    }
}
