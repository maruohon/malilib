package malilib.util.data.palette;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;

public class LinearPalette<T> implements Palette<T>
{
    protected final PaletteResizeHandler<T> paletteResizer;
    protected final T[] values;
    protected final int bits;
    protected final int maxSize;
    protected int currentSize;

    @SuppressWarnings("unchecked")
    public LinearPalette(int bitsIn, PaletteResizeHandler<T> paletteResizer)
    {
        this.bits = bitsIn;
        this.maxSize = 1 << bitsIn;
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
        return id >= 0 && id < this.currentSize ? this.values[id] : null;
    }

    @Override
    public int idFor(T value)
    {
        final int currentSize = this.currentSize;
        T[] values = this.values;

        for (int i = 0; i < currentSize; ++i)
        {
            if (values[i] == value)
            {
                return i;
            }
        }

        if (currentSize < this.maxSize)
        {
            this.values[currentSize] = value;
            ++this.currentSize;
            return currentSize;
        }
        else
        {
            return this.paletteResizer.onResize(this.bits + 1, value, this);
        }
    }

    @Override
    public List<T> getMapping()
    {
        List<T> list = new ArrayList<>(this.currentSize);
        list.addAll(Arrays.asList(this.values).subList(0, this.currentSize));
        return list;
    }

    @Override
    public boolean setMapping(List<T> list)
    {
        final int size = list.size();

        if (size <= this.values.length)
        {
            for (int id = 0; id < size; ++id)
            {
                this.values[id] = list.get(id);
            }

            this.currentSize = size;

            return true;
        }

        return false;
    }

    @Override
    public boolean overrideMapping(int id, T state)
    {
        if (id >= 0 && id < this.values.length)
        {
            this.values[id] = state;
            return true;
        }

        return false;
    }

    @Override
    public LinearPalette<T> copy(PaletteResizeHandler<T> resizeHandler)
    {
        LinearPalette<T> copy = new LinearPalette<>(this.bits, resizeHandler);

        System.arraycopy(this.values, 0, copy.values, 0, this.values.length);
        copy.currentSize  = this.currentSize;

        return copy;
    }
}
