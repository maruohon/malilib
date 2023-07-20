package malilib.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;

import malilib.MaLiLib;

public class HttpUtils
{
    protected static HttpURLConnection createUrlConnection(URL url, int timeout) throws IOException
    {
        MaLiLib.debugLog("Opening connection to {}", url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);

        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        connection.setUseCaches(false);

        return connection;
    }

    @Nullable
    protected static String performGetRequest(URL url, int timeout) throws IOException
    {
        MaLiLib.debugLog("Reading data from URL '{}'", url);
        HttpURLConnection connection = createUrlConnection(url, timeout);
        InputStream inputStream = null;

        try
        {
            inputStream = connection.getInputStream();
            String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            MaLiLib.debugLog("Successful read, server response was: " + connection.getResponseCode());
            MaLiLib.debugLog("Result: " + result);
            return result;
        }
        catch (IOException e)
        {
            IOUtils.closeQuietly(inputStream);
            inputStream = connection.getErrorStream();

            if (inputStream != null)
            {
                MaLiLib.debugLog("Reading error page from '{}'", url);
                final String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                MaLiLib.debugLog("Successful read, server response was: " + connection.getResponseCode());
                MaLiLib.debugLog("Result: " + result);
                return result;
            }
            else
            {
                MaLiLib.debugLog("GET request failed from '{}'", url, e);
            }
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }

        return null;
    }

    @Nullable
    public static String tryFetchPage(String pageURL, int timeout)
    {
        try
        {
            return performGetRequest(new URL(pageURL), timeout);
        }
        catch (Exception e)
        {
            MaLiLib.debugLog("Page fetch from '{}' failed", pageURL, e);
        }

        return null;
    }
}
