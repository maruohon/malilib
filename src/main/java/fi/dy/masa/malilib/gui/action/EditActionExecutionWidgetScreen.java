package fi.dy.masa.malilib.gui.action;

import java.util.List;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.position.EdgeInt;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.CheckBoxWidget;
import fi.dy.masa.malilib.gui.widget.ColorEditorWidget;
import fi.dy.masa.malilib.gui.widget.IntegerEditWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.util.StringUtils;

public class EditActionExecutionWidgetScreen extends BaseScreen
{
    protected final List<ActionExecutionWidget> widgets;
    protected final ActionExecutionWidget firstWidget;
    protected final LabelWidget nameLabelWidget;
    protected final LabelWidget nameColorLabelWidget;
    protected final LabelWidget nameXOffsetLabelWidget;
    protected final LabelWidget nameYOffsetLabelWidget;
    protected final LabelWidget hoveredBgColorLabelWidget;
    protected final LabelWidget normalBgColorLabelWidget;
    protected final LabelWidget hoveredBorderColorLabelWidget;
    protected final LabelWidget normalBorderColorLabelWidget;
    protected final BaseTextFieldWidget nameTextField;
    protected final IntegerEditWidget nameXOffsetEditWidget;
    protected final IntegerEditWidget nameYOffsetEditWidget;
    protected final CheckBoxWidget nameCenteredOnXCheckbox;
    protected final CheckBoxWidget nameCenteredOnYCheckbox;
    protected final ColorEditorWidget nameColorEditWidget;
    protected final ColorEditorWidget hoveredBackgroundColorEditWidget;
    protected final ColorEditorWidget normalBackgroundColorEditWidget;
    protected final ColorEditorWidget hoveredBorderColorEditWidget;
    protected final ColorEditorWidget normalBorderColorEditWidget;

