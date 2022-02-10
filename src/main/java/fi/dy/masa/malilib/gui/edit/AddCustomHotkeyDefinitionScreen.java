package fi.dy.masa.malilib.gui.edit;

import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.input.CustomHotkeyDefinition;
import fi.dy.masa.malilib.input.CustomHotkeyManager;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindImpl;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import fi.dy.masa.malilib.registry.Registry;

public class AddCustomHotkeyDefinitionScreen extends BaseScreen
{
    protected final BaseTextFieldWidget nameTextField;
    protected final DropDownListWidget<NamedAction> actionDropDownWidget;
    protected final LabelWidget actionLabelWidget;
    protected final LabelWidget nameLabelWidget;
    protected final GenericButton addButton;
    protected final GenericButton cancelButton;

    public AddCustomHotkeyDefinitionScreen()
    {
        this.useTitleHierarchy = false;
        this.backgroundColor = 0xFF000000;
        this.renderBorder = true;
        this.setTitle("malilib.gui.title.add_new_hotkey");

        this.nameLabelWidget = new LabelWidget(0xFFF0F0F0, "malilib.label.name.colon");
        this.actionLabelWidget = new LabelWidget(0xFFF0F0F0, "malilib.label.action.colon");

        this.nameTextField = new BaseTextFieldWidget(160, 16);
        this.addButton = GenericButton.simple("malilib.gui.button.add", this::onAddButtonClicked);
        this.cancelButton = GenericButton.simple("malilib.gui.button.cancel", this::onCancelButtonClicked);

        this.actionDropDownWidget = new DropDownListWidget<>(-1, 16, 160, 10,
                                                             Registry.ACTION_REGISTRY.getAllActions(),
                                                             NamedAction::getName);

        this.nameTextField.setFocused(true);

        this.setScreenWidthAndHeight(320, 120);
        this.centerOnScreen();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.nameLabelWidget);
        this.addWidget(this.nameTextField);
        this.addWidget(this.actionLabelWidget);
        this.addWidget(this.actionDropDownWidget);
        this.addWidget(this.addButton);
        this.addWidget(this.cancelButton);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 10;
        int y = this.y + 24;

        this.nameLabelWidget.setPosition(x, y);
        y += 12;
        this.nameTextField.setPosition(x, y);

        y += 24;
        this.actionLabelWidget.setPosition(x, y);
        y += 12;
        this.actionDropDownWidget.setPosition(x, y);

        y += 22;
        this.addButton.setPosition(x, y);
        this.cancelButton.setPosition(this.addButton.getRight() + 6, y);
    }

    protected void onAddButtonClicked()
    {
        String name = this.nameTextField.getText();
        NamedAction action = this.actionDropDownWidget.getSelectedEntry();

        if (org.apache.commons.lang3.StringUtils.isBlank(name))
        {
            MessageDispatcher.error("malilib.message.error.custom_hotkey_add.give_name");
            return;
        }

        if (action == null)
        {
            MessageDispatcher.error("malilib.message.error.custom_hotkey_add.select_action");
            return;
        }

        KeyBind keyBind = KeyBindImpl.fromStorageString("", KeyBindSettings.INGAME_SUCCESS);
        CustomHotkeyManager.INSTANCE.addCustomHotkey(new CustomHotkeyDefinition(name, keyBind, action));
        BaseScreen.openScreen(this.getParent());
    }

    protected void onCancelButtonClicked()
    {
        BaseScreen.openScreen(this.getParent());
    }
}
