package fi.dy.masa.malilib;

import java.util.Collections;
import java.util.List;
import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.reference.MaLiLibReference;
import net.minecraft.client.resource.language.I18n;

public class MaLiLibConfigGui extends GuiConfigsBase
{
    private static ConfigGuiTab tab = ConfigGuiTab.GENERIC;
    private int id;

    public MaLiLibConfigGui()
    {
        super(10, 50, MaLiLibReference.MOD_ID, null);

        this.title = I18n.translate("malilib.gui.title.configs");
    }

    @Override
    public void init()
    {
        super.init();
        this.clearOptions();

        this.id = 0;
        int x = 10;
        int y = 26;

        for (ConfigGuiTab tab : ConfigGuiTab.values())
        {
            x += this.createButton(x, y, -1, tab) + 4;
        }
    }

    private int createButton(int x, int y, int width, ConfigGuiTab tab)
    {
        ButtonListener listener = new ButtonListener(tab, this);
        boolean enabled = MaLiLibConfigGui.tab != tab;
        String label = tab.getDisplayName();

        if (width < 0)
        {
            width = this.textRenderer.getStringWidth(label) + 10;
        }

        ButtonGeneric button = new ButtonGeneric(this.id++, x, y, width, 20, label);
        button.active = enabled;
        this.addButton(button, listener);

        return width;
    }

    @Override
    protected int getConfigWidth()
    {
        ConfigGuiTab tab = MaLiLibConfigGui.tab;

        if (tab == ConfigGuiTab.GENERIC)
        {
            return 200;
        }

        return super.getConfigWidth();
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        List<? extends IConfigBase> configs;
        ConfigGuiTab tab = MaLiLibConfigGui.tab;

        if (tab == ConfigGuiTab.GENERIC)
        {
            configs = MaLiLibConfigs.Generic.OPTIONS;
        }
        else if (tab == ConfigGuiTab.DEBUG)
        {
            configs = ImmutableList.of(KeybindMulti.KEYBIND_DEBUG, KeybindMulti.KEYBIND_DEBUG_ACTIONBAR);
        }
        else
        {
            return Collections.emptyList();
        }

        return ConfigOptionWrapper.createFor(configs);
    }

    private static class ButtonListener implements IButtonActionListener<ButtonGeneric>
    {
        private final MaLiLibConfigGui parent;
        private final ConfigGuiTab tab;

        public ButtonListener(ConfigGuiTab tab, MaLiLibConfigGui parent)
        {
            this.tab = tab;
            this.parent = parent;
        }

        @Override
        public void actionPerformed(ButtonGeneric control)
        {
        }

        @Override
        public void actionPerformedWithButton(ButtonGeneric control, int mouseButton)
        {
            MaLiLibConfigGui.tab = this.tab;

            this.parent.reCreateListWidget(); // apply the new config width
            this.parent.getListWidget().resetScrollbarPosition();
            this.parent.init();
        }
    }

    public enum ConfigGuiTab
    {
        GENERIC ("malilib.gui.button.config_gui.generic"),
        DEBUG   ("malilib.gui.button.config_gui.debug");

        private final String translationKey;

        private ConfigGuiTab(String translationKey)
        {
            this.translationKey = translationKey;
        }

        public String getDisplayName()
        {
            return I18n.translate(this.translationKey);
        }
    }
}
