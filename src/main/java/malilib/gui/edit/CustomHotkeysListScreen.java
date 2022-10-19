package malilib.gui.edit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import malilib.MaLiLibConfigScreen;
import malilib.MaLiLibReference;
import malilib.gui.BaseListScreen;
import malilib.gui.BaseScreen;
import malilib.gui.TextInputScreen;
import malilib.gui.config.KeybindEditingScreen;
import malilib.gui.util.GuiUtils;
import malilib.gui.widget.button.GenericButton;
import malilib.gui.widget.button.KeyBindConfigButton;
import malilib.gui.widget.list.DataListWidget;
import malilib.gui.widget.list.entry.CustomHotkeyDefinitionEntryWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.input.CustomHotkeyDefinition;
import malilib.input.CustomHotkeyManager;
import malilib.input.KeyBind;
import malilib.input.KeyBindImpl;
import malilib.input.KeyBindSettings;
import malilib.input.Keys;

public class CustomHotkeysListScreen extends BaseListScreen<DataListWidget<CustomHotkeyDefinition>> implements KeybindEditingScreen
{
    protected final GenericButton addHotkeyButton;
    @Nullable protected KeyBindConfigButton activeKeyBindButton;

    public CustomHotkeysListScreen()
    {
        super(10, 74, 20, 80, MaLiLibReference.MOD_ID, MaLiLibConfigScreen.ALL_TABS, MaLiLibConfigScreen.GENERIC);

        this.setTitle("malilib.title.screen.configs.custom_hotkeys", MaLiLibReference.MOD_VERSION);

        this.addHotkeyButton = GenericButton.create(16, "malilib.button.custom_hotkeys.add_hotkey", this::openAddHotkeyScreen);
        this.addHotkeyButton.translateAndAddHoverString("malilib.hover.button.custom_hotkeys.add_new_hotkey");
        this.screenCloseListener = CustomHotkeyManager.INSTANCE::checkIfDirtyAndSaveAndUpdate;

        this.createSwitchModConfigScreenDropDown(MaLiLibReference.MOD_INFO);
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.addHotkeyButton);
        this.getListWidget().refreshEntries();
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        this.addHotkeyButton.setPosition(this.x + 10, this.y + 57);
    }

    protected void openAddHotkeyScreen()
    {
        String title = "malilib.title.screen.custom_hotkey_create";
        TextInputScreen screen = new TextInputScreen(title, "", this::openEditHotkeyScreen);
        screen.setParent(this);
        screen.setLabelText("malilib.label.custom_hotkeys.create.hotkey_name");
        screen.setInfoText("malilib.info.custom_hotkey.name_immutable");
        BaseScreen.openPopupScreen(screen);
    }

    protected boolean openEditHotkeyScreen(String name)
    {
        if (name.isEmpty())
        {
            return false;
        }

        KeyBind keyBind = KeyBindImpl.fromStorageString("", KeyBindSettings.INGAME_DEFAULT);
        CustomHotkeyDefinition hotkey = new CustomHotkeyDefinition(name, keyBind, ImmutableList.of());
        CustomHotkeyManager.INSTANCE.addCustomHotkey(hotkey);
        CustomHotkeyEditScreen screen = new CustomHotkeyEditScreen(hotkey);
        screen.setParent(this);
        BaseScreen.openScreen(screen);

        return true;
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onKeyTyped(keyCode, scanCode, modifiers);
            return true;
        }
        else
        {
            if (this.getListWidget().onKeyTyped(keyCode, scanCode, modifiers))
            {
                return true;
            }

            if (keyCode == Keys.KEY_ESCAPE && this.getParent() != GuiUtils.getCurrentScreen())
            {
                this.openParentScreen();
                return true;
            }

            return super.onKeyTyped(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (super.onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        // When clicking on not-a-button, clear the selection
        if (this.activeKeyBindButton != null && mouseButton == 0)
        {
            this.setActiveKeyBindButton(null);
            return true;
        }

        return false;
    }

    protected List<CustomHotkeyDefinition> getCustomHotkeyDefinitions()
    {
        List<CustomHotkeyDefinition> hotkeys = new ArrayList<>(CustomHotkeyManager.INSTANCE.getAllCustomHotkeys());
        hotkeys.sort(Comparator.comparing(CustomHotkeyDefinition::getName));
        return hotkeys;
    }

    @Override
    protected DataListWidget<CustomHotkeyDefinition> createListWidget()
    {
        DataListWidget<CustomHotkeyDefinition> listWidget = new DataListWidget<>(this::getCustomHotkeyDefinitions, true);
        
        listWidget.setListEntryWidgetFixedHeight(22);
        listWidget.setDataListEntryWidgetFactory(this::createWidget);

        return listWidget;
    }

    protected CustomHotkeyDefinitionEntryWidget createWidget(@Nullable CustomHotkeyDefinition data,
                                                             DataListEntryWidgetData constructData)
    {
        return new CustomHotkeyDefinitionEntryWidget(data, constructData, this);
    }

    @Override
    public void setActiveKeyBindButton(@Nullable KeyBindConfigButton button)
    {
        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onClearSelection();
        }

        this.activeKeyBindButton = button;

        if (this.activeKeyBindButton != null)
        {
            this.activeKeyBindButton.onSelected();
        }
    }
}
