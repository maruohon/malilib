package malilib.gui.widget.list.entry.config;

import javax.annotation.Nullable;
import malilib.config.option.BaseDualValueConfig;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.BaseDualNumberEditWidget;
import malilib.gui.widget.BaseNumberEditWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import org.apache.commons.lang3.tuple.Pair;

public abstract class BaseDualNumberConfigWidget<T, C extends BaseDualValueConfig<T>, W extends BaseNumberEditWidget> extends BaseGenericConfigWidget<T, C>
{
    protected final BaseDualNumberEditWidget<T, W> editWidget;

    public BaseDualNumberConfigWidget(C config,
                                      DataListEntryWidgetData constructData,
                                      ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.editWidget = this.createEditWidget(config);

        @Nullable Pair<String, String> labels = config.getLabels();
        @Nullable Pair<String, String> hoverTexts = config.getHoverTexts();

        if (labels != null)
        {
            this.editWidget.setLabels(labels.getLeft(), labels.getRight());
        }

        if (hoverTexts != null)
        {
            this.editWidget.setHoverTexts(hoverTexts.getLeft(), hoverTexts.getRight());
        }
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.editWidget);
        this.addWidget(this.resetButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getElementsStartPosition();
        int y = this.getY();

        this.editWidget.setWidth(this.getElementWidth());
        this.editWidget.setX(x);
        this.editWidget.centerVerticallyInside(this);

        this.resetButton.setPosition(this.editWidget.getRight() + 4, y + 1);
    }

    @Override
    public void updateWidgetState()
    {
        super.updateWidgetState();
        this.editWidget.setValueAndUpdate(this.config.getValue());
    }

    protected abstract BaseDualNumberEditWidget<T, W> createEditWidget(C config);
}
