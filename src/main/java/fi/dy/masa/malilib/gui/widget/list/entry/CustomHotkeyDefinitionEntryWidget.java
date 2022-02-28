package fi.dy.masa.malilib.gui.widget.list.entry;

import java.util.ArrayList;
import fi.dy.masa.malilib.action.MacroAction;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.edit.CustomHotkeyEditScreen;
import fi.dy.masa.malilib.gui.edit.CustomHotkeysListScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.KeybindSettingsWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.button.KeyBindConfigButton;
import fi.dy.masa.malilib.input.CustomHotkeyDefinition;
import fi.dy.masa.malilib.input.CustomHotkeyManager;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.render.text.TextStyle;

public class CustomHotkeyDefinitionEntryWidget extends BaseDataListEntryWidget<CustomHotkeyDefinition>
{
    protected final CustomHotkeysListScreen screen;
    protected final LabelWidget nameLabelWidget;
    protected final KeyBindConfigButton keybindButton;
    protected final KeybindSettingsWidget settingsWidget;
    protected final GenericButton editButton;
    protected final GenericButton removeButton;

    public CustomHotkeyDefinitionEntryWidget(CustomHotkeyDefinition data,
                                             DataListEntryWidgetData constructData,
                                             CustomHotkeysListScreen screen)
    {
        super(data, constructData);

        this.screen = screen;

        TextStyle actionStyle = TextStyle.normal(0xFFC0C0C0);
        StyledTextLine name = StyledTextLine.translate("malilib.label.custom_hotkeys.widget.hotkey_name", data.getName());
        StyledTextLine actionName = data.getActionDisplayName().withStartingStyle(actionStyle);

        this.nameLabelWidget = new LabelWidget(-1, this.getHeight(), 0xFFF0F0F0);
        this.nameLabelWidget.getPadding().setTop(2).setLeft(4);
        this.nameLabelWidget.setLabelStyledTextLines(name, actionName);

        this.keybindButton = new KeyBindConfigButton(120, 20, data.getKeyBind(), screen);
        this.settingsWidget = new KeybindSettingsWidget(data.getKeyBind(), data.getName());
        this.editButton = GenericButton.create("malilib.button.misc.edit", this::editHotkey);
        this.removeButton = GenericButton.create("malilib.button.misc.remove", this::removeHotkey);

        this.getBackgroundRenderer().getNormalSettings().setEnabled(true);
        this.getBackgroundRenderer().getNormalSettings().setColor(this.isOdd ? 0x30707070 : 0x50707070);
        this.getBackgroundRenderer().getHoverSettings().setColor(0x50909090);

        ArrayList<StyledTextLine> lines = new ArrayList<>();
        MacroAction.getContainedActionsTooltip(data.getActionList(), lines::add, 8);
        this.hoverInfoFactory.addTextLines(lines);
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.nameLabelWidget);
        this.addWidget(this.keybindButton);
        this.addWidget(this.settingsWidget);
        this.addWidget(this.editButton);
        this.addWidget(this.removeButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getX();
        int y = this.getY();

        this.nameLabelWidget.setPosition(x, y);

        this.removeButton.setRight(this.getRight() - 2);
        this.removeButton.centerVerticallyInside(this);

        this.settingsWidget.setRight(this.removeButton.getX() - 2);
        this.settingsWidget.centerVerticallyInside(this);

        this.keybindButton.setRight(this.settingsWidget.getX() - 2);
        this.keybindButton.centerVerticallyInside(this);

        this.editButton.setRight(this.keybindButton.getX() - 2);
        this.editButton.centerVerticallyInside(this);
    }

    @Override
    public void updateWidgetState()
    {
        super.updateWidgetState();

        this.updateHoverStrings();
        this.keybindButton.updateHoverStrings();
    }

    protected void removeHotkey()
    {
        this.scheduleTask(() -> {
            CustomHotkeyManager.INSTANCE.removeCustomHotkey(this.data);
            this.screen.getListWidget().refreshEntries();
        });
    }

    protected void editHotkey()
    {
        CustomHotkeyEditScreen screen = new CustomHotkeyEditScreen(this.data);
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }
}
