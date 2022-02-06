package fi.dy.masa.malilib.gui.widget.list.entry.action;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.action.ParameterizableNamedAction;
import fi.dy.masa.malilib.action.ParameterizedNamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.DualTextInputScreen;
import fi.dy.masa.malilib.gui.position.ElementOffset;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StyledTextUtils;
import fi.dy.masa.malilib.util.data.LeftRight;

public class ParameterizableActionEntryWidget extends ActionListBaseActionEntryWidget
{
    protected final GenericButton parameterizeButton;

    public ParameterizableActionEntryWidget(int x, int y, int width, int height, int listIndex,
                                            int originalListIndex, @Nullable NamedAction data,
                                            @Nullable DataListWidget<NamedAction> listWidget)
    {
        super(x, y, width, height, listIndex, originalListIndex, data, listWidget);

        StyledTextLine nameText = data.getWidgetDisplayName();
        this.setText(StyledTextUtils.clampStyledTextToMaxWidth(nameText, width - 20, LeftRight.RIGHT, " ..."));

        this.parameterizeButton = GenericButton.simple(14, "malilib.button.label.action_list_screen_widget.parameterize",
                                                       this::openParameterizationPrompt);
        this.parameterizeButton.translateAndAddHoverStrings("malilib.button.hover.parameterize_action");

        this.getBackgroundRenderer().getHoverSettings().setEnabled(false);
        this.getBorderRenderer().getHoverSettings().setBorderWidthAndColor(1, 0xFF00FF60);

        this.getHoverInfoFactory().setTextLineProvider("action_info", data::getHoverInfo);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();
        this.addWidget(this.parameterizeButton);
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();
        this.parameterizeButton.setY(this.getY() + ElementOffset.getCenteredElementOffset(this.getHeight(), this.parameterizeButton.getHeight()));
        this.parameterizeButton.setRight(this.createAliasButton.getX() - 2);
    }

    protected void openParameterizationPrompt()
    {
        DualTextInputScreen screen = new DualTextInputScreen("malilib.gui.prompt.title.parameterize_action",
                                                             "", "", this::parameterizeAction);
        screen.setInfoText("malilib.info.action.create_parameterized_copy");
        screen.setLabelText("malilib.label.action.parameterized_action_name");
        screen.setLabelText2("malilib.label.action.parameterized_action_argument");
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openPopupScreen(screen);
    }

    protected boolean parameterizeAction(String name, String arg)
    {
        ParameterizedNamedAction action = ((ParameterizableNamedAction) this.data).parameterize(name, arg);
        return Registry.ACTION_REGISTRY.addParameterizedAction(action);
    }
}
