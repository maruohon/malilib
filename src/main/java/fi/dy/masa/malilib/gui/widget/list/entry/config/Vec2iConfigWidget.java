package fi.dy.masa.malilib.gui.widget.list.entry.config;

import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;
import fi.dy.masa.malilib.config.option.Vec2iConfig;
import fi.dy.masa.malilib.gui.config.ConfigWidgetContext;
import fi.dy.masa.malilib.gui.widget.Vec2iEditWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.DataListEntryWidgetData;
import fi.dy.masa.malilib.util.position.Vec2i;

public class Vec2iConfigWidget extends BaseGenericConfigWidget<Vec2i, Vec2iConfig>
{
    protected final Vec2iEditWidget editWidget;

    public Vec2iConfigWidget(Vec2iConfig config,
                             DataListEntryWidgetData constructData,
                             ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.editWidget = new Vec2iEditWidget(120, 18, 6, false, config.getValue(), this.config::setValue);

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
}
