package malilib.overlay.message;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableList;

import malilib.config.option.HotkeyedBooleanConfig;
import malilib.render.text.StyledText;
import malilib.render.text.StyledTextLine;
import malilib.util.data.BooleanStorage;

public class NagHelper
{
    protected final BooleanStorage mainNagOption;
    protected final BooleanStorage nagDisableReminderOption;
    protected final BooleanStorage newUserExtraNagOption;
    protected final ArrayList<StyledTextLine> lines = new ArrayList<>();
    @Nullable protected final String nagDisableReminderKey;
    @Nullable protected final String extraNagDisableKey;

    public NagHelper(BooleanStorage mainNagOption,
                     BooleanStorage nagDisableReminderOption,
                     BooleanStorage newUserExtraNagOption,
                     @Nullable String nagDisableReminderKey,
                     @Nullable String extraNagDisableKey)
    {
        this.mainNagOption = mainNagOption;
        this.nagDisableReminderOption = nagDisableReminderOption;
        this.newUserExtraNagOption = newUserExtraNagOption;
        this.nagDisableReminderKey = nagDisableReminderKey;
        this.extraNagDisableKey = extraNagDisableKey;
    }

    public StyledText appendNag(StyledText input)
    {
        StyledText text = input;

        if (this.nagDisableReminderOption.getBooleanValue() && this.nagDisableReminderKey != null)
        {
            StyledText nagDisableText = StyledText.translate(this.nagDisableReminderKey);
            text = text.append(1, nagDisableText.lines, 0);
        }

        if (this.newUserExtraNagOption.getBooleanValue() && this.extraNagDisableKey != null)
        {
            StyledText extraNagText = StyledText.translate(this.extraNagDisableKey);
            text = text.append(1, extraNagText.lines, 0);
        }

        return text;
    }

    public void showNag(MessageDispatcher dispatcher, Supplier<StyledText> textSupplier)
    {
        if (this.mainNagOption.getBooleanValue() == false)
        {
            return;
        }

        if (this.newUserExtraNagOption.getBooleanValue())
        {
            dispatcher.time(12000);
        }

        StyledText text = textSupplier.get();
        text = this.appendNag(text);
        dispatcher.send(text);
    }

    public void showNag(String translationKey, Object... args)
    {
        this.showNag(MessageDispatcher.generic(), translationKey, args);
    }

    public void showNag(MessageDispatcher dispatcher, String translationKey, Object... args)
    {
        this.showNag(dispatcher, () -> StyledText.translate(translationKey, args));
    }

    public void showNagIfConfigDisabled(HotkeyedBooleanConfig config, String translationKey)
    {
        this.showNagIfConfigDisabled(MessageDispatcher.error(), config, translationKey);
    }

    public void showNagIfConfigDisabled(MessageDispatcher dispatcher, HotkeyedBooleanConfig config, String translationKey)
    {
        MessageUtils.getErrorMessageIfConfigDisabled(config, translationKey, msg -> this.showNag(dispatcher, () -> msg));
    }

    public NagHelper startNag()
    {
        this.lines.clear();
        return this;
    }

    public void buildNag(MessageDispatcher dispatcher)
    {
        if (this.lines.isEmpty() == false)
        {
            this.showNag(dispatcher, () -> StyledText.ofLines(ImmutableList.copyOf(this.lines)));
            this.lines.clear();
        }
    }

    public NagHelper pushNag(StyledText text)
    {
        return this.pushNag(text.lines);
    }

    public NagHelper pushNag(List<StyledTextLine> lines)
    {
        this.lines.addAll(lines);
        return this;
    }

    public NagHelper pushNagIfConfigDisabled(HotkeyedBooleanConfig config, String translationKey)
    {
        MessageUtils.getErrorMessageIfConfigDisabled(config, translationKey, this::pushNag);
        return this;
    }
}
