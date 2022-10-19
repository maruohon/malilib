package malilib.util.game.wrap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

public class DefaultedList<E> extends net.minecraft.util.collection.DefaultedList<E>
{
    protected DefaultedList()
    {
        this(new ArrayList<>(), null);
    }

    protected DefaultedList(List<E> delegate, @Nullable E defaultValue)
    {
        super(delegate, defaultValue);
    }

    public static <E> DefaultedList<E> empty()
    {
        return new DefaultedList<>();
    }

    /**
     * Creates a new NonNullList with <i>fixed</i> size, and filled with the object passed.
     */
    @SuppressWarnings("unchecked")
    public static <E> DefaultedList<E> ofSize(int size, E defaultValue)
    {
        Validate.notNull(defaultValue);
        Object[] arr = new Object[size];
        Arrays.fill(arr, defaultValue);

        return new DefaultedList<>(Arrays.asList((E[]) arr), defaultValue);
    }
}
