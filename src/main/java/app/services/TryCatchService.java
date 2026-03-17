package app.services;

import app.exceptions.ApiException;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class TryCatchService {

    public static int tryParseInt(String value, String message) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            throw new ApiException(400, message);
        }
    }

    public static String tryString(String value, String message) {
        if (value == null || value.isEmpty()) {
            throw new ApiException(400, message);
        }
        return value;
    }

    public static <T> T tryEntity(T entity, String message) {
        if (entity == null) {
            throw new ApiException(404, message);
        }
        return entity;
    }

    public static boolean tryParseBoolean(String value, String message) {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            throw new ApiException(400, message);
        }
    }

    public static <T extends Enum<T>> T tryParseEnum(Class<T> enumClass, String value, String message) {
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (Exception e) {
            throw new ApiException(400, message);
        }
    }

    public static <T> List<T> tryList(List<T> list, String message) {
        if (list == null || list.isEmpty()) {
            throw new ApiException(400, message);
        }
        return list;
    }

    public static double tryParseDouble(String value, String message) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            throw new ApiException(400, message);
        }
    }

    public static <T> T tryBody(Context ctx, Class<T> clazz, String message) {
        try {
            return ctx.bodyAsClass(clazz);
        } catch (Exception e) {
            throw new ApiException(400, message);
        }
    }

    public static Map<String, String> tryBodyMap(Context ctx, String message) {
        try {
            return ctx.bodyAsClass(Map.class);
        } catch (Exception e) {
            throw new ApiException(400, message);
        }
    }
}