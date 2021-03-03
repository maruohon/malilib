package fi.dy.masa.malilib.render.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.util.StringReader;
import fi.dy.masa.malilib.util.data.Color4f;

public class StyledTextParser
{
    public static final String VANILLA_FORMAT_CODES = "0123456789abcdefklmnorABCDEFKLMNOR";
    protected static final Pattern PATTERN_COLOR_3 = Pattern.compile("[0-9a-fA-F]{3}>");
    protected static final Pattern PATTERN_COLOR_4 = Pattern.compile("[0-9a-fA-F]{4}>");
    protected static final Pattern PATTERN_COLOR_6 = Pattern.compile("[0-9a-fA-F]{6}>");
    protected static final Pattern PATTERN_COLOR_8 = Pattern.compile("[0-9a-fA-F]{8}>");

    public static StyledText parseString(String str)
    {
        StringReader reader = new StringReader(str);
        List<Token> tokens = new ArrayList<>();

        readTokens(reader, tokens);

        return parseTokensToStyledText(tokens);
    }

    public static StyledText parseTokensToStyledText(List<Token> tokens)
    {
        StyledText.Builder builder = StyledText.builder();

        for (Token token : tokens)
        {
            token.applyTo(builder);
        }

        return builder.build();
    }

    public static void readTokens(StringReader reader, List<Token> tokens)
    {
        int stringStart = reader.getPos();
        int stringLength = 0;

        while (reader.canRead())
        {
            char previous = reader.peekPrevious();
            char current = reader.peek();
            char next = reader.peekNext();

            Token token = tryReadStyleTokenAt(reader);

            if (token == null)
            {
                if (current == '§' && previous != '\\' && VANILLA_FORMAT_CODES.indexOf(next) != -1)
                {
                    token = new VanillaStyleToken(next);
                }
                else if (current == '\\' && next == 'n' && previous != '\\')
                {
                    token = new LineBreakToken(2);
                }
                else if (current == '\n')
                {
                    token = new LineBreakToken(1);
                }
                else
                {
                    ++stringLength;
                }
            }

            if (token != null)
            {
                if (stringLength > 0)
                {
                    tokens.add(new StringToken(reader.subString(stringStart, stringStart + stringLength - 1)));
                    stringLength = 0;
                }

                tokens.add(token);
                reader.skip(token.getStringLength());
                stringStart = reader.getPos();
            }
            else
            {
                reader.skip(1);
            }
        }

        if (stringLength > 0)
        {
            tokens.add(new StringToken(reader.subString(stringStart, stringStart + stringLength - 1)));
        }
    }

    @Nullable
    public static Token tryReadStyleTokenAt(StringReader reader)
    {
        /*
        <b>This is Bold</b> this is normal <i>some Italic</i> and normal
        Normal text <u>some Underline</u> normal again <st>StrikeThrough</st>
        Normal <c=FFFF5090>colored</c> normal <c=FF30FF90>other color <b>Bold <i>Italic <rnd>Random <rst>normal
        Normal <sh>shadow</sh> <c=FFF000F0>colored</c> <csh=FF30F030>shadow color</csh> normal
        */
        char c = reader.peek();

        if (c != '<' || reader.peekPrevious() == '\\')
        {
            return null;
        }

        final int originalPos = reader.getPos();
        reader.skip(1);
        c = reader.peek();

        boolean state = true;

        if (c == '/' || c == '!' || c == '^')
        {
            state = false;
            reader.skip(1);
        }

        // <, >, and possibly the negation character
        int baseLength = state ? 2 : 3;
        Token token = null;

        if (reader.startsWith("b>"))
        {
            token = new StyleChangeToken(TextStyle.Builder::withBold, state, baseLength + 1);
        }
        else if (reader.startsWith("i>"))
        {
            token = new StyleChangeToken(TextStyle.Builder::withItalic, state, baseLength + 1);
        }
        else if (reader.startsWith("u>"))
        {
            token = new StyleChangeToken(TextStyle.Builder::withUnderline, state, baseLength + 1);
        }
        else if (reader.startsWith("st>"))
        {
            token = new StyleChangeToken(TextStyle.Builder::withStrikeThrough, state, baseLength + 2);
        }
        else if (reader.startsWith("sh>"))
        {
            token = new StyleChangeToken(TextStyle.Builder::withShadow, state, baseLength + 2);
        }
        else if (reader.startsWith("rnd>"))
        {
            token = new StyleChangeToken(TextStyle.Builder::withRandom, state, baseLength + 3);
        }
        else if (reader.startsWith("rst>") && state) // negated reset state is not valid
        {
            token = new StyleChangeToken((b, v) -> b.resetEverything(), false, baseLength + 3);
        }
        else if (state == false && (reader.startsWith("c>") || reader.startsWith("csh>")))
        {
            boolean shadow = reader.startsWith("csh>");
            int tokenValueLen = shadow ? 3 : 1;
            BiConsumer<TextStyle.Builder, Color4f> consumer = shadow ? TextStyle.Builder::withShadowColor : TextStyle.Builder::withColor;
            token = new ColorChangeToken(consumer, null, baseLength + tokenValueLen);
        }
        else if (state && (reader.startsWith("c=") || reader.startsWith("csh=")))
        {
            Color4f color = null;
            boolean shadow = reader.startsWith("csh=");
            int tokenValueLen = shadow ? 4 : 2;

            reader.skip(tokenValueLen);
            String str = reader.subString();

            if (PATTERN_COLOR_6.matcher(str.substring(0, 7)).matches())
            {
                int colorInt = (int) Long.parseLong(str.substring(0, 6), 16);
                color = Color4f.fromColor(colorInt, 1f);
                tokenValueLen += 6;
            }
            else if (PATTERN_COLOR_8.matcher(str.substring(0, 9)).matches())
            {
                int colorInt = (int) Long.parseLong(str.substring(0, 8), 16);
                color = Color4f.fromColor(colorInt);
                tokenValueLen += 8;
            }
            else if (PATTERN_COLOR_3.matcher(str.substring(0, 4)).matches())
            {
                int r = (int) Long.parseLong(str.substring(0, 1), 16) * 17;
                int g = (int) Long.parseLong(str.substring(1, 2), 16) * 17;
                int b = (int) Long.parseLong(str.substring(2, 3), 16) * 17;
                color = Color4f.fromColor(r << 16 | g << 8 | b, 1f);
                tokenValueLen += 3;
            }
            else if (PATTERN_COLOR_4.matcher(str.substring(0, 5)).matches())
            {
                int a = (int) Long.parseLong(str.substring(0, 1), 16) * 17;
                int r = (int) Long.parseLong(str.substring(1, 2), 16) * 17;
                int g = (int) Long.parseLong(str.substring(2, 3), 16) * 17;
                int b = (int) Long.parseLong(str.substring(3, 4), 16) * 17;
                color = Color4f.fromColor(a << 24 | r << 16 | g << 8 | b);
                tokenValueLen += 4;
            }

            if (color != null)
            {
                BiConsumer<TextStyle.Builder, Color4f> consumer = shadow ? TextStyle.Builder::withShadowColor : TextStyle.Builder::withColor;
                token = new ColorChangeToken(consumer, color, baseLength + tokenValueLen);
            }
        }

        reader.setPos(originalPos);

        return token;
    }

