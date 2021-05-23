package fi.dy.masa.malilib.gui.widget.button;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.option.OptionListConfig;
import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.malilib.render.text.StyledTextLine;
import fi.dy.masa.malilib.util.StringUtils;

public class OptionListConfigButton extends GenericButton
{
    protected final OptionListConfig<?> config;
    @Nullable protected final String prefixTranslationKey;
    @Nullable protected EventListener changeListener;

    public OptionListConfigButton(int width, int height, OptionListConfig<?> config)
    {
        this(width, height, config, null);
    }

    public OptionListConfigButton(int width, int height, OptionListConfig<?> config, @Nullable String prefixTranslationKey)
    {
        super(width, height, "");

        this.config = config;
        this.prefixTranslationKey = prefixTranslationKey;
        this.setHoverTextLineProvider("list_preview", this::getOptionListPreviewHoverString, 100);

        this.setActionListener((btn, mbtn) -> this.cycleValue(mbtn));
        this.updateDisplayString();
    }

    public void setChangeListener(@Nullable EventListener changeListener)
    {
        this.changeListener = changeListener;
    }

    protected void cycleValue(int mouseButton)
    {
        this.config.cycleValue(mouseButton != 0);
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

    protected List<StyledTextLine> getOptionListPreviewHoverString(List<StyledTextLine> previousLines)
    {
        return getOptionListPreviewHoverString(this.config, previousLines);
    }

    public static List<StyledTextLine> getOptionListPreviewHoverString(OptionListConfig<?> config, List<StyledTextLine> previousLines)
    {
        List<StyledTextLine> lines = new ArrayList<>();
        List<OptionListConfigValue> allValues = new ArrayList<>(config.getAllValues());
        Set<OptionListConfigValue> allowedValues = new HashSet<>(config.getAllowedValues());
        OptionListConfigValue currentValue = config.getValue();
        int totalValues = allValues.size();
        int allowedValuesCount = config.getAllowedValues().size();

        if (previousLines.isEmpty() == false)
        {
            lines.add(StyledTextLine.EMPTY);
        }

        if (totalValues == allowedValuesCount)
        {
            lines.add(StyledTextLine.translate("malilib.gui.label.option_list_hover.total_values.all", totalValues));
        }
        else
        {
            lines.add(StyledTextLine.translate("malilib.gui.label.option_list_hover.total_values.allowed", allowedValuesCount, totalValues));
        }

        for (OptionListConfigValue value : allValues)
        {
            if (allowedValues.contains(value))
            {
                if (currentValue.equals(value))
                {
                    lines.add(StyledTextLine.translate("malilib.hover_info.option_list_button.selected_value", value.getDisplayName()));
                }
                else
                {
                    lines.add(StyledTextLine.translate("malilib.hover_info.option_list_button.non_selected_value", value.getDisplayName()));
                }
            }
        }

        if (totalValues != allowedValuesCount)
        {
            lines.add(StyledTextLine.EMPTY);
            lines.add(StyledTextLine.translate("malilib.gui.label.option_list_hover.total_values.disallowed", totalValues - allowedValuesCount, totalValues));

            for (OptionListConfigValue value : allValues)
            {
                if (allowedValues.contains(value) == false)
                {
                    lines.add(StyledTextLine.translate("malilib.hover_info.option_list_button.non_selected_value", value.getDisplayName()));
                }
            }
        }

        return lines;
    }
}
