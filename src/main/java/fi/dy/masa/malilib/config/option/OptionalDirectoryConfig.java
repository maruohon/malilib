package fi.dy.masa.malilib.config.option;

import java.io.File;
import java.util.Objects;
import fi.dy.masa.malilib.config.option.OptionalDirectoryConfig.BooleanAndFile;

public class OptionalDirectoryConfig extends BaseGenericConfig<BooleanAndFile>
{
    public OptionalDirectoryConfig(String name, boolean defaultBooleanValue, File defaultDirectory)
    {
        super(name, new BooleanAndFile(defaultBooleanValue, defaultDirectory));
    }

    public static class BooleanAndFile
    {
        public final boolean booleanValue;
        public final File fileValue;

        public BooleanAndFile(boolean booleanValue, File fileValue)
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
    }
}