    public abstract static class Token
    {
        protected int stringLength;

        public int getStringLength()
        {
            return this.stringLength;
        }

        public abstract void applyTo(StyledText.Builder builder);
    }

    public static class StyleChangeToken extends Token
    {
        protected final BiConsumer<TextStyle.Builder, Boolean> consumer;
        protected final boolean state;

        public StyleChangeToken(BiConsumer<TextStyle.Builder, Boolean> consumer, boolean state, int tokenLength)
        {
            this.consumer = consumer;
            this.state = state;
            this.stringLength = tokenLength;
        }

        @Override
        public void applyTo(StyledText.Builder builder)
        {
            builder.applyStyleChange((b) -> this.consumer.accept(b, this.state));
        }
    }

    public static class ColorChangeToken extends Token
    {
        protected final BiConsumer<TextStyle.Builder, Color4f> consumer;
        protected final Color4f color;

        public ColorChangeToken(BiConsumer<TextStyle.Builder, Color4f> consumer, Color4f color, int tokenLength)
        {
            this.consumer = consumer;
            this.color = color;
            this.stringLength = tokenLength;
        }

        @Override
        public void applyTo(StyledText.Builder builder)
        {
            builder.applyStyleChange((b) -> this.consumer.accept(b, this.color));
        }
    }

    public static class VanillaStyleToken extends Token
    {
        protected final char code;

        public VanillaStyleToken(char code)
        {
            this.code = String.valueOf(code).toLowerCase(Locale.ROOT).charAt(0);
            this.stringLength = 2;
        }

        @Override
        public void applyTo(StyledText.Builder builder)
        {
            switch (this.code)
            {
                case 'k': builder.applyStyleChange((b) -> b.withRandom(true)); break;
                case 'l': builder.applyStyleChange((b) -> b.withBold(true)); break;
                case 'm': builder.applyStyleChange((b) -> b.withStrikeThrough(true)); break;
                case 'n': builder.applyStyleChange((b) -> b.withUnderline(true)); break;
                case 'o': builder.applyStyleChange((b) -> b.withItalic(true)); break;
                case 'r': builder.applyStyleChange(TextStyle.Builder::resetVanillaStyles); break;
                default:
                    builder.applyStyleChange(TextStyle.Builder::resetVanillaStyles);
                    builder.applyStyleChange((b) -> b.withColor(Color4f.fromColor(TextRenderer.INSTANCE.getColorCode(this.code), 1f)));
            }
        }
    }

    public static class LineBreakToken extends Token
    {
        public LineBreakToken(int tokenLength)
        {
            this.stringLength = tokenLength;
        }

        @Override
        public void applyTo(StyledText.Builder builder)
        {
            builder.addLineBeak();
        }
    }

    public static class StringToken extends Token
    {
        protected final String str;

        public StringToken(String str)
        {
            this.str = str;
            this.stringLength = str.length();
        }

        @Override
        public void applyTo(StyledText.Builder builder)
        {
            builder.appendString(this.str);
        }
    }
}
