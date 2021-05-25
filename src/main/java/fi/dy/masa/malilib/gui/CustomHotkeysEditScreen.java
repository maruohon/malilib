package fi.dy.masa.malilib.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import fi.dy.masa.malilib.MaLiLibConfigScreen;
import fi.dy.masa.malilib.MaLiLibReference;
import fi.dy.masa.malilib.gui.config.KeybindEditingScreen;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.button.KeyBindConfigButton;
import fi.dy.masa.malilib.gui.widget.list.DataListWidget;
import fi.dy.masa.malilib.gui.widget.list.entry.CustomHotkeyDefinitionEntryWidget;
import fi.dy.masa.malilib.input.CustomHotkeyDefinition;
import fi.dy.masa.malilib.input.CustomHotkeyManager;
import fi.dy.masa.malilib.input.HotkeyManager;

public class CustomHotkeysEditScreen extends BaseListScreen<DataListWidget<CustomHotkeyDefinition>> implements KeybindEditingScreen
{
    protected final GenericButton addHotkeyButton;
    @Nullable protected KeyBindConfigButton activeKeyBindButton;

    public CustomHotkeysEditScreen()
    {
        super(10, 74, 20, 84, MaLiLibReference.MOD_ID, MaLiLibConfigScreen.ALL_TABS, MaLiLibConfigScreen.GENERIC);

        this.setTitle("malilib.gui.title.manage_custom_hotkeys");

        this.addHotkeyButton = GenericButton.simple("malilib.gui.button.add_new_hotkey", this::openAddHotkeyScreen);
        this.addHotkeyButton.translateAndAddHoverStrings("malilib.gui.button.hover.add_new_hotkey");
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

        this.addHotkeyButton.setPosition(this.x + 10, this.y + 50);
    }

    @Override
    public void onGuiClosed()
    {
        CustomHotkeyManager.INSTANCE.checkIfDirty();
        CustomHotkeyManager.INSTANCE.saveToFileIfDirty();
        HotkeyManager.INSTANCE.updateUsedKeys();

        super.onGuiClosed();
    }

    protected void openAddHotkeyScreen()
    {
        AddCustomHotkeyDefinitionScreen screen = new AddCustomHotkeyDefinitionScreen();
        screen.setParent(this);
        BaseScreen.openPopupScreen(screen);
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

            if (keyCode == Keyboard.KEY_ESCAPE && this.getParent() != GuiUtils.getCurrentScreen())
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

    @Nullable
    @Override
    protected DataListWidget<CustomHotkeyDefinition> createListWidget(int listX, int listY, int listWidth, int listHeight)
    {
        DataListWidget<CustomHotkeyDefinition> listWidget = new DataListWidget<>(0, 0, 200, 120, this::getCustomHotkeyDefinitions);
        listWidget.setListEntryWidgetFixedHeight(22);
        listWidget.setFetchFromSupplierOnRefresh(true);
        listWidget.setEntryWidgetFactory(this::createWidget);
        return listWidget;
    }

    protected CustomHotkeyDefinitionEntryWidget createWidget(int x, int y, int width, int height, int listIndex,
                                                             int originalListIndex, @Nullable CustomHotkeyDefinition data,
                                                             @Nullable DataListWidget<? extends CustomHotkeyDefinition> listWidget)
    {
        return new CustomHotkeyDefinitionEntryWidget(x, y, width, height, listIndex, originalListIndex,
                                                     data, listWidget, this);
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
