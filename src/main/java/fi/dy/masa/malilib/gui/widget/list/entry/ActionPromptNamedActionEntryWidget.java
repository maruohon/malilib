package fi.dy.masa.malilib.gui.widget.list.entry;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.action.ActionContext;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;

public class ActionPromptNamedActionEntryWidget extends NamedActionEntryWidget
{
    public ActionPromptNamedActionEntryWidget(int x, int y, int width, int height, int listIndex,
                                              int originalListIndex, @Nullable NamedAction data,
                                              @Nullable DataListWidget<? extends NamedAction> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        this.renderHoverBackground = false;
        this.borderEnabledHovered = true;
        this.setHoveredBorderColor(0xFF00C0C0);
        this.setHoveredBorderWidth(1);
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        // Close the current screen first, in case the action opens another screen
        BaseScreen.openScreen(null);
        this.data.getAction().execute(new ActionContext());
        return true;
    }
}
