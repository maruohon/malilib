package fi.dy.masa.malilib.gui.widget;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.widget.util.TextFieldValidator;
import fi.dy.masa.malilib.util.StringUtils;

public class DoubleTextFieldWidget extends BaseTextFieldWidget
{
    public DoubleTextFieldWidget(int x, int y, int width, int height)
    {
        this(x, y, width, height, 0);
    }

    public DoubleTextFieldWidget(int x, int y, int width, int height, double value)
    {
        this(x, y, width, height, value, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public DoubleTextFieldWidget(int x, int y, int width, int height, double initialValue, double minValue, double maxValue)
    {
        this(x, y, width, height, String.valueOf(initialValue), minValue, maxValue);
    }

    public DoubleTextFieldWidget(int x, int y, int width, int height, String initialValue, double minValue, double maxValue)
    {
        super(x, y, width, height, initialValue);

        this.setTextValidator(new DoubleValidator(minValue, maxValue));
    }

    public static class DoubleValidator implements TextFieldValidator
    {
        protected final double maxValue;
        protected final double minValue;

        public DoubleValidator(double minValue, double maxValue)
        {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public boolean isValidInput(String text)
        {
            try
            {
                double value = Double.parseDouble(text);
                return value >= this.minValue && value <= this.maxValue;
            }
            catch (Exception e)
            {
            }

            return false;
        }

        @Override
        @Nullable
        public String getErrorMessage(String text)
        {
            try
            {
                double value = Double.parseDouble(text);

                if (value < this.minValue)
                {
                    return StringUtils.translate("malilib.message.error.textfield.value_below_min", String.valueOf(value), String.valueOf(this.minValue));
                }
                else if (value > this.maxValue)
                {
                    return StringUtils.translate("malilib.message.error.textfield.value_above_max", String.valueOf(value), String.valueOf(this.maxValue));
                }
                else if (Double.isNaN(value))
                {
                    return StringUtils.translate("malilib.message.error.textfield.invalid_value_floating_point_number", text);
                }
            }
            catch (Exception e)
            {
                return StringUtils.translate("malilib.message.error.textfield.invalid_value_floating_point_number", text);
            }

            return null;
        }
    }
}
