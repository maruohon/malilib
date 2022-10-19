package malilib.render.text;

import java.util.Objects;
import javax.annotation.Nullable;
import malilib.render.Gradient;
import malilib.util.data.Color4f;

public class TextStyle
{
    public static final TextStyle DEFAULT = new TextStyle(null, false, false, false);

    public final boolean bold;
    public final boolean italic;
    public final boolean underline;
    public final boolean strikeThrough;
    public final boolean random;
    @Nullable public final Color4f color;
    @Nullable public final Color4f shadowColor;
    @Nullable public final Gradient gradient;
    /** If this shadow value is set, then it overrides the shadow value the renderer is set to use normally in the given context */
    @Nullable public final Boolean shadow;

    public TextStyle(@Nullable Color4f color, boolean bold, boolean italic, boolean underline)
    {
        this(color, bold, italic, underline, false, false, null, null);
    }

    public TextStyle(@Nullable Color4f color, boolean bold, boolean italic, boolean underline,
                     boolean strikeThrough, boolean random, @Nullable Boolean shadow, @Nullable Gradient gradient)
    {
        this(color, bold, italic, underline, strikeThrough, random, getDefaultShadowColor(color), shadow, gradient);
    }

    public TextStyle(@Nullable Color4f color, boolean bold, boolean italic, boolean underline,
                     boolean strikeThrough, boolean random, @Nullable Color4f shadowColor,
                     @Nullable Boolean shadow, @Nullable Gradient gradient)
    {
        this.color = color;
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.strikeThrough = strikeThrough;
        this.random = random;
        this.shadowColor = shadowColor;
        this.shadow = shadow;
        this.gradient = gradient;
    }

    /**
     * Merges the other style to this style.
     * For the optional/nullable values this means that if other has the value defined,
     * then that value will override the value (if any) from this instance.
     * For the "normal" booleans such as bold and italic, if other has the property enabled,
     * then it will be enabled, otherwise the value from this instance will be used.
     */
    public TextStyle merge(TextStyle other)
    {
        TextStyle.Builder builder = builder().fromStyle(this);

        if (other.bold)                 { builder.withBold(true); }
        if (other.italic)               { builder.withItalic(true); }
        if (other.underline)            { builder.withUnderline(true); }
        if (other.strikeThrough)        { builder.withStrikeThrough(true); }
        if (other.random)               { builder.withRandom(true); }
        if (other.color != null)        { builder.withColor(other.color); }
        if (other.gradient != null)     { builder.withGradient(other.gradient); }
        if (other.shadow != null)       { builder.withShadow(other.shadow); }

        if (other.shadowColor != null)
        {
            builder.withShadowColor(other.shadowColor);
        }
        // Don't keep the old shadow color if the main color got changed
        else if (this.shadowColor != null && other.color != null)
        {
            builder.withShadowColor(null);
        }

        return builder.build();
    }

    /**
     * Use the provided color as the color and/or the shadow color,
     * if they are not set already.
     */
    public TextStyle withFallbackColor(Color4f color)
    {
        if (this.color == null || this.shadowColor == null)
        {
            Builder builder = builder();

            if (this.color == null)
            {
                builder.withColor(color);
            }

            if (this.shadowColor == null)
            {
                builder.withShadowColor(getDefaultShadowColor(this.color != null ? this.color : color));
            }

            return builder.build();
        }

        return this;
    }

    @Override
    public String toString()
    {
        return String.format("TextStyle{bold=%s, italic=%s, underline=%s, strikeThrough=%s, random=%s, shadow=%s, color=%s, shadowColor=%s, gradient=%s}",
                             this.bold, this.italic, this.underline, this.strikeThrough, this.random,
                             this.shadow, this.color, this.shadowColor, this.gradient);
    }

    public static TextStyle normal(int color)
    {
        return new TextStyle(Color4f.fromColor(color), false, false, false);
    }

    public static TextStyle bold(int color)
    {
        return new TextStyle(Color4f.fromColor(color), true, false, false);
    }

    public static TextStyle italic(int color)
    {
        return new TextStyle(Color4f.fromColor(color), false, true, false);
    }

    public static TextStyle underlined(int color)
    {
        return new TextStyle(Color4f.fromColor(color), false, false, true);
    }

    @Nullable
    public static Color4f getDefaultShadowColor(@Nullable Color4f textColor)
    {
        return textColor != null ? Color4f.fromColor(0xFF000000 | ((textColor.intValue & 0xFCFCFC) >> 2)) : null;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass()) { return false; }

        TextStyle textStyle = (TextStyle) o;

