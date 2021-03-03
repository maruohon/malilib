package fi.dy.masa.malilib.render.text;

import java.util.Objects;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.render.Gradient;
import fi.dy.masa.malilib.util.data.Color4f;

public class TextStyle
{
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

    public static TextStyle normal(Color4f color)
    {
        return new TextStyle(color, false, false, false);
    }

    public static TextStyle bold(Color4f color)
    {
        return new TextStyle(color, true, false, false);
    }

    public static TextStyle italic(Color4f color)
    {
        return new TextStyle(color, false, true, false);
    }

    public static TextStyle underlined(Color4f color)
    {
        return new TextStyle(color, false, false, true);
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
            this.color = old.color;
            this.shadowColor = old.shadowColor;
            this.bold = old.bold;
            this.italic = old.italic;
            this.underline = old.underline;
            this.strikeThrough = old.strikeThrough;
            this.random = old.random;
            this.gradient = old.gradient;
            this.shadow = old.shadow;
        }

        public Builder resetEverything()
        {
            this.shadowColor = null;
            this.gradient = null;
            this.shadow = null;
            this.resetVanillaStyles();
            return this;
        }

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

        public Builder withColor(@Nullable Color4f color)
        {
            this.color = color;
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
