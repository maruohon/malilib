package fi.dy.masa.malilib.gui.widget;

import org.lwjgl.input.Keyboard;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.value.BaseConfigOptionListEntry;
import fi.dy.masa.malilib.config.value.BlockSnap;
import fi.dy.masa.malilib.config.value.ConfigOptionListEntry;
import fi.dy.masa.malilib.gui.button.KeyBindConfigButton;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.widget.list.entry.SelectionListener;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBindImpl;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.input.KeyBindSettings.Context;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigsSearchBarWidget extends SearchBarWidget
{
    protected final KeyBindImpl searchKey;
    protected final KeyBindConfigButton button;
    protected final DropDownListWidget<Scope> sourceSelectionDropdown;

    public ConfigsSearchBarWidget(int x, int y, int width, int height, int searchBarOffsetX,
                                  Icon iconSearch, HorizontalAlignment iconAlignment,
                                  SelectionListener<Scope> scopeChangeListener)
    {
        super(x, y + 3, width - 160, 14, searchBarOffsetX, iconSearch, iconAlignment);

        KeyBindSettings settings = KeyBindSettings.create(Context.ANY, KeyAction.BOTH, true, true, false, false, false);
        this.searchKey = KeyBindImpl.fromStorageString("", "", settings);

        this.button = new KeyBindConfigButton(x + width - 150, y, 100, 20, this.searchKey, null);
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
        super.reAddSubWidgets();

        if (this.searchOpen)
        {
            this.addWidget(this.button);
            this.addWidget(this.sourceSelectionDropdown);
        }
    }

    @Override
    public void updateSubWidgetsToGeometryChanges()
    {
        super.updateSubWidgetsToGeometryChanges();

        int x = this.getX();
        int y = this.getY();
        int width = this.getWidth();

        this.sourceSelectionDropdown.setPosition(x + 18, y - 16);
        this.button.setPosition(x + width - 100, y);
        this.textField.setWidth(width - 120);
    }

    @Override
    public boolean hasFilter()
    {
        return super.hasFilter() || (this.searchOpen && this.searchKey.getKeys().size() > 0);
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.searchOpen)
        {
            if (this.button.isMouseOver(mouseX, mouseY))
            {
                boolean selectedPre = this.button.isSelected();
                this.button.onMouseClicked(mouseX, mouseY, mouseButton);

                if (selectedPre == false)
                {
                    this.button.onSelected();
                }

                return true;
            }
            else if (this.button.isSelected())
            {
                this.button.onClearSelection();
                return true;
            }
        }

        return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }

    @Override
    protected boolean onKeyTypedImpl(char typedChar, int keyCode)
    {
        if (this.searchOpen && this.button.isSelected())
        {
            this.button.onKeyPressed(keyCode);

            if (keyCode == Keyboard.KEY_ESCAPE)
            {
                this.button.onClearSelection();
            }

            return true;
        }

        return super.onKeyTypedImpl(typedChar, keyCode);
    }

    @Override
    public void render(int mouseX, int mouseY, boolean isActiveGui, boolean hovered)
    {
        super.render(mouseX, mouseY, isActiveGui, hovered);

        if (this.searchOpen)
        {
            this.button.render(mouseX, mouseY, isActiveGui, hovered);
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