    public EditActionExecutionWidgetScreen(List<ActionExecutionWidget> widgets)
    {
        if (widgets.size() > 1)
        {
            this.title = StringUtils.translate("malilib.gui.title.edit_action_execution_widget.multiple", widgets.size());
        }
        else
        {
            this.title = StringUtils.translate("malilib.gui.title.edit_action_execution_widget");
        }

        this.useTitleHierarchy = false;
        this.widgets = widgets;
        ActionExecutionWidget widget = widgets.get(0);
        this.firstWidget = widget;

        this.nameLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.name.colon");
        this.nameTextField = new BaseTextFieldWidget(0, 0, 140, 16, widget.getName());

        this.nameXOffsetLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.name_x_offset.colon");
        this.nameYOffsetLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.name_y_offset.colon");

        this.nameColorLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.name_color.colon");
        this.normalBgColorLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.background.colon");
        this.hoveredBgColorLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.hovered_background.colon");
        this.normalBorderColorLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.border_color.colon");
        this.hoveredBorderColorLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.hovered_border.colon");

        this.nameXOffsetEditWidget = new IntegerEditWidget(0, 0, 72, 16, widget.getTextOffsetX(), -512, 512, widget::setTextOffsetX);
        this.nameYOffsetEditWidget = new IntegerEditWidget(0, 0, 72, 16, widget.getTextOffsetY(), -512, 512, widget::setTextOffsetY);
        this.nameCenteredOnXCheckbox = new CheckBoxWidget(0, 0, "malilib.label.center", null);
        this.nameCenteredOnYCheckbox = new CheckBoxWidget(0, 0, "malilib.label.center", null);
        this.nameCenteredOnXCheckbox.setBooleanStorage(widget::getCenterTextHorizontally, widget::setCenterTextHorizontally);
        this.nameCenteredOnYCheckbox.setBooleanStorage(widget::getCenterTextVertically, widget::setCenterTextVertically);

        this.nameColorEditWidget                = new ColorEditorWidget(0, 0, 90, 16, widget::getDefaultTextColor, widget::setDefaultTextColor);
        this.normalBackgroundColorEditWidget    = new ColorEditorWidget(0, 0, 90, 16, widget::getNormalBackgroundColor, widget::setNormalBackgroundColor);
        this.hoveredBackgroundColorEditWidget   = new ColorEditorWidget(0, 0, 90, 16, widget::getHoveredBackgroundColor, widget::setHoveredBackgroundColor);
        this.normalBorderColorEditWidget        = new ColorEditorWidget(0, 0, 90, 16, widget.getNormalBorderColor());
        this.hoveredBorderColorEditWidget       = new ColorEditorWidget(0, 0, 90, 16, widget.getHoveredBorderColor());

        this.backgroundColor = 0xFF101010;
        this.setScreenWidthAndHeight(240, 200);
        this.centerOnScreen();
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        int x = this.x + 10;
        int y = this.y + 24;

        if (this.widgets.size() == 1)
        {
            this.nameLabelWidget.setPosition(x, y + 4);
            this.nameTextField.setPosition(this.nameLabelWidget.getRight() + 6, y);
            y += 20;
        }

        this.nameXOffsetLabelWidget.setPosition(x, y + 4);
        this.nameXOffsetEditWidget.setPosition(this.nameXOffsetLabelWidget.getRight() + 6, y);
        this.nameCenteredOnXCheckbox.setPosition(this.nameXOffsetEditWidget.getRight() + 6, y + 4);
        y += 20;

        this.nameYOffsetLabelWidget.setPosition(x, y + 4);
        this.nameYOffsetEditWidget.setPosition(this.nameYOffsetLabelWidget.getRight() + 6, y);
        this.nameCenteredOnYCheckbox.setPosition(this.nameYOffsetEditWidget.getRight() + 6, y + 4);
        y += 20;

        this.nameColorLabelWidget.setPosition(x, y + 4);
        this.nameColorEditWidget.setPosition(this.nameColorLabelWidget.getRight() + 6, y);
        y += 20;

        this.normalBgColorLabelWidget.setPosition(x, y + 4);
        this.normalBackgroundColorEditWidget.setPosition(this.normalBgColorLabelWidget.getRight() + 6, y);
        y += 20;

        this.hoveredBgColorLabelWidget.setPosition(x, y + 4);
        this.hoveredBackgroundColorEditWidget.setPosition(this.hoveredBgColorLabelWidget.getRight() + 6, y);
        y += 20;

        this.normalBorderColorLabelWidget.setPosition(x, y + 4);
        this.normalBorderColorEditWidget.setPosition(this.normalBorderColorLabelWidget.getRight() + 6, y);
        y += 20;

        this.hoveredBorderColorLabelWidget.setPosition(x, y + 4);
        this.hoveredBorderColorEditWidget.setPosition(this.hoveredBorderColorLabelWidget.getRight() + 6, y);

        int x1 = Math.max(this.nameColorLabelWidget.getRight(), this.normalBgColorLabelWidget.getRight());
        int x2 = Math.max(this.hoveredBgColorLabelWidget.getRight(), this.normalBorderColorLabelWidget.getRight());
        x = Math.max(x1, x2);
        x = Math.max(x, this.hoveredBorderColorLabelWidget.getRight()) + 6;
        this.nameColorEditWidget.setX(x);
        this.normalBackgroundColorEditWidget.setX(x);
        this.hoveredBackgroundColorEditWidget.setX(x);
        this.normalBorderColorEditWidget.setX(x);
        this.hoveredBorderColorEditWidget.setX(x);

        if (this.widgets.size() == 1)
        {
            this.addWidget(this.nameLabelWidget);
            this.addWidget(this.nameTextField);
        }

        this.addWidget(this.nameXOffsetLabelWidget);
        this.addWidget(this.nameXOffsetEditWidget);
        this.addWidget(this.nameCenteredOnXCheckbox);

        this.addWidget(this.nameYOffsetLabelWidget);
        this.addWidget(this.nameYOffsetEditWidget);
        this.addWidget(this.nameCenteredOnYCheckbox);

        this.addWidget(this.nameColorLabelWidget);
        this.addWidget(this.nameColorEditWidget);

        this.addWidget(this.normalBgColorLabelWidget);
        this.addWidget(this.normalBackgroundColorEditWidget);

        this.addWidget(this.hoveredBgColorLabelWidget);
        this.addWidget(this.hoveredBackgroundColorEditWidget);

        this.addWidget(this.normalBorderColorLabelWidget);
        this.addWidget(this.normalBorderColorEditWidget);

        this.addWidget(this.hoveredBorderColorLabelWidget);
        this.addWidget(this.hoveredBorderColorEditWidget);
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        int size = this.widgets.size();

        // Copy the values from the first widget, to which they get set from the edit widgets
        if (size > 1)
        {
            int nameColor = this.firstWidget.getDefaultTextColor();
            int normalBg = this.firstWidget.getNormalBackgroundColor();
            int hoverBg = this.firstWidget.getHoveredBackgroundColor();
            EdgeInt normalBorder = this.firstWidget.getNormalBorderColor();
            EdgeInt hoverBorder = this.firstWidget.getHoveredBorderColor();
            int offsetX = this.firstWidget.getTextOffsetX();
            int offsetY = this.firstWidget.getTextOffsetY();
            boolean centerX = this.firstWidget.getCenterTextHorizontally();
            boolean centerY = this.firstWidget.getCenterTextVertically();

            for (int i = 1; i < size; ++i)
            {
                ActionExecutionWidget widget = this.widgets.get(i);
                widget.setDefaultTextColor(nameColor);

                widget.setNormalBackgroundColor(normalBg);
                widget.setHoveredBackgroundColor(hoverBg);

                widget.getNormalBorderColor().setFrom(normalBorder);
                widget.getHoveredBorderColor().setFrom(hoverBorder);

                widget.setCenterTextHorizontally(centerX);
                widget.setCenterTextVertically(centerY);

                widget.setTextOffsetX(offsetX);
                widget.setTextOffsetY(offsetY);
            }
        }
        else
        {
            this.firstWidget.setName(this.nameTextField.getText());
        }
    }
}
