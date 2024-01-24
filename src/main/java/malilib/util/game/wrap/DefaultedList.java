package malilib.util.game.wrap;

public class DefaultedList{/*<E> extends NonNullList<E>
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
    */

    /**
     * Creates a new NonNullList with <i>fixed</i> size, and filled with the object passed.
     */
    /*
    @SuppressWarnings("unchecked")
    public static <E> DefaultedList<E> ofSize(int size, E defaultValue)
    {
        Validate.notNull(defaultValue);
        Object[] arr = new Object[size];
        Arrays.fill(arr, defaultValue);

        return new DefaultedList<>(Arrays.asList((E[]) arr), defaultValue);
    }
    */
}
