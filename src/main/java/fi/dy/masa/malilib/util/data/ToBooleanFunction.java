package fi.dy.masa.malilib.util.data;

@FunctionalInterface
public interface ToBooleanFunction<T>
{
    boolean applyAsBoolean(T value);
}
