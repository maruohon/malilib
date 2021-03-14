package fi.dy.masa.malilib.gui.widget;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.lwjgl.input.Keyboard;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.MaLiLibConfigs;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.ConfirmActionScreen;
import fi.dy.masa.malilib.gui.config.ConfigSearchInfo;
import fi.dy.masa.malilib.gui.config.ConfigWidgetRegistry;
import fi.dy.masa.malilib.gui.config.KeybindEditingScreen;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.position.HorizontalAlignment;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.button.KeyBindConfigButton;
import fi.dy.masa.malilib.input.CancelCondition;
import fi.dy.masa.malilib.input.Context;
import fi.dy.masa.malilib.input.KeyAction;
import fi.dy.masa.malilib.input.KeyBind;
import fi.dy.masa.malilib.input.KeyBindImpl;
import fi.dy.masa.malilib.input.KeyBindSettings;
import fi.dy.masa.malilib.listener.ConfirmationListener;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.listener.TextChangeListener;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigsSearchBarWidget extends SearchBarWidget
{
    protected final KeyBindImpl searchKey;
    protected final KeyBindConfigButton hotkeySearchButton;
    protected final GenericButton resetConfigsButton;
    protected final DropDownListWidget<Scope> sourceSelectionDropdown;
    protected final DropDownListWidget<TypeFilter> typeFilterDropdown;
    protected int openedHeight;

    public ConfigsSearchBarWidget(int x, int y, int width, int openedHeight, int searchBarOffsetX,
                                  MultiIcon iconSearch, HorizontalAlignment iconAlignment,
                                  TextChangeListener textChangeListener,
                                  final EventListener filterChangeListener,
                                  ConfirmationListener configResetter,
                                  KeybindEditingScreen screen)
    {
        super(x, y + 3, width - 160, 14, searchBarOffsetX, iconSearch, iconAlignment, textChangeListener);

        this.openedHeight = openedHeight;

        KeyBindSettings settings = KeyBindSettings.create(Context.ANY, KeyAction.BOTH, true, true, false, CancelCondition.NEVER, false);
        this.searchKey = KeyBindImpl.fromStorageString("", settings);

        this.hotkeySearchButton = new KeyBindConfigButton(x + width - 150, y, 160, 20, this.searchKey, screen);
        this.hotkeySearchButton.setUpdateKeyBindImmediately();
        this.hotkeySearchButton.translateAndAddHoverStrings("malilib.gui.button.hover.hotkey_search_button");
        this.hotkeySearchButton.setHoverInfoRequiresShift(false);
        this.hotkeySearchButton.setValueChangeListener(filterChangeListener);

        this.resetConfigsButton = new GenericButton(x + width - 150, y, 160, 20, "malilib.gui.button.config.reset_all_filtered");
        this.resetConfigsButton.translateAndAddHoverStrings("malilib.gui.button.hover.config.reset_all_filtered");
        final ConfirmActionScreen confirmScreen = new ConfirmActionScreen(240, "malilib.gui.title.confirm_config_reset", configResetter, GuiUtils.getCurrentScreen(), "malilib.gui.label.confirm_config_reset");
        this.resetConfigsButton.setActionListener((btn, mbtn) -> BaseScreen.openPopupScreen(confirmScreen));

        this.sourceSelectionDropdown = new DropDownListWidget<>(x, y - 16, -1, 15, 60, 10, Scope.VALUES, Scope::getDisplayName, null);
        this.sourceSelectionDropdown.setSelectedEntry(MaLiLibConfigs.Generic.CONFIG_SEARCH_DEFAULT_SCOPE.getValue());
        this.sourceSelectionDropdown.setSelectionListener((s) -> filterChangeListener.onEvent());
        this.sourceSelectionDropdown.setOpenStateHoverText(StringUtils.translate("malilib.gui.label.hover.config_search_default_scope"));

        this.typeFilterDropdown = new DropDownListWidget<>(x + 100, y - 16, -1, 15, 160, 10, TypeFilter.VALUES, TypeFilter::getDisplayName, null);
        this.typeFilterDropdown.setSelectedEntry(TypeFilter.ALL);
        this.typeFilterDropdown.setSelectionListener((s) -> filterChangeListener.onEvent());
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
            this.addWidget(this.typeFilterDropdown);
            this.addWidget(this.resetConfigsButton);
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
        this.typeFilterDropdown.setPosition(this.sourceSelectionDropdown.getRight() + 4, y);

        int btnX = x + width - this.hotkeySearchButton.getWidth() - 1;
        this.resetConfigsButton.setPosition(btnX, y - 9);
        this.hotkeySearchButton.setPosition(btnX, y + height - this.hotkeySearchButton.getHeight());
        this.textField.setY(y + this.sourceSelectionDropdown.getHeight() + 2);
        this.textField.setWidth(width - this.hotkeySearchButton.getWidth() - 20);
    }

    @Override
    public int getHeight()
    {
        return this.isSearchOpen() ? this.openedHeight : super.getHeight();
    }

    @Override
    public boolean hasFilter()
    {
        return super.hasFilter() || (this.isSearchOpen() &&
                                     (this.searchKey.getKeys().size() > 0 ||
                                      this.typeFilterDropdown.getSelectedEntry() != TypeFilter.ALL));
    }

    public boolean passesFilter(ConfigInfo config)
    {
        if (this.isSearchOpen())
        {
            @Nullable ConfigSearchInfo<ConfigInfo> info = ConfigWidgetRegistry.INSTANCE.getSearchInfo(config);

            if (this.searchKey.getKeys().size() > 0)
            {
                if (info == null || info.hasHotkey == false)
                {
                    return false;
                }

                KeyBind kb = info.getKeyBind(config);

                if (kb == null || kb.overlaps(this.searchKey) == false)
                {
                    return false;
                }
            }

            TypeFilter type = this.typeFilterDropdown.getSelectedEntry();
            return type == null || type.matches(info, config);
        }

        return true;
    }

    @Override
    protected boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.isSearchOpen())
        {
            if (this.hotkeySearchButton.isMouseOver(mouseX, mouseY))
            {
                boolean selectedPre = this.hotkeySearchButton.isSelected();
                this.hotkeySearchButton.tryMouseClick(mouseX, mouseY, mouseButton);

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

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (this.isSearchOpen() && this.hotkeySearchButton.isSelected())
        {
            this.hotkeySearchButton.onKeyPressed(keyCode, scanCode, modifiers);

            if (keyCode == Keyboard.KEY_ESCAPE)
            {
                this.hotkeySearchButton.onClearSelection();
            }

            return true;
        }

        return super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    public static class Scope extends BaseOptionListConfigValue
    {
        public static final Scope CURRENT_CATEGORY = new Scope("malilib.gui.label.config_scope.current_category");
        public static final Scope ALL_CATEGORIES   = new Scope("malilib.gui.label.config_scope.all_categories");
        public static final Scope ALL_MODS         = new Scope("malilib.gui.label.config_scope.all_mods");

        public static final ImmutableList<Scope> VALUES = ImmutableList.of(CURRENT_CATEGORY, ALL_CATEGORIES, ALL_MODS);

        private Scope(String translationKey)
        {
            super(translationKey, translationKey);
        }
    }

    public static class TypeFilter implements OptionListConfigValue
    {
        public static final List<TypeFilter> VALUES = new ArrayList<>();

        public static final TypeFilter ALL             = register("malilib.gui.label.config_type_filter.all",                (i, c) -> true);
        public static final TypeFilter MODIFIED        = register("malilib.gui.label.config_type_filter.modified",           (i, c) -> c.isModified());
        public static final TypeFilter MODIFIED_TOGGLE = register("malilib.gui.label.config_type_filter.modified_toggle",    (i, c) -> i != null && i.hasModifiedToggle(c));
        public static final TypeFilter ENABLED_TOGGLE  = register("malilib.gui.label.config_type_filter.enabled_toggle",     (i, c) -> i != null && i.hasEnabledToggle(c));
        public static final TypeFilter DISABLED_TOGGLE = register("malilib.gui.label.config_type_filter.disabled_toggle",    (i, c) -> i != null && i.hasDisabledToggle(c));
        public static final TypeFilter ANY_HOTKEY      = register("malilib.gui.label.config_type_filter.any_hotkey",         (i, c) -> i != null && i.hasHotkey);
        public static final TypeFilter MODIFIED_HOTKEY = register("malilib.gui.label.config_type_filter.modified_hotkey",    (i, c) -> i != null && i.hasModifiedHotkey(c));
        public static final TypeFilter BOUND_HOTKEY    = register("malilib.gui.label.config_type_filter.bound_hotkey",       (i, c) -> i != null && i.hasBoundHotkey(c));
        public static final TypeFilter UNBOUND_HOTKEY  = register("malilib.gui.label.config_type_filter.unbound_hotkey",     (i, c) -> i != null && i.hasUnboundHotkey(c));

        private final String translationKey;
        private final TypeFilterTest tester;

        public TypeFilter(String translationKey, TypeFilterTest tester)
        {
            this.translationKey = translationKey;
            this.tester = tester;
        }

        @Override
        public String getName()
        {
            return this.translationKey;
        }

        @Override
        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }

        public boolean matches(@Nullable ConfigSearchInfo<ConfigInfo> info, ConfigInfo config)
        {
            return this.tester.test(info, config);
        }

        public static TypeFilter register(String translationKey, TypeFilterTest tester)
        {
            TypeFilter filter = new TypeFilter(translationKey, tester);
            VALUES.add(filter);
            return filter;
        }
    }

    public interface TypeFilterTest
    {
        boolean test(ConfigSearchInfo<ConfigInfo> info, ConfigInfo config);
    }
}