        if (this.bold != textStyle.bold) { return false; }
        if (this.italic != textStyle.italic) { return false; }
        if (this.underline != textStyle.underline) { return false; }
        if (this.strikeThrough != textStyle.strikeThrough) { return false; }
        if (this.random != textStyle.random) { return false; }
        if (!Objects.equals(this.color, textStyle.color)) { return false; }
        if (!Objects.equals(this.shadowColor, textStyle.shadowColor)) { return false; }
        if (!Objects.equals(this.gradient, textStyle.gradient)) { return false; }
        return Objects.equals(this.shadow, textStyle.shadow);
    }

    @Override
    public int hashCode()
    {
        int result = (this.bold ? 1 : 0);
        result = 31 * result + (this.italic ? 1 : 0);
        result = 31 * result + (this.underline ? 1 : 0);
        result = 31 * result + (this.strikeThrough ? 1 : 0);
        result = 31 * result + (this.random ? 1 : 0);
        result = 31 * result + (this.color != null ? this.color.hashCode() : 0);
        result = 31 * result + (this.shadowColor != null ? this.shadowColor.hashCode() : 0);
        result = 31 * result + (this.gradient != null ? this.gradient.hashCode() : 0);
        result = 31 * result + (this.shadow != null ? this.shadow.hashCode() : 0);
        return result;
    }

    public static class Builder
    {
        @Nullable private Color4f color;
        @Nullable private Color4f shadowColor;
        @Nullable private Gradient gradient;
        @Nullable private Boolean shadow;
        private boolean bold;
        private boolean italic;
        private boolean underline;
        private boolean strikeThrough;
        private boolean random;

        public Builder()
        {
        }

        public Builder(TextStyle old)
        {
            this.fromStyle(old);
        }

        public Builder resetAll()
        {
            this.shadowColor = null;
            this.gradient = null;
            this.shadow = null;
            this.resetVanillaStyles();
            return this;
        }

        /**
         * Resets all the styles that the vanilla text renderer supports and has formatting codes for:
         * color, bold, italic, underline, strike-through, random/obfuscated
         */
        public Builder resetVanillaStyles()
        {
            this.color = null;
            this.bold = false;
            this.italic = false;
            this.underline = false;
            this.strikeThrough = false;
            this.random = false;
            return this;
        }

        public Builder fromStyle(TextStyle old)
        {
            this.color = old.color;
            this.shadowColor = old.shadowColor;
            this.bold = old.bold;
            this.italic = old.italic;
            this.underline = old.underline;
            this.strikeThrough = old.strikeThrough;
            this.random = old.random;
            this.gradient = old.gradient;
            this.shadow = old.shadow;
            return this;
        }

        public Builder withColor(@Nullable Color4f color)
        {
            this.color = color;
            return this;
        }

        public Builder withColor(int color)
        {
            this.color = Color4f.fromColor(color);
            return this;
        }

        public Builder withShadowColor(@Nullable Color4f shadowColor)
        {
            this.shadowColor = shadowColor;
            return this;
        }

        public Builder withGradient(@Nullable Gradient gradient)
        {
            this.gradient = gradient;
            return this;
        }

        public Builder withShadow(@Nullable Boolean shadow)
        {
            this.shadow = shadow;
            return this;
        }

        public Builder withBold(boolean bold)
        {
            this.bold = bold;
            return this;
        }

        public Builder withItalic(boolean italic)
        {
            this.italic = italic;
            return this;
        }

        public Builder withUnderline(boolean underline)
        {
            this.underline = underline;
            return this;
        }

        public Builder withStrikeThrough(boolean strikeThrough)
        {
            this.strikeThrough = strikeThrough;
            return this;
        }

        public Builder withRandom(boolean random)
        {
            this.random = random;
            return this;
        }

        /**
         * Creates a TextStyle instance of the current style settings in the Builder.
         * This does not have side effects, and the Builder can be further used/modified and built again.
         * @return
         */
        public TextStyle build()
        {
            return new TextStyle(this.color, this.bold, this.italic, this.underline, this.strikeThrough, this.random,
                                 this.shadowColor, this.shadow, this.gradient);
        }

        public boolean equalsStyle(TextStyle textStyle)
        {
            if (this.bold != textStyle.bold) { return false; }
            if (this.italic != textStyle.italic) { return false; }
            if (this.underline != textStyle.underline) { return false; }
            if (this.strikeThrough != textStyle.strikeThrough) { return false; }
            if (this.random != textStyle.random) { return false; }
            if (!Objects.equals(this.color, textStyle.color)) { return false; }
            if (!Objects.equals(this.shadowColor, textStyle.shadowColor)) { return false; }
            if (!Objects.equals(this.gradient, textStyle.gradient)) { return false; }
            return Objects.equals(this.shadow, textStyle.shadow);
        }
    }
}
