package suchmaschine.http;

import suchmaschine.http.exceptions.InvalidHttpMethodException;
import suchmaschine.http.exceptions.InvalidRequestException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private final HttpMethod method;
    private final String path;
    private final Map<String, String> parameters;

    public HttpRequest(String header) {
        if (header == null)
            throw new InvalidRequestException("null");

        String[] headerParts = header.split(" ");
        if (headerParts.length != 3)
            throw new InvalidRequestException("Request has illegal format");

        try {
            method = HttpMethod.valueOf(headerParts[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpMethodException("Invalid method '" + headerParts[0] + "'");
        }

        String urlPath = headerParts[1];
        String[] pathSplit = urlPath.split("\\?");
        if (pathSplit.length > 2) // '?' occurs more than one time
            throw new InvalidRequestException("path has invalid format!");

        path = pathSplit[0].toLowerCase(); // even when there is no '?' character, the first element exists

        if (pathSplit.length == 2) {
            String[] keyValuePairs = pathSplit[1].split("&");

            this.parameters = Arrays.stream(keyValuePairs).map(pair -> {
                if (!pair.contains("="))
                    throw new InvalidRequestException("Query string is malformed! Missing equal");

                String[] keyValue =  pair.split("=");
                if (keyValue.length > 2)
                    throw new InvalidRequestException("Query parameter is malformed! (" + pair + ")");

                return keyValue;
            }).collect(Collectors.toMap(keyValue -> keyValue[0].toLowerCase(), keyValue -> keyValue.length == 2? keyValue[1]: "", (value1, value2) -> value1));
        }
        else // ensure parameters is set
            this.parameters = new HashMap<>();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParameter(String param) {
        return parameters.get(param);
    }

    @Override
    public String toString() {
        return method + " " + path + "?" + parameters + " HTTP/1.1";
    }

}