package app.services;

import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class TryCatchService {

    public static int tryParseInt(String value, Context ctx, String message) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            ctx.status(500).json(message);
            throw new RuntimeException();
        }
    }

    public static String tryString(String value, Context ctx, String message) {
        if (value == null || value.isEmpty()) {
            ctx.status(500).json(message);
            throw new RuntimeException();
        }
        return value;
    }

    public static <T> T tryEntity(T entity, Context ctx, String message) {
        if (entity == null) {
            ctx.status(500).json(message);
            throw new RuntimeException();
        }
        return entity;
    }

    public static boolean tryParseBoolean(String value, Context ctx, String message) {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            ctx.status(500).json(message);
            throw new RuntimeException();
        }
    }

    public static <T extends Enum<T>> T tryParseEnum(Class<T> enumClass, String value, Context ctx, String message) {
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (Exception e) {
            ctx.status(500).json(message);
            throw new RuntimeException();
        }
    }

    public static <T> List<T> tryList(List<T> list, Context ctx, String message) {
        if (list == null || list.isEmpty()) {
            ctx.status(500).json(message);
            throw new RuntimeException();
        }
        return list;
    }

    public static double tryParseDouble(String value, Context ctx, String message) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            ctx.status(500).json(message);
            throw new RuntimeException();
        }
    }

    public static String tryPathParam(Context ctx, String param, String message) {
        String value = ctx.pathParam(param);

        if (value == null || value.isEmpty()) {
            ctx.status(500).json(message);
            throw new RuntimeException();
        }

        return value;
    }

    public static <T> T tryBody(Context ctx, Class<T> clazz, String message) {
        try {
            return ctx.bodyAsClass(clazz);
        } catch (Exception e) {
            ctx.status(500).json(message);
            throw new RuntimeException();
        }
    }

    public static Map<String, String> tryBodyMap(Context ctx, String message) {
        try {
            return ctx.bodyAsClass(Map.class);
        } catch (Exception e) {
            ctx.status(500).json(message);
            throw new RuntimeException();
        }
    }
}