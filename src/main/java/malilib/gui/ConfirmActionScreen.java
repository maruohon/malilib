package malilib.gui;

import javax.annotation.Nullable;

import malilib.listener.EventListener;

public class ConfirmActionScreen extends BaseConfirmActionScreen
{
    protected final EventListener confirmListener;
    @Nullable protected final EventListener cancelListener;

    public ConfirmActionScreen(int width,
                               String titleKey,
                               EventListener confirmListener,
                               String messageKey, Object... messageArgs)
    {
        this(width, titleKey, confirmListener, null,
             "malilib.button.misc.confirm.colored",
             "malilib.button.misc.cancel.colored",
             messageKey, messageArgs);
    }

    public ConfirmActionScreen(int width,
                               String titleKey,
                               EventListener confirmListener,
                               @Nullable EventListener cancelListener,
                               String confirmButtonTranslationKey,
                               String cancelButtonTranslationKey,
                               String messageKey, Object... messageArgs)
    {
        super(width, titleKey, confirmButtonTranslationKey, cancelButtonTranslationKey, messageKey, messageArgs);

        this.confirmListener = confirmListener;
        this.cancelListener = cancelListener;

        this.setScreenWidthAndHeight(width, this.getRequiredScreenHeight());
        this.centerOnScreen();
    }

    @Override
    protected void onConfirm()
    {
        this.openParentScreen();

        if (this.confirmListener != null)
        {
            this.confirmListener.onEvent();
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
}
