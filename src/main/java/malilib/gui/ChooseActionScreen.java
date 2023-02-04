package malilib.gui;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;

import malilib.gui.widget.RadioButtonWidget;
import malilib.listener.EventListener;

public class ChooseActionScreen<T> extends BaseConfirmActionScreen
{
    protected final Consumer<T> confirmListener;
    @Nullable protected final EventListener cancelListener;
    private final RadioButtonWidget<T> radioWidget;

    public ChooseActionScreen(int width,
                              String titleKey,
                              List<T> options, Function<T, String> optionDisplayStringFunction, T selectedValue,
                              Consumer<T> confirmListener,
                              String messageKey, Object... messageArgs)
    {
        this(width, titleKey, options, optionDisplayStringFunction, selectedValue, confirmListener, null,
             "malilib.button.misc.confirm.colored",
             "malilib.button.misc.cancel.colored",
             messageKey, messageArgs);
    }

    public ChooseActionScreen(int width,
                              String titleKey,
                              List<T> options, Function<T, String> optionDisplayStringFunction, T selectedValue,
                              Consumer<T> confirmListener,
                              @Nullable EventListener cancelListener,
                              String confirmButtonTranslationKey,
                              String cancelButtonTranslationKey,
                              String messageKey, Object... messageArgs)
    {
        super(width, titleKey, confirmButtonTranslationKey, cancelButtonTranslationKey, messageKey, messageArgs);

        this.confirmListener = confirmListener;
        this.cancelListener = cancelListener;
        this.radioWidget = new RadioButtonWidget<>(options, optionDisplayStringFunction);
        this.radioWidget.setSelection(selectedValue, false);

        this.setScreenWidthAndHeight(width, this.getRequiredScreenHeight());
        this.centerOnScreen();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.radioWidget);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 10;
        int y = this.labelWidget.getBottom() + 8;

        this.radioWidget.setPosition(x, y);
    }

    @Override
    protected int getRequiredScreenHeight()
    {
        return this.labelWidget.getHeight() + this.radioWidget.getHeight() + 60;
    }

    @Override
    protected void onConfirm()
    {
        this.openParentScreen();

        if (this.confirmListener != null)
        {
            T value = this.radioWidget.getSelection();

            if (value != null)
            {
                this.confirmListener.accept(value);
            }
        }
    }

    @Override
    protected void onCancel()
    {
        this.openParentScreen();

        if (this.cancelListener != null)
        {
            this.cancelListener.onEvent();
        }
    }

    public void setSelectedValue(T value)
    {
        this.radioWidget.setSelection(value, false);
    }
}
