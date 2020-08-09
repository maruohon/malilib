package fi.dy.masa.malilib.gui.widget;

import org.lwjgl.input.Keyboard;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.value.BaseConfigOptionListEntry;
import fi.dy.masa.malilib.config.value.ConfigOptionListEntry;
import fi.dy.masa.malilib.gui.button.KeyBindConfigButton;
import fi.dy.masa.malilib.gui.config.KeybindEditingScreen;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.widget.list.entry.SelectionListener;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindImpl;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.input.KeyBindSettings.Context;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigsSearchBarWidget extends SearchBarWidget
{
    protected final KeyBindImpl searchKey;
    protected final KeyBindConfigButton hotkeySearchButton;
    protected final DropDownListWidget<Scope> sourceSelectionDropdown;
    protected int openedHeight;

    public ConfigsSearchBarWidget(int x, int y, int width, int openedHeight, int searchBarOffsetX,
                                  Icon iconSearch, HorizontalAlignment iconAlignment,
                                  SelectionListener<Scope> scopeChangeListener,
                                  KeybindEditingScreen screen)
    {
        super(x, y + 3, width - 160, 14, searchBarOffsetX, iconSearch, iconAlignment);

        this.openedHeight = openedHeight;

        KeyBindSettings settings = KeyBindSettings.create(Context.ANY, KeyAction.BOTH, true, true, false, false, false);
        this.searchKey = KeyBindImpl.fromStorageString("", "", settings);

        this.hotkeySearchButton = new KeyBindConfigButton(x + width - 150, y, 100, 20, this.searchKey, screen);
        this.hotkeySearchButton.setUpdateKeyBindImmediately();
        this.hotkeySearchButton.addHoverStrings("malilib.gui.button.hover.hotkey_search_button");
        this.hotkeySearchButton.setHoverInfoRequiresShift(false);

        this.sourceSelectionDropdown = new DropDownListWidget<>(x, y - 16, -1, 15, 60, 3, Scope.VALUES, Scope::getDisplayName);
        this.sourceSelectionDropdown.setSelectedEntry(Scope.CURRENT_CATEGORY);
        this.sourceSelectionDropdown.setSelectionListener(scopeChangeListener);
    }

    public KeyBind getKeybind()
    {
        return this.searchKey;
    }

    public Scope getCurrentScope()
    {
        return this.isSearchOpen() ? this.sourceSelectionDropdown.getSelectedEntry() : Scope.CURRENT_CATEGORY;
    }

    @Override
    public void reAddSubWidgets()
    {
        // Don't call super, so that we can add the dropdown widget before the text field,
        // so that the dropdown can handle the mouse click first even when it overlaps the text field.
        // Although this should now be fixed by the new input dispatch to the top hovered widget first.

        this.clearWidgets();

        this.addWidget(this.buttonSearchToggle);

        if (this.isSearchOpen())
        {
            this.addWidget(this.sourceSelectionDropdown);
            this.addWidget(this.hotkeySearchButton);
            this.addWidget(this.textField);
        }
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int x = this.getX();
        int y = this.getY();
        int width = this.getWidth();
        int height = this.getHeight();

        this.sourceSelectionDropdown.setPosition(x + 18, y);
        this.hotkeySearchButton.setPosition(x + width - this.hotkeySearchButton.getWidth() - 1, y + height - this.hotkeySearchButton.getHeight());
        this.textField.setY(y + this.sourceSelectionDropdown.getHeight() + 2);
        this.textField.setWidth(width - 120);
    }

    @Override
    public int getHeight()
    {
        return this.isSearchOpen() ? this.openedHeight : super.getHeight();
    }

    @Override
    public boolean hasFilter()
    {
        return super.hasFilter() || (this.isSearchOpen() && this.searchKey.getKeys().size() > 0);
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isSearchOpen())
        {
            if (this.hotkeySearchButton.isMouseOver(mouseX, mouseY))
            {
                boolean selectedPre = this.hotkeySearchButton.isSelected();
                this.hotkeySearchButton.onMouseClicked(mouseX, mouseY, mouseButton);

                if (selectedPre == false)
                {
                    this.hotkeySearchButton.onSelected();
                }

                return true;
            }
            else if (this.hotkeySearchButton.isSelected())
            {
                this.hotkeySearchButton.onClearSelection();
                return true;
            }
        }

        return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }

    @Override
    protected boolean onKeyTypedImpl(char typedChar, int keyCode)
    {
        if (this.isSearchOpen() && this.hotkeySearchButton.isSelected())
        {
            this.hotkeySearchButton.onKeyPressed(keyCode);

            if (keyCode == Keyboard.KEY_ESCAPE)
            {
                this.hotkeySearchButton.onClearSelection();
            }

            return true;
        }

        return super.onKeyTypedImpl(typedChar, keyCode);
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        super.render(mouseX, mouseY, isActiveGui, hovered);

        if (this.isSearchOpen())
        {
            this.hotkeySearchButton.render(mouseX, mouseY, isActiveGui, hovered);
        }
    }

    public enum Scope implements ConfigOptionListEntry<Scope>
    {
        CURRENT_CATEGORY ("malilib.gui.label.config_scope.current_category"),
        ALL_CATEGORIES   ("malilib.gui.label.config_scope.all_categories"),
        ALL_MODS         ("malilib.gui.label.config_scope.all_mods");

        public static final ImmutableList<Scope> VALUES = ImmutableList.copyOf(values());

        private final String translationKey;

        Scope(String translationKey)
        {
            this.translationKey = translationKey;
        }

        @Override
        public String getStringValue()
        {
            return this.translationKey;
        }

        @Override
        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }

        @Override
        public Scope cycle(boolean forward)
        {
            return BaseConfigOptionListEntry.cycleValue(VALUES, this.ordinal(), forward);
        }

        @Override
        public Scope fromString(String name)
        {
            return BaseConfigOptionListEntry.findValueByName(name, VALUES);
        }
    }
}
