package fi.dy.masa.malilib.gui.action;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.icon.IconRegistry;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.CheckBoxWidget;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.IconWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.registry.Registry;

public class AddActionExecutionWidgetScreen extends BaseScreen
{
    protected final Consumer<BaseActionExecutionWidget> widgetConsumer;
    protected final DropDownListWidget<NamedAction> actionDropDownWidget;
    protected final DropDownListWidget<BaseActionExecutionWidget.Type> typeDropDownWidget;
    protected final DropDownListWidget<Icon> iconDropDownWidget;
    protected final LabelWidget actionLabelWidget;
    protected final LabelWidget typeLabelWidget;
    protected final LabelWidget nameLabelWidget;
    protected final LabelWidget iconLabelWidget;
    protected final LabelWidget hoverTextLabelWidget;
    protected final LabelWidget argumentLabelWidget;
    protected final CheckBoxWidget addArgumentCheckbox;
    protected final BaseTextFieldWidget nameTextField;
    protected final BaseTextFieldWidget hoverTextTextField;
    protected final BaseTextFieldWidget argumentTextField;
    protected final GenericButton addButton;
    protected final GenericButton cancelButton;
    protected boolean hasArgumentElements;

    public AddActionExecutionWidgetScreen(Consumer<BaseActionExecutionWidget> widgetConsumer)
    {
        this.widgetConsumer = widgetConsumer;

        this.setTitle("malilib.title.screen.create_action_execution_widget");
        this.useTitleHierarchy = false;

        this.actionLabelWidget = new LabelWidget("malilib.label.action_widgets.action");
        this.typeLabelWidget = new LabelWidget("malilib.label.misc.type");
        this.nameLabelWidget = new LabelWidget("malilib.label.misc.name_optional");
        this.iconLabelWidget = new LabelWidget("malilib.label.actions.action_widget.icon_optional");
        this.hoverTextLabelWidget = new LabelWidget("malilib.label.misc.hover_text_optional");
        this.argumentLabelWidget = new LabelWidget("malilib.label.misc.argument");

        this.actionDropDownWidget = new DropDownListWidget<>(160, 16, 240, 20,
                                                             Registry.ACTION_REGISTRY.getAllActions(),
                                                             NamedAction::getDisplayName);
        this.actionDropDownWidget.setSelectionListener(this::onActionSelected);

        this.typeDropDownWidget = new DropDownListWidget<>(-1, 16, 80, 4,
                                                           BaseActionExecutionWidget.Type.VALUES,
                                                           BaseActionExecutionWidget.Type::getDisplayName);
        this.typeDropDownWidget.setSelectedEntry(BaseActionExecutionWidget.Type.RECTANGULAR);

        this.iconDropDownWidget = new DropDownListWidget<>(120, 16, 120, 10,
                                                           Registry.ICON.getAllIcons(),
                                                           IconRegistry::getKeyForIcon, (h, i) -> new IconWidget(i));

        this.addArgumentCheckbox = new CheckBoxWidget("malilib.button.action_widgets.add_argument",
                                                      "malilib.hover.action.add_action_execution_widget.add_argument");

        this.nameTextField = new BaseTextFieldWidget(140, 16);
        this.hoverTextTextField = new BaseTextFieldWidget(140, 16);
        this.argumentTextField = new BaseTextFieldWidget(160, 16);

        this.addButton = GenericButton.create("malilib.button.misc.add", this::createActionWidget);
        this.cancelButton = GenericButton.create("malilib.button.misc.cancel", this::openParentScreen);

        this.backgroundColor = 0xFF101010;
        this.setScreenWidthAndHeight(240, 190);
        this.centerOnScreen();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.actionLabelWidget);
        this.addWidget(this.actionDropDownWidget);

        this.addWidget(this.typeLabelWidget);
        this.addWidget(this.typeDropDownWidget);

        this.addWidget(this.nameLabelWidget);
        this.addWidget(this.nameTextField);

        this.addWidget(this.hoverTextLabelWidget);
        this.addWidget(this.hoverTextTextField);

        this.addWidget(this.iconLabelWidget);
        this.addWidget(this.iconDropDownWidget);

        this.addWidget(this.addButton);
        this.addWidget(this.cancelButton);

        if (this.hasArgumentElements)
        {
            this.addWidget(this.addArgumentCheckbox);
            this.addWidget(this.argumentLabelWidget);
            this.addWidget(this.argumentTextField);
        }
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 10;
        int y = this.y + 24;

        this.actionLabelWidget.setPosition(x, y + 4);
        this.actionDropDownWidget.setPosition(this.actionLabelWidget.getRight() + 6, y);

        y += 20;
        this.typeLabelWidget.setPosition(x, y + 4);
        this.typeDropDownWidget.setPosition(this.typeLabelWidget.getRight() + 6, y);

        y += 22;
        this.nameLabelWidget.setPosition(x, y);
        y += 10;
        this.nameTextField.setPosition(x, y);

        y += 22;
        this.hoverTextLabelWidget.setPosition(x, y);
        y += 10;
        this.hoverTextTextField.setPosition(x, y);

        y += 22;
        this.iconLabelWidget.setPosition(x, y);
        y += 10;
        this.iconDropDownWidget.setPosition(x, y);

        y += 24;
        this.addArgumentCheckbox.setPosition(x, y);

        y += 14;
        this.argumentLabelWidget.setPosition(x, y);
        y += 10;
        this.argumentTextField.setPosition(x, y);

        if (this.hasArgumentElements)
        {
            y = this.argumentTextField.getBottom() + 6;
        }
        else
        {
            y = this.iconDropDownWidget.getBottom() + 6;
        }

        this.addButton.setPosition(x, y);
        this.cancelButton.setPosition(this.addButton.getRight() + 10, y);
    }

    protected void updateHeight()
    {
        int height = this.hasArgumentElements ? 238 : 190;
        this.setScreenWidthAndHeight(240, height);
        this.centerOnScreen();
    }

    protected void onActionSelected(@Nullable NamedAction action)
    {
        this.argumentTextField.setText("");
        this.addArgumentCheckbox.setSelected(false);
        //this.hasArgumentElements = action != null && action.needsArgument(); // TODO

        this.reAddActiveWidgets();
        this.updateHeight();
    }

    protected void createActionWidget()
    {
        NamedAction action = this.actionDropDownWidget.getSelectedEntry();
        BaseActionExecutionWidget.Type type = this.typeDropDownWidget.getSelectedEntry();

        if (action != null && type != null)
        {
            // TODO
            /*
            if (this.addArgumentCheckbox.isSelected() && action.needsArgument())
            {
                action = action.createAlias(action.getName() + "_parameterized", this.argumentTextField.getText());
            }
            */

            BaseActionExecutionWidget widget = type.create();
            widget.setAction(action);
            widget.setName(this.nameTextField.getText());
            widget.setIcon(this.iconDropDownWidget.getSelectedEntry());
            widget.setActionWidgetHoverText(this.hoverTextTextField.getText());

            this.widgetConsumer.accept(widget);
            this.openParentScreen();
        }
    }
}
