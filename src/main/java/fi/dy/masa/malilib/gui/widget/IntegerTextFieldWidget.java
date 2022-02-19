package fi.dy.masa.malilib.gui.widget;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.widget.util.TextFieldValidator;
import fi.dy.masa.malilib.util.StringUtils;

public class IntegerTextFieldWidget extends BaseTextFieldWidget
{
    public IntegerTextFieldWidget(int width, int height)
    {
        this(width, height, 0);
    }

    public IntegerTextFieldWidget(int width, int height, int value)
    {
        this(width, height, value, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public IntegerTextFieldWidget(int width, int height, int initialValue, int minValue, int maxValue)
    {
        this(width, height, String.valueOf(initialValue), minValue, maxValue);
    }

    public IntegerTextFieldWidget(int width, int height, String initialValue, int minValue, int maxValue)
    {
        super(width, height, initialValue);

        this.setTextValidator(new IntValidator(minValue, maxValue));
    }

    public static class IntValidator implements TextFieldValidator
    {
        protected final int maxValue;
        protected final int minValue;

        public IntValidator(int minValue, int maxValue)
        {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public boolean isValidInput(String text)
        {
            try
            {
                int value = Integer.parseInt(text);
                return value >= this.minValue && value <= this.maxValue;
            }
            catch (Exception ignore) {}

            return false;
        }

        @Override
        @Nullable
        public String getErrorMessage(String text)
        {
            try
            {
                int value = Integer.parseInt(text);

                if (value < this.minValue)
                {
                    return StringUtils.translate("malilib.message.error.text_field.value_below_min", String.valueOf(value), String.valueOf(this.minValue));
                }
                else if (value > this.maxValue)
                {
                    return StringUtils.translate("malilib.message.error.text_field.value_above_max", String.valueOf(value), String.valueOf(this.maxValue));
                }
            }
            catch (Exception e)
            {
                return StringUtils.translate("malilib.message.error.text_field.invalid_value_int", text);
            }

            return null;
        }
    }
}
