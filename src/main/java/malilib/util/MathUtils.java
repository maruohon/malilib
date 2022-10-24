package malilib.util;

public class MathUtils
{
    /**
     * @return The average value of the elements in the given array
     */
    public static double average(int[] arr)
    {
        final int size = arr.length;

        if (size == 0)
        {
            return 0;
        }

        long sum = 0;

        for (int i = 0; i < size; ++i)
        {
            sum += arr[i];
        }

        return (double) sum / (double) size;
    }

    /**
     * @return The average value of the elements in the given array
     */
    public static double average(long[] arr)
    {
        final int size = arr.length;

        if (size == 0)
        {
            return 0;
        }

        long sum = 0;

        for (int i = 0; i < size; ++i)
        {
            sum += arr[i];
        }

        return (double) sum / (double) size;
    }

    /**
     * @return The average value of the elements in the given array
     */
    public static double average(double[] arr)
    {
        final int size = arr.length;

        if (size == 0)
        {
            return 0;
        }

        double sum = 0;

        for (int i = 0; i < size; ++i)
        {
            sum += arr[i];
        }

        return sum / (double) size;
    }

    public static int roundDown(int value, int interval)
    {
        if (interval == 0 || value == 0)
        {
            return 0;
        }
        else
        {
            if (value < 0)
            {
                interval *= -1;
            }

            int remainder = value % interval;

            return remainder == 0 ? value : value - remainder;
        }
    }

    public static double roundDown(double value, double interval)
    {
        if (interval == 0.0 || value == 0.0)
        {
            return 0.0;
        }
        else
        {
            if (value < 0.0)
            {
                interval *= -1.0;
            }

            double remainder = value % interval;

            return remainder == 0.0 ? value : value - remainder;
        }
    }

    public static int roundUp(int value, int interval)
    {
        if (interval == 0)
        {
            return 0;
        }
        else if (value == 0)
        {
            return interval;
        }
        else
        {
            if (value < 0)
            {
                interval *= -1;
            }

            int remainder = value % interval;

            return remainder == 0 ? value : value + interval - remainder;
        }
    }

    public static double roundUp(double value, double interval)
    {
        if (interval == 0.0)
        {
            return 0.0;
        }
        else if (value == 0.0)
        {
            return interval;
        }
        else
        {
            if (value < 0.0)
            {
                interval *= -1.0;
            }

            double remainder = value % interval;

            return remainder == 0.0 ? value : value + interval - remainder;
        }
    }

    public static long roundUp(long number, long interval)
    {
        if (interval == 0)
        {
            return 0;
        }
        else if (number == 0)
        {
            return interval;
        }
        else
        {
            if (number < 0)
            {
                interval *= -1;
            }

            long i = number % interval;
            return i == 0 ? number : number + interval - i;
        }
    }

    /**
     * Wraps/normalizes the given angle to the range 0 ... 2 * Pi
     */
    public static double wrapRadianAngle(double angle)
    {
        double twoPi = 2 * Math.PI;
        angle %= twoPi;

        if (angle < 0)
        {
            angle += twoPi;
        }

        return angle;
    }

    public static double distanceFromPointToLine(double pointX, double pointY,
                                                 double line1X, double line1Y,
                                                 double line2X, double line2Y)
    {
        // https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
        double num = Math.abs((line2X - line1X) * (line1Y - pointY) - (line1X - pointX) * (line2Y - line1Y));
        double diffX = line2X - line1X;
        double diffY = line2Y - line1Y;
        double den = Math.sqrt(diffX * diffX + diffY * diffY);

        return num / den;
    }

    /**
     * @return The minimum value from the given array
     */
    public static int getMinValue(int[] arr)
    {
        if (arr.length == 0)
        {
            throw new IllegalArgumentException("Empty array");
        }

        final int size = arr.length;
        int minValue = arr[0];

        for (int i = 1; i < size; ++i)
        {
            if (arr[i] < minValue)
            {
                minValue = arr[i];
            }
        }

        return minValue;
    }

    /**
     * @return The maximum value from the given array
     */
    public static int getMaxValue(int[] arr)
    {
        if (arr.length == 0)
        {
            throw new IllegalArgumentException("Empty array");
        }

        final int size = arr.length;
        int maxValue = arr[0];

        for (int i = 1; i < size; ++i)
        {
            if (arr[i] > maxValue)
            {
                maxValue = arr[i];
            }
        }

        return maxValue;
    }

    /**
     * @return The minimum value from the given array
     */
    public static long getMinValue(long[] arr)
    {
        if (arr.length == 0)
        {
            throw new IllegalArgumentException("Empty array");
        }

        final int size = arr.length;
        long minValue = arr[0];

        for (int i = 1; i < size; ++i)
        {
            if (arr[i] < minValue)
            {
                minValue = arr[i];
            }
        }

        return minValue;
    }

    /**
     * @return The maximum value from the given array
     */
    public static long getMaxValue(long[] arr)
    {
        if (arr.length == 0)
        {
            throw new IllegalArgumentException("Empty array");
        }

        final int size = arr.length;
        long maxValue = arr[0];

        for (int i = 1; i < size; ++i)
        {
            if (arr[i] > maxValue)
            {
                maxValue = arr[i];
            }
        }

        return maxValue;
    }
}
