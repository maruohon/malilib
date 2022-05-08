package fi.dy.masa.malilib.gui.edit;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.BaseScreen;
import fi.dy.masa.malilib.gui.icon.BaseIcon;
import fi.dy.masa.malilib.gui.icon.BaseMultiIcon;
import fi.dy.masa.malilib.gui.icon.DefaultIcons;
import fi.dy.masa.malilib.gui.icon.Icon;
import fi.dy.masa.malilib.gui.icon.MultiIcon;
import fi.dy.masa.malilib.gui.util.ScreenContext;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.IntegerEditWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.ShapeRenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.Identifier;

public class CustomIconEditScreen extends BaseScreen
{
    protected final Icon originalIcon;
    protected final Consumer<Icon> iconConsumer;
    protected final LabelWidget textureNameLabel;
    protected final LabelWidget uLabel;
    protected final LabelWidget vLabel;
    protected final LabelWidget iconWidthLabel;
    protected final LabelWidget iconHeightLabel;
    protected final LabelWidget textureSheetWidthLabel;
    protected final LabelWidget textureSheetHeightLabel;
    protected final LabelWidget variantOffsetLabel;
    protected final LabelWidget previewLabel;
    protected final IntegerEditWidget uEditWidget;
    protected final IntegerEditWidget vEditWidget;
    protected final IntegerEditWidget iconWidthEditWidget;
    protected final IntegerEditWidget iconHeightEditWidget;
    protected final IntegerEditWidget textureSheetWidthEditWidget;
    protected final IntegerEditWidget textureSheetHeightEditWidget;
    protected final IntegerEditWidget variantOffsetUEditWidget;
    protected final IntegerEditWidget variantOffsetVEditWidget;
    protected final BaseTextFieldWidget textureNameTextField;
    @Nullable protected Identifier texture;
    protected int u;
    protected int v;
    protected int iconWidth;
    protected int iconHeight;
    protected int textureSheetWidth;
    protected int textureSheetHeight;
    protected int variantOffsetU;
    protected int variantOffsetV;

    public CustomIconEditScreen(Consumer<Icon> iconConsumer)
    {
        this(DefaultIcons.INFO_ICON_11, iconConsumer);
    }

    public CustomIconEditScreen(Icon icon, Consumer<Icon> iconConsumer)
    {
        this(icon, iconConsumer, 0, 0);
    }

    public CustomIconEditScreen(MultiIcon icon, Consumer<Icon> iconConsumer)
    {
        this(icon, iconConsumer,
             icon.getVariantU(1) - icon.getU(),
             icon.getVariantV(1) - icon.getV());
    }

    public CustomIconEditScreen(Icon icon, Consumer<Icon> iconConsumer, int variantOffsetU, int variantOffsetV)
    {
        this.setTitle("malilib.title.screen.custom_icons_edit_screen");

        this.originalIcon = icon;
        this.texture = icon.getTexture();
        this.u = icon.getU();
        this.v = icon.getV();
        this.iconWidth = icon.getWidth();
        this.iconHeight = icon.getHeight();
        this.textureSheetWidth = icon.getTextureSheetWidth();
        this.textureSheetHeight = icon.getTextureSheetHeight();
        this.variantOffsetU = variantOffsetU;
        this.variantOffsetV = variantOffsetV;
        this.iconConsumer = iconConsumer;

        this.textureNameLabel           = new LabelWidget("malilib.label.custom_icon_edit.texture_name");
        this.uLabel                     = new LabelWidget("malilib.label.custom_icon_edit.coordinate.u");
        this.vLabel                     = new LabelWidget("malilib.label.custom_icon_edit.coordinate.v");
        this.iconWidthLabel             = new LabelWidget("malilib.label.custom_icon_edit.icon_width");
        this.iconHeightLabel            = new LabelWidget("malilib.label.custom_icon_edit.icon_height");
        this.textureSheetWidthLabel     = new LabelWidget("malilib.label.custom_icon_edit.texture_sheet_width");
        this.textureSheetHeightLabel    = new LabelWidget("malilib.label.custom_icon_edit.texture_sheet_height");
        this.variantOffsetLabel         = new LabelWidget("malilib.label.custom_icon_edit.variant_offset_uv");
        this.previewLabel               = new LabelWidget("malilib.label.custom_icon_edit.icon_preview");

        this.textureNameTextField = new BaseTextFieldWidget(280, 16, this.texture.toString());
        this.textureNameTextField.setListener(this::setTextureName);

        int fieldWidth = 80;
        this.uEditWidget                    = new IntegerEditWidget(fieldWidth, 16, this.u, 0, 32767, (val) -> this.u = val);
        this.vEditWidget                    = new IntegerEditWidget(fieldWidth, 16, this.v, 0, 32767, (val) -> this.v = val);
        this.iconWidthEditWidget            = new IntegerEditWidget(fieldWidth, 16, this.iconWidth, 1, 8192, (val) -> this.iconWidth = val);
        this.iconHeightEditWidget           = new IntegerEditWidget(fieldWidth, 16, this.iconHeight, 1, 8192, (val) -> this.iconHeight = val);
        this.textureSheetWidthEditWidget    = new IntegerEditWidget(fieldWidth, 16, this.textureSheetWidth, 1, 32767, (val) -> this.textureSheetWidth = val);
        this.textureSheetHeightEditWidget   = new IntegerEditWidget(fieldWidth, 16, this.textureSheetHeight, 1, 32767, (val) -> this.textureSheetHeight = val);
        this.variantOffsetUEditWidget       = new IntegerEditWidget(fieldWidth, 16, this.variantOffsetU, -32768, 32767, (val) -> this.variantOffsetU = val);
        this.variantOffsetVEditWidget       = new IntegerEditWidget(fieldWidth, 16, this.variantOffsetV, -32768, 32767, (val) -> this.variantOffsetV = val);

        this.backgroundColor = 0xF0000000;
        this.renderBorder = true;
        this.screenCloseListener = this::createAndApplyIcon;

        this.setScreenWidthAndHeight(300, 272);
        this.centerOnScreen();
    }

