package com.callcenter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpClient {

    public String get(final String url) throws Exception {
        return doRequest(url, "GET", null, null);
    }

    public String get(final String url, final Map<String, String> header) throws Exception {
        return doRequest(url, "GET", header, null);
    }

    private String doRequest(final String url, final String type, final Map<String, String> header, final String body) throws Exception {
        String response = null;
        HttpURLConnection connection = null;
        try {
            final URL reqUrl = new URL(url);
            connection = ((HttpURLConnection) reqUrl.openConnection());
            connection.setRequestMethod(type);
            if (header != null) {
                for (final String key : header.keySet()) {
                    connection.addRequestProperty(key, header.get(key));
                }
            }
            if (body != null) {
                applyBody(connection, body);
            }

            final InputStream inputStream;

            final boolean isSuccess = connection.getResponseCode() >= 200 && connection.getResponseCode() < 300;

            if (isSuccess) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            response = stringBuilder.toString();
            inputStream.close();

        } catch (Exception e) {

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }

    private void applyBody(final HttpURLConnection httpURLConnection, final String body) throws Exception {
        final byte[] outputInBytes = body.getBytes("UTF-8");
        final OutputStream os = httpURLConnection.getOutputStream();
        os.write(outputInBytes);
        os.close();
    }

}