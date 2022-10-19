package malilib.util.data.palette;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class HashMapPalette<T> implements Palette<T>
{
    protected final PaletteResizeHandler<T> paletteResizer;
    protected final Object2IntOpenHashMap<T> valueToIdMap;
    protected final T[] values;
    protected final int bits;
    protected final int maxSize;
    protected int currentSize;

    @SuppressWarnings("unchecked")
    public HashMapPalette(int bitsIn, PaletteResizeHandler<T> paletteResizer)
    {
        this.bits = bitsIn;
        this.maxSize = 1 << bitsIn;
        this.valueToIdMap = new Object2IntOpenHashMap<>();
        this.valueToIdMap.defaultReturnValue(-1);
        this.values = (T[]) new Object[this.maxSize];
        this.paletteResizer = paletteResizer;
    }

    @Override
    public int getSize()
    {
        return this.currentSize;
    }

    @Override
    public int getMaxSize()
    {
        return this.maxSize;
    }

    @Override
    @Nullable
    public T getValue(int id)
    {
        return id >= 0 && id < this.maxSize ? this.values[id] : null;
    }

    @Override
    public int idFor(T value)
    {
        int id = this.valueToIdMap.getInt(value);

        if (id == -1)
        {
            if (this.currentSize >= this.maxSize)
            {
                id = this.paletteResizer.onResize(this.bits + 1, value, this);
            }
            else
            {
                id = this.valueToIdMap.put(value, this.currentSize);
                ++this.currentSize;
            }
        }

        return id;
    }

    @Override
    public List<T> getMapping()
    {
        final int size = this.currentSize;
        List<T> list = new ArrayList<>(size);

        for (int id = 0; id < size; ++id)
        {
            list.add(this.values[id]);
        }

        return list;
    }

    @Override
    public boolean setMapping(List<T> list)
    {
        if (list.size() > this.maxSize)
        {
            throw new IllegalArgumentException("Tried to set a mapping that exceeds the maximum size " +
                                               "of the palette (mapping size:" + list.size() +
                                               ", max size: " + this.maxSize + ")");
        }

        this.valueToIdMap.clear();
        Arrays.fill(this.values, null);
        int id = 0;

        for (T val : list)
        {
            this.valueToIdMap.put(val, id);
            this.values[id++] = val;
        }

        return true;
    }

    @Override
    public boolean overrideMapping(int id, T value)
    {
        if (id >= 0 && id < this.currentSize)
        {
            this.values[id] = value;
            this.valueToIdMap.put(value, id);
            return true;
        }

        return false;
    }

    @Override
    public HashMapPalette<T> copy(PaletteResizeHandler<T> resizeHandler)
    {
        HashMapPalette<T> copy = new HashMapPalette<>(this.bits, resizeHandler);

        for (int id = 0; id < this.currentSize; ++id)
        {
            T value = this.values[id];
            copy.values[id] = value;
            copy.valueToIdMap.put(value, id);
        }

        return copy;
    }
}
