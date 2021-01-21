package fi.dy.masa.malilib.gui.widget.button;

import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.util.StringUtils;

public class OptionListConfigButton extends GenericButton
{
    protected final OptionListConfig<?> config;
    @Nullable protected final String prefixTranslationKey;
    @Nullable protected EventListener changeListener;

    public OptionListConfigButton(int x, int y, int width, int height, OptionListConfig<?> config)
    {
        this(x, y, width, height, config, null);
    }

    public OptionListConfigButton(int x, int y, int width, int height, OptionListConfig<?> config, @Nullable String prefixTranslationKey)
    {
        super(x, y, width, height, "");

        this.config = config;
        this.prefixTranslationKey = prefixTranslationKey;
        this.hoverInfoFactory.setStringListProvider("list_preview", this::getOptionListPreviewHoverString);

        this.setActionListener((btn, mbtn) -> this.cycleValue(mbtn));
        this.updateDisplayString();
    }

    public void setChangeListener(@Nullable EventListener changeListener)
    {
        this.changeListener = changeListener;
    }

    protected void cycleValue(int mouseButton)
    {
        this.config.cycleValue(mouseButton == 0);
        this.updateDisplayString();

        if (this.changeListener != null)
        {
            this.changeListener.onEvent();
        }
    }

    @Override
    protected String generateDisplayString()
    {
        String configDisplayText = this.config.getValue().getDisplayName();

        if (this.prefixTranslationKey != null)
        {
            return StringUtils.translate(this.prefixTranslationKey, configDisplayText);
        }
        else
        {
            return configDisplayText;
        }
    }

    protected List<String> getOptionListPreviewHoverString()
    {
        //int options = this.config.;

        return EMPTY_STRING_LIST;
    }
}
