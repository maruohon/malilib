package malilib.gui.widget.list.entry.action;

import malilib.action.NamedAction;
import malilib.gui.BaseScreen;
import malilib.gui.widget.list.entry.BaseDataListEntryWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.render.text.StyledTextLine;
import malilib.render.text.StyledTextUtils;
import malilib.util.data.LeftRight;

public class ActionPromptEntryWidget extends BaseDataListEntryWidget<NamedAction>
{
    public ActionPromptEntryWidget(NamedAction data,
                                   DataListEntryWidgetData constructData)
    {
        super(data, constructData);

        this.canReceiveMouseClicks = true;
        this.getBorderRenderer().getHoverSettings().setBorderWidthAndColor(1, 0xFFF0B000);
        this.getBackgroundRenderer().getHoverSettings().setEnabled(false);

        StyledTextLine fullName = data.getColoredWidgetDisplayName();
        this.setText(StyledTextUtils.clampStyledTextToMaxWidth(fullName, this.getWidth() - 16, LeftRight.RIGHT, " ..."));

        this.getHoverInfoFactory().setTextLineProvider("action_info", data::getHoverInfo);
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        // Close the current screen first, in case the action opens another screen
        BaseScreen.openScreen(null);
        this.data.execute();
        return true;
    }
}
