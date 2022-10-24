package malilib.gui.widget.list.entry.config;

import org.apache.commons.lang3.tuple.Pair;

import malilib.config.option.DualColorConfig;
import malilib.gui.config.ConfigWidgetContext;
import malilib.gui.widget.BaseTextFieldWidget;
import malilib.gui.widget.ColorIndicatorWidget;
import malilib.gui.widget.list.entry.DataListEntryWidgetData;
import malilib.util.data.Color4f;

public class DualColorConfigWidget extends BaseGenericConfigWidget<Pair<Color4f, Color4f>, DualColorConfig>
{
    protected final ColorIndicatorWidget colorIndicatorWidget1;
    protected final ColorIndicatorWidget colorIndicatorWidget2;
    protected final BaseTextFieldWidget textField1;
    protected final BaseTextFieldWidget textField2;
    protected final String initialStringValue1;
    protected final String initialStringValue2;

    public DualColorConfigWidget(DualColorConfig config,
                                 DataListEntryWidgetData constructData,
                                 ConfigWidgetContext ctx)
    {
        super(config, constructData, ctx);

        this.initialStringValue1 = this.initialValue.getLeft().toString();
        this.initialStringValue2 = this.initialValue.getRight().toString();

        this.colorIndicatorWidget1 = new ColorIndicatorWidget(18, 18, this.config::getFirstColorInt, (newValue) -> {
            this.config.setFirstColorFromInt(newValue);
            this.updateWidgetState();
        });
        this.colorIndicatorWidget2 = new ColorIndicatorWidget(18, 18, this.config::getSecondColorInt, (newValue) -> {
            this.config.setSecondColorFromInt(newValue);
            this.updateWidgetState();
        });

        this.colorIndicatorWidget1.translateAndAddHoverString(config.getFirstColorHoverInfoKey());
        this.colorIndicatorWidget2.translateAndAddHoverString(config.getSecondColorHoverInfoKey());

        this.colorIndicatorWidget1.getHoverInfoFactory()
                .setStringListProvider("locked", this.config::getLockAndOverrideMessages, 110);
        this.colorIndicatorWidget2.getHoverInfoFactory()
                .setStringListProvider("locked", this.config::getLockAndOverrideMessages, 110);

        this.textField1 = new BaseTextFieldWidget(70, 16, this.initialStringValue1);
        this.textField2 = new BaseTextFieldWidget(70, 16, this.initialStringValue2);
        this.textField1.setHoverStringProvider("locked", this.config::getLockAndOverrideMessages);
        this.textField2.setHoverStringProvider("locked", this.config::getLockAndOverrideMessages);

        this.textField1.setListener((str) -> {
            this.config.setValueFromStrings(str, this.textField2.getText());
            this.updateWidgetState();
        });

        this.textField2.setListener((str) -> {
            this.config.setValueFromStrings(this.textField1.getText(), str);
            this.updateWidgetState();
        });
    }

    @Override
    public void reAddSubWidgets()
    {
        super.reAddSubWidgets();

        this.addWidget(this.colorIndicatorWidget1);
        this.addWidget(this.colorIndicatorWidget2);
        this.addWidget(this.textField1);
        this.addWidget(this.textField2);
        this.addWidget(this.resetButton);
    }

    @Override
    public void updateSubWidgetPositions()
    {
        super.updateSubWidgetPositions();

        int x = this.getElementsStartPosition();
        int y = this.getY();
        int tw = 64;

        this.colorIndicatorWidget1.setPosition(x, y + 2);
        this.textField1.setWidth(tw);
        this.textField1.setPosition(this.colorIndicatorWidget1.getRight() + 4, y + 3);
        this.textField1.setEnabled(this.config.isLocked() == false);

        this.colorIndicatorWidget2.setPosition(this.textField1.getRight() + 4, y + 2);
        this.textField2.setWidth(tw);
        this.textField2.setPosition(this.colorIndicatorWidget2.getRight() + 4, y + 3);
        this.textField2.setEnabled(this.config.isLocked() == false);

        this.resetButton.setPosition(this.textField2.getRight() + 4, y + 1);
    }

    @Override
    public void updateWidgetState()
    {
        super.updateWidgetState();

        this.textField1.setText(this.config.getFirstColor().toString());
        this.textField2.setText(this.config.getSecondColor().toString());
        this.textField1.updateHoverStrings();
        this.textField2.updateHoverStrings();
        this.colorIndicatorWidget1.updateHoverStrings();
        this.colorIndicatorWidget2.updateHoverStrings();
    }

    @Override
    public void onAboutToDestroy()
    {
        String text1 = this.textField1.getText();
        String text2 = this.textField2.getText();

        if (text1.equals(this.initialStringValue1) == false ||
            text2.equals(this.initialStringValue2) == false)
        {
            this.config.setValueFromStrings(text1, text2);
        }
    }
}