    @Override
    protected void reAddActiveWidgets()
    {
        super.reAddActiveWidgets();

        this.addWidget(this.textureNameLabel);
        this.addWidget(this.textureNameTextField);

        this.addWidget(this.uLabel);
        this.addWidget(this.uEditWidget);

        this.addWidget(this.vLabel);
        this.addWidget(this.vEditWidget);

        this.addWidget(this.iconWidthLabel);
        this.addWidget(this.iconWidthEditWidget);

        this.addWidget(this.iconHeightLabel);
        this.addWidget(this.iconHeightEditWidget);

        this.addWidget(this.textureSheetWidthLabel);
        this.addWidget(this.textureSheetWidthEditWidget);

        this.addWidget(this.textureSheetHeightLabel);
        this.addWidget(this.textureSheetHeightEditWidget);

        this.addWidget(this.variantOffsetLabel);
        this.addWidget(this.variantOffsetUEditWidget);
        this.addWidget(this.variantOffsetVEditWidget);

        this.addWidget(this.previewLabel);
    }

    @Override
    protected void updateWidgetPositions()
    {
        super.updateWidgetPositions();

        int x = this.x + 10;
        int y = this.y + 24;
        int rightX = x + Math.max(this.iconWidthLabel.getWidth(), this.textureSheetWidthLabel.getWidth()) + 10;
        rightX = Math.max(rightX, x + 100);

        this.textureNameLabel.setPosition(x, y);

        y += 10;
        this.textureNameTextField.setPosition(x, y);

        y += 22;
        this.uLabel.setPosition(x, y);
        this.uEditWidget.setPosition(x, y + 10);

        this.vLabel.setPosition(rightX, y);
        this.vEditWidget.setPosition(rightX, y + 10);

        y += 32;
        this.iconWidthLabel.setPosition(x, y);
        this.iconWidthEditWidget.setPosition(x, y + 10);

        this.iconHeightLabel.setPosition(rightX, y);
        this.iconHeightEditWidget.setPosition(rightX, y + 10);

        y += 32;
        this.textureSheetWidthLabel.setPosition(x, y);
        this.textureSheetWidthEditWidget.setPosition(x, y + 10);

        this.textureSheetHeightLabel.setPosition(rightX, y);
        this.textureSheetHeightEditWidget.setPosition(rightX, y + 10);

        y += 32;
        this.variantOffsetLabel.setPosition(x, y);
        this.variantOffsetUEditWidget.setPosition(x, y + 10);
        this.variantOffsetVEditWidget.setPosition(rightX, y + 10);

        y += 32;
        this.previewLabel.setPosition(x, y);
    }

    protected void createAndApplyIcon()
    {
        Icon icon = this.createIcon();

        if (icon != null && icon.equals(this.originalIcon) == false)
        {
            this.iconConsumer.accept(icon);
        }
    }

    @Nullable
    protected Icon createIcon()
    {
        if (this.hasValidData())
        {
            if (this.isMultiIcon())
            {
                return new BaseMultiIcon(this.u, this.v, this.iconWidth, this.iconHeight,
                                         this.variantOffsetU, this.variantOffsetV,
                                         this.textureSheetWidth, this.textureSheetHeight,
                                         this.texture);
            }
            else
            {
                return new BaseIcon(this.u, this.v, this.iconWidth, this.iconHeight,
                                    this.textureSheetWidth, this.textureSheetHeight,
                                    this.texture);
            }
        }

        return null;
    }

    protected boolean hasValidData()
    {
        return this.texture != null &&
               this.iconWidth > 0 && this.iconHeight > 0 &&
               this.textureSheetWidth > 0 && this.textureSheetHeight > 0;
    }

    protected boolean isMultiIcon()
    {
        return this.variantOffsetU != 0 || this.variantOffsetV != 0;
    }

    protected void setTextureName(String str)
    {
        this.texture = StringUtils.identifier(str);
    }

    @Override
    protected void renderCustomContents(ScreenContext ctx)
    {
        if (this.hasValidData())
        {
            int x = this.x + 10;
            int y = this.previewLabel.getBottom() + 2;
            float z = this.z + 0.5f;

            float pw = 1.0F / (float) this.textureSheetWidth;
            float ph = 1.0F / (float) this.textureSheetHeight;
            int width = this.iconWidth;
            int height = this.iconHeight;

            if (width > 64 || height > 64)
            {
                double scale = 64.0 / (double) Math.max(width, height);
                width = (int) Math.floor(scale * width);
                height = (int) Math.floor(scale * height);
            }

            RenderUtils.color(1f, 1f, 1f, 1f);
            RenderUtils.setupBlend();

            //ShapeRenderUtils.renderOutline(x, y, z, width + 4, height + 4, 1, 0xFFFFFFFF);

            x += 2;
            y += 2;

            RenderUtils.bindTexture(this.texture);

            ShapeRenderUtils.renderScaledTexturedRectangle(x, y, z,
                                                           this.u, this.v,
                                                           width, height,
                                                           this.iconWidth, this.iconHeight,
                                                           pw, ph);
        }
    }
}
