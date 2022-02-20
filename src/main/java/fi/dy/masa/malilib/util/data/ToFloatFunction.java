package fi.dy.masa.malilib.util.data;

@FunctionalInterface
public interface ToFloatFunction<T>
{
    float applyAsFloat(T value);
}
