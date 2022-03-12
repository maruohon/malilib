package fi.dy.masa.malilib.gui.widget.list.header;

import java.util.Comparator;
import java.util.Optional;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.value.HorizontalAlignment;
import fi.dy.masa.malilib.config.value.SortDirection;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.render.text.StyledTextLine;

public class DataColumn<DATATYPE>
{
    @Nullable protected final String nameTranslationKey;
    @Nullable protected final Icon sortIconAsc;
    @Nullable protected final Icon sortIconDesc;
    @Nullable protected final Comparator<DATATYPE> comparator;
    protected final HorizontalAlignment iconPosition;
    protected SortDirection defaultSortDirection = SortDirection.ASCENDING;
    protected int maxContentWidth = -1;
    protected int relativeStartX;
    protected int width;

    public DataColumn(@Nullable String nameTranslationKey,
                      @Nullable Comparator<DATATYPE> comparator)
    {
        this(nameTranslationKey,
             DefaultIcons.SMALL_ARROW_UP,
             DefaultIcons.SMALL_ARROW_DOWN,
             comparator,
             HorizontalAlignment.RIGHT);
    }

    public DataColumn(@Nullable String nameTranslationKey,
                      @Nullable Comparator<DATATYPE> comparator,
                      SortDirection defaultSortDirection)
    {
        this(nameTranslationKey,
             DefaultIcons.SMALL_ARROW_UP,
             DefaultIcons.SMALL_ARROW_DOWN,
             comparator,
             HorizontalAlignment.RIGHT);

        this.defaultSortDirection = defaultSortDirection;
    }

    public DataColumn(@Nullable String nameTranslationKey,
                      @Nullable Icon sortIconAsc,
                      @Nullable Icon sortIconDesc,
                      @Nullable Comparator<DATATYPE> comparator)
    {
        this(nameTranslationKey, sortIconAsc, sortIconDesc, comparator, HorizontalAlignment.RIGHT);
    }

    public DataColumn(@Nullable String nameTranslationKey,
                      @Nullable Icon sortIconAsc,
                      @Nullable Icon sortIconDesc,
                      @Nullable Comparator<DATATYPE> comparator,
                      HorizontalAlignment iconPosition)
    {
        this.nameTranslationKey = nameTranslationKey;
        this.sortIconAsc = sortIconAsc;
        this.sortIconDesc = sortIconDesc;
        this.iconPosition = iconPosition;
        this.comparator = comparator;
    }

    public Optional<StyledTextLine> getName()
    {
        if (this.nameTranslationKey != null)
        {
            return Optional.of(StyledTextLine.translate(this.nameTranslationKey));
        }

        return Optional.empty();
    }

    public Optional<Comparator<DATATYPE>> getComparator()
    {
        return Optional.ofNullable(this.comparator);
    }

    public SortDirection getDefaultSortDirection()
    {
        return this.defaultSortDirection;
    }

    public DataColumn<DATATYPE> setDefaultSortDirection(SortDirection defaultSortDirection)
    {
        this.defaultSortDirection = defaultSortDirection;
        return this;
    }

    public boolean getCanSortBy()
    {
        return this.comparator != null;
    }

    public Optional<Icon> getSortIcon(SortDirection direction)
    {
        if (direction == SortDirection.ASCENDING)
        {
            return Optional.ofNullable(this.sortIconAsc);
        }

        if (direction == SortDirection.DESCENDING)
        {
            return Optional.ofNullable(this.sortIconDesc);
        }

        return Optional.empty();
    }

    public HorizontalAlignment getIconPosition()
    {
        return this.iconPosition;
    }

    public int getMaxContentWidth()
    {
        return this.maxContentWidth;
    }

    public int getRelativeStartX()
    {
        return this.relativeStartX;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getRelativeRight()
    {
        return this.relativeStartX + this.width;
    }

    public void setMaxContentWidth(int maxContentWidth)
    {
        this.maxContentWidth = maxContentWidth;
    }

    public void setRelativeStartX(int relativeStartX)
    {
        this.relativeStartX = relativeStartX;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }
}
