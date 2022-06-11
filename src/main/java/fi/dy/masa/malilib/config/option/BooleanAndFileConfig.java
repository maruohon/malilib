package fi.dy.masa.malilib.config.option;

import java.nio.file.Path;
import java.util.Objects;
import java.util.StringJoiner;
import fi.dy.masa.malilib.config.option.BooleanAndFileConfig.BooleanAndFile;

public class BooleanAndFileConfig extends BaseGenericConfig<BooleanAndFile> implements BooleanContainingConfig<BooleanAndFile>
{
    public BooleanAndFileConfig(String name, boolean defaultBooleanValue, Path defaultDirectory)
    {
        super(name, new BooleanAndFile(defaultBooleanValue, defaultDirectory));
    }

    public BooleanAndFileConfig(String name, boolean defaultBooleanValue, Path defaultDirectory,
                                String commentTranslationKey, Object... commentArgs)
    {
        super(name, new BooleanAndFile(defaultBooleanValue, defaultDirectory), commentTranslationKey, commentArgs);
    }

    @Override
    public boolean getBooleanValue()
    {
        return this.getValue().booleanValue;
    }

    @Override
    public boolean getDefaultBooleanValue()
    {
        return this.defaultValue.booleanValue;
    }

    @Override
    public boolean setBooleanValue(boolean newValue)
    {
        BooleanAndFile oldValue = this.getValue();
        return this.setValue(new BooleanAndFile(newValue, oldValue.fileValue));
    }

    @Override
    public void toggleBooleanValue()
    {
        BooleanAndFile oldValue = this.getValue();
        this.setValue(new BooleanAndFile(! oldValue.booleanValue, oldValue.fileValue));
    }

    public static class BooleanAndFile
    {
        public final boolean booleanValue;
        public final Path fileValue;

        public BooleanAndFile(boolean booleanValue, Path fileValue)
        {
            this.booleanValue = booleanValue;
            this.fileValue = fileValue;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) { return true; }
            if (o == null || this.getClass() != o.getClass()) { return false; }

            BooleanAndFile that = (BooleanAndFile) o;

            return this.booleanValue == that.booleanValue &&
                   Objects.equals(this.fileValue, that.fileValue);
        }

        @Override
        public int hashCode()
        {
            int result = (this.booleanValue ? 1 : 0);
            result = 31 * result + (this.fileValue != null ? this.fileValue.hashCode() : 0);
            return result;
        }

        @Override
        public String toString()
        {
            return new StringJoiner(", ", BooleanAndFile.class.getSimpleName() + "[", "]")
                    .add("booleanValue=" + this.booleanValue)
                    .add("fileValue=" + this.fileValue)
                    .toString();
        }
    }
}
