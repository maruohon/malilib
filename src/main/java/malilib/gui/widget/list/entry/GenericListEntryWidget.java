package malilib.gui.widget.list.entry;

import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import malilib.gui.icon.Icon;
import malilib.render.text.StyledTextLine;

public class GenericListEntryWidget<T> extends BaseDataListEntryWidget<T>
{
    protected final Function<T, String> entryNameFunction;
    @Nullable protected final Function<T, Icon> entryIconFunction;

    public GenericListEntryWidget(T data,
                                  DataListEntryWidgetData constructData,
                                  Function<T, String> entryNameFunction,
                                  @Nullable Function<T, Icon> entryIconFunction,
                                  @Nullable Function<T, ImmutableList<StyledTextLine>> hoverInfoFunction)
    {
        super(data, constructData);

        this.entryNameFunction = entryNameFunction;
        this.entryIconFunction = entryIconFunction;
        this.downScaleIcon = true;

        this.setText(StyledTextLine.parseFirstLine(entryNameFunction.apply(data)));

        if (entryIconFunction != null)
        {
            Icon icon = entryIconFunction.apply(data);

            if (icon != null)
            {
                this.iconOffset.setXOffset(4);
                this.textOffset.setXOffset(this.getHeight() + 6);
                this.setIcon(icon);
            }
        }

        if (hoverInfoFunction != null)
        {
            this.getHoverInfoFactory().addTextLines(hoverInfoFunction.apply(data));
        }
    }
}
