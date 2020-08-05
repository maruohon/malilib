package fi.dy.masa.malilib.gui.widget.list.entry.config;

import java.io.File;
import java.util.ArrayList;
import fi.dy.masa.malilib.config.option.ConfigInfo;
import fi.dy.masa.malilib.config.option.ConfigOption;
import fi.dy.masa.malilib.config.option.FileConfig;
import fi.dy.masa.malilib.config.option.SliderConfig;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.DirectorySelectorScreen;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.config.BaseConfigScreen;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.gui.util.BaseGuiIcon;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.WidgetLabel;
import fi.dy.masa.malilib.gui.widget.list.entry.BaseDataListEntryWidget;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.StringUtils;

public abstract class BaseConfigOptionWidget<C extends ConfigInfo> extends BaseDataListEntryWidget<C>
{
    protected final BaseConfigScreen gui;
    protected ButtonBase resetButton;

    public BaseConfigOptionWidget(int x, int y, int width, int height, int listIndex, C config, BaseConfigScreen gui)
    {
        super(x, y, width, height, listIndex, config);

        this.gui = gui;
    }

    protected void reCreateWidgets(int x, int y)
    {
        this.clearWidgets();

        WidgetLabel label = new WidgetLabel(x + 2, y + 6, 0xFFFFFFFF, this.data.getDisplayName());
        label.addHoverStrings(this.data.getComment());

        this.addWidget(label);
    }

    protected ButtonGeneric createResetButton(int x, int y, ConfigOption<?> config)
    {
        String labelReset = StringUtils.translate("malilib.gui.button.reset.caps");
        ButtonGeneric resetButton = new ButtonGeneric(x, y, -1, 20, labelReset);
        resetButton.setEnabled(config.isModified());

        return resetButton;
    }

    protected void addButtonsForButtonBasedConfigs(int resetX, int resetY, final ConfigOption<?> config, final ButtonBase configButton)
    {
        final ButtonGeneric resetButton = this.createResetButton(resetX, resetY, config);
        this.resetButton = resetButton;

        this.addButton(configButton, (btn, mbtn) -> resetButton.setEnabled(config.isModified()));
        this.addButton(resetButton, (btn, mbtn) -> {
            config.resetToDefault();
            configButton.updateDisplayString();
            resetButton.setEnabled(config.isModified());
        });
    }

    protected <T extends ConfigOption<?>> void addGenericResetButton(int x, int y, final T config)
    {
        final ButtonGeneric resetButton = this.createResetButton(x , y, config);
        this.resetButton = resetButton;

        resetButton.setActionListener((btn, mbtn) -> {
            config.resetToDefault();
            this.resetButton.setEnabled(config.isModified());
            this.reCreateWidgets(this.getX(), this.getY());
        });

        this.addWidget(resetButton);
    }

    protected <T extends ConfigOption<?> & SliderConfig> void addSliderToggleButton(int x, int y, T config)
    {
        IGuiIcon icon = config.shouldUseSlider() ? BaseGuiIcon.BTN_TXTFIELD : BaseGuiIcon.BTN_SLIDER;
        ButtonGeneric toggleBtn = new ButtonGeneric(x, y, icon);
        toggleBtn.addHoverStrings("malilib.gui.button.hover.text_field_slider_toggle");
        toggleBtn.setActionListener((btn, mbtn) -> {
            config.toggleUseSlider();
            this.reCreateWidgets(this.getX(), this.getY());
        });

        this.addWidget(toggleBtn);
    }

    protected ButtonGeneric createFileSelectorWidgets(int x, int y, final FileConfig config,
                                             final FileSelectorScreenFactory screenFactory, String buttonText, String hoverTextKey)
    {
        x += this.getMaxLabelWidth() + 10;
        int elementWidth = this.gui.getConfigElementsWidth();
        File file = FileUtils.getCanonicalFileIfPossible(config.getFile());

        ArrayList<String> lines = new ArrayList<>();
        StringUtils.splitTextToLines(lines, StringUtils.translate(hoverTextKey, file.getAbsolutePath()), 280);

        ButtonGeneric button = new ButtonGeneric(x, y + 1, elementWidth, 20, buttonText);
        button.addHoverStrings(lines);

        this.addButton(button, (btn, mbtn) -> {
            DirectorySelectorScreen browserScreen = screenFactory.create();
            browserScreen.setParent(GuiUtils.getCurrentScreen());
            BaseScreen.openGui(browserScreen);
        });

        final ButtonGeneric resetButton = this.createResetButton(x + elementWidth + 4, y + 1, config);
        this.resetButton = resetButton;

        this.addButton(resetButton, (btn, mbtn) -> {
            config.resetToDefault();
            this.reCreateWidgets(this.getX(), this.getY());
        });

        return button;
    }

    public int getMaxLabelWidth()
    {
        return this.gui.getMaxLabelWidth();
    }

    public boolean wasModified()
    {
        return false;
    }

    public interface FileSelectorScreenFactory
    {
        DirectorySelectorScreen create();
    }
}
