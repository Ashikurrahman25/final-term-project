package util;

import java.io.*;
import java.util.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonUtil {
    
    private static final String DATA_DIR = "data/";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    // Save list to JSON format
    public static <T> void saveToFile(List<T> list, String fileName) {
        try {
            // Ensure .json extension
            if (!fileName.endsWith(".json")) {
                fileName = fileName.replace(".dat", ".json");
            }
            
            File file = new File(DATA_DIR + fileName);
            file.getParentFile().mkdirs();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("[");
                for (int i = 0; i < list.size(); i++) {
                    writer.print(objectToJson(list.get(i), 1));
                    if (i < list.size() - 1) {
                        writer.println(",");
                    } else {
                        writer.println();
                    }
                }
                writer.println("]");
            }
        } catch (IOException e) {
            System.err.println("Error saving to file " + fileName + ": " + e.getMessage());
        }
    }
    
    // Load list from JSON format (fallback to binary for existing files)
    @SuppressWarnings("unchecked")
    public static <T> List<T> loadFromFile(String fileName, Class<T> clazz) {
        try {
            // Ensure .json extension
            if (!fileName.endsWith(".json")) {
                fileName = fileName.replace(".dat", ".json");
            }
            
            File file = new File(DATA_DIR + fileName);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            
            // Try to read as text first to check if it's JSON
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            
            String jsonContent = content.toString().trim();
            
            // If it starts with '[' it's likely JSON, otherwise try binary fallback
            if (jsonContent.startsWith("[")) {
                return parseJsonArray(jsonContent, clazz);
            } else {
                // Fallback to binary reading for existing files
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    return (List<T>) ois.readObject();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading from file " + fileName + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Save single object to JSON format
    public static <T> void saveObject(T object, String fileName) {
        try {
            // Ensure .json extension
            if (!fileName.endsWith(".json")) {
                fileName = fileName.replace(".dat", ".json");
            }
            
            File file = new File(DATA_DIR + fileName);
            file.getParentFile().mkdirs();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(objectToJson(object, 0));
            }
        } catch (IOException e) {
            System.err.println("Error saving object to file " + fileName + ": " + e.getMessage());
        }
    }
    
    // Load single object from JSON format (fallback to binary for existing files)
    @SuppressWarnings("unchecked")
    public static <T> T loadObject(String fileName, Class<T> clazz) {
        try {
            // Ensure .json extension
            if (!fileName.endsWith(".json")) {
                fileName = fileName.replace(".dat", ".json");
            }
            
            File file = new File(DATA_DIR + fileName);
            if (!file.exists()) {
                return null;
            }
            
            // Try to read as text first
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            
            String jsonContent = content.toString().trim();
            
            // If it starts with '{' it's likely JSON, otherwise try binary fallback
            if (jsonContent.startsWith("{")) {
                return parseJsonObject(jsonContent, clazz);
            } else {
                // Fallback to binary reading for existing files
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    return (T) ois.readObject();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading object from file " + fileName + ": " + e.getMessage());
            return null;
        }
    }
    
    // Convert object to JSON string
    private static String objectToJson(Object obj, int indent) {
        if (obj == null) return "null";
        
        StringBuilder json = new StringBuilder();
        String indentStr = "  ".repeat(indent);
        
        json.append("{\n");
        
        // Get all fields including inherited fields
        List<Field> allFields = getAllFields(obj.getClass());
        boolean first = true;
        
        for (Field field : allFields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (!first) json.append(",\n");
                first = false;
                
                json.append(indentStr).append("  \"").append(field.getName()).append("\": ");
                json.append(valueToJson(value, indent + 1));
            } catch (IllegalAccessException e) {
                // Skip this field
            }
        }
        
        json.append("\n").append(indentStr).append("}");
        return json.toString();
    }
    
    // Get all fields including inherited fields
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                // Skip serialVersionUID field
                if (!field.getName().equals("serialVersionUID")) {
                    fields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
    
    // Convert value to JSON format
    private static String valueToJson(Object value, int indent) {
        if (value == null) return "null";
        
        if (value instanceof String) {
            String str = value.toString();
            str = str.replace("\\", "\\\\");  // Escape backslashes first
            str = str.replace("\"", "\\\"");  // Then escape quotes
            return "\"" + str + "\"";
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof LocalDateTime) {
            return "\"" + ((LocalDateTime) value).format(DATE_FORMATTER) + "\"";
        } else if (value instanceof List) {
            StringBuilder json = new StringBuilder();
            List<?> list = (List<?>) value;
            json.append("[\n");
            for (int i = 0; i < list.size(); i++) {
                json.append("  ".repeat(indent + 1));
                json.append(valueToJson(list.get(i), indent + 1));
                if (i < list.size() - 1) json.append(",");
                json.append("\n");
            }
            json.append("  ".repeat(indent)).append("]");
            return json.toString();
        } else {
            return objectToJson(value, indent);
        }
    }
    
    // Simple JSON array parser (basic implementation)
    private static <T> List<T> parseJsonArray(String json, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        try {
            // Remove [ and ] from array
            json = json.trim();
            if (json.startsWith("[")) json = json.substring(1);
            if (json.endsWith("]")) json = json.substring(0, json.length() - 1);
            
            // Split objects by },{ pattern
            String[] objects = splitJsonObjects(json);
            
            for (String objStr : objects) {
                objStr = objStr.trim();
                if (!objStr.isEmpty()) {
                    T obj = parseJsonObject(objStr, clazz);
                    if (obj != null) {
                        result.add(obj);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON array: " + e.getMessage());
        }
        return result;
    }
    
    // Split JSON objects in array
    private static String[] splitJsonObjects(String json) {
        List<String> objects = new ArrayList<>();
        int braceCount = 0;
        int start = 0;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    // Found complete object
                    objects.add(json.substring(start, i + 1));
                    // Skip comma and whitespace
                    while (i + 1 < json.length() && (json.charAt(i + 1) == ',' || Character.isWhitespace(json.charAt(i + 1)))) {
                        i++;
                    }
                    start = i + 1;
                }
            }
        }
        
        return objects.toArray(new String[0]);
    }
    
    // Simple JSON object parser (basic implementation)
    @SuppressWarnings("unchecked")
    private static <T> T parseJsonObject(String json, Class<T> clazz) {
        try {
            // Remove { and } from object
            json = json.trim();
            if (json.startsWith("{")) json = json.substring(1);
            if (json.endsWith("}")) json = json.substring(0, json.length() - 1);
            
            T obj = clazz.getDeclaredConstructor().newInstance();
            
            // Parse key-value pairs
            String[] pairs = splitJsonPairs(json);
            
            for (String pair : pairs) {
                pair = pair.trim();
                if (pair.isEmpty()) continue;
                
                int colonIndex = pair.indexOf(':');
                if (colonIndex == -1) continue;
                
                String key = pair.substring(0, colonIndex).trim();
                String value = pair.substring(colonIndex + 1).trim();
                
                // Remove quotes from key
                if (key.startsWith("\"") && key.endsWith("\"")) {
                    key = key.substring(1, key.length() - 1);
                }
                
                // Set field value
                setFieldValue(obj, key, value);
            }
            
            return obj;
        } catch (Exception e) {
            System.err.println("Error parsing JSON object for " + clazz.getSimpleName() + ": " + e.getMessage());
            return null;
        }
    }
    
    // Split JSON key-value pairs
    private static String[] splitJsonPairs(String json) {
        List<String> pairs = new ArrayList<>();
        int start = 0;
        boolean inQuotes = false;
        int braceCount = 0;
        int bracketCount = 0;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
            } else if (!inQuotes) {
                if (c == '{') {
                    braceCount++;
                } else if (c == '}') {
                    braceCount--;
                } else if (c == '[') {
                    bracketCount++;
                } else if (c == ']') {
                    bracketCount--;
                } else if (c == ',' && braceCount == 0 && bracketCount == 0) {
                    pairs.add(json.substring(start, i));
                    start = i + 1;
                }
            }
        }
        
        // Add last pair
        if (start < json.length()) {
            pairs.add(json.substring(start));
        }
        
        return pairs.toArray(new String[0]);
    }
    
    // Set field value using reflection
    private static void setFieldValue(Object obj, String fieldName, String value) {
        try {
            Field field = getFieldByName(obj.getClass(), fieldName);
            if (field == null) return;
            
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            
            // Remove quotes from string values
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
                // Unescape characters
                value = value.replace("\\\"", "\"").replace("\\\\", "\\");
            }
            
            if (fieldType == String.class) {
                field.set(obj, value);
            } else if (fieldType == int.class || fieldType == Integer.class) {
                field.set(obj, Integer.parseInt(value));
            } else if (fieldType == long.class || fieldType == Long.class) {
                field.set(obj, Long.parseLong(value));
            } else if (fieldType == double.class || fieldType == Double.class) {
                field.set(obj, Double.parseDouble(value));
            } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                field.set(obj, Boolean.parseBoolean(value));
            } else if (fieldType == LocalDateTime.class) {
                field.set(obj, LocalDateTime.parse(value, DATE_FORMATTER));
            } else if (fieldType == List.class) {
                // Handle List types - specifically for different field types
                if ("tickets".equals(fieldName)) {
                    // Don't remove quotes for array parsing
                    String arrayValue = value;
                    if (arrayValue.startsWith("\"") && arrayValue.endsWith("\"")) {
                        arrayValue = arrayValue.substring(1, arrayValue.length() - 1);
                        // Unescape characters
                        arrayValue = arrayValue.replace("\\\"", "\"").replace("\\\\", "\\");
                    }
                    
                    // Parse as List<Ticket>
                    List<model.Ticket> tickets = parseJsonArray(arrayValue, model.Ticket.class);
                    field.set(obj, tickets);
                } else if ("seats".equals(fieldName)) {
                    // Handle seats field for Flight objects
                    String arrayValue = value;
                    if (arrayValue.startsWith("\"") && arrayValue.endsWith("\"")) {
                        arrayValue = arrayValue.substring(1, arrayValue.length() - 1);
                        // Unescape characters
                        arrayValue = arrayValue.replace("\\\"", "\"").replace("\\\\", "\\");
                    }
                    
                    // Parse as List<FlightSeat>
                    List<model.FlightSeat> seats = parseJsonArray(arrayValue, model.FlightSeat.class);
                    field.set(obj, seats);
                } else if ("seatNumbers".equals(fieldName)) {
                    // Handle seatNumbers field specifically
                    String arrayValue = value;
                    if (arrayValue.startsWith("\"") && arrayValue.endsWith("\"")) {
                        arrayValue = arrayValue.substring(1, arrayValue.length() - 1);
                        arrayValue = arrayValue.replace("\\\"", "\"").replace("\\\\", "\\");
                    }
                    
                    if (arrayValue.startsWith("[") && arrayValue.endsWith("]")) {
                        List<String> stringList = new ArrayList<>();
                        String arrayContent = arrayValue.substring(1, arrayValue.length() - 1).trim();
                        
                        if (!arrayContent.isEmpty()) {
                            // Split by comma but handle quoted strings
                            String[] items = splitArrayItems(arrayContent);
                            for (String item : items) {
                                item = item.trim();
                                if (item.startsWith("\"") && item.endsWith("\"")) {
                                    item = item.substring(1, item.length() - 1);
                                }
                                stringList.add(item);
                            }
                        }
                        field.set(obj, stringList);
                    }
                } else {
                    // For other lists, try to parse as List<String>
                    String arrayValue = value;
                    if (arrayValue.startsWith("\"") && arrayValue.endsWith("\"")) {
                        arrayValue = arrayValue.substring(1, arrayValue.length() - 1);
                        arrayValue = arrayValue.replace("\\\"", "\"").replace("\\\\", "\\");
                    }
                    
                    if (arrayValue.startsWith("[") && arrayValue.endsWith("]")) {
                        List<String> stringList = new ArrayList<>();
                        String arrayContent = arrayValue.substring(1, arrayValue.length() - 1).trim();
                        if (!arrayContent.isEmpty()) {
                            String[] items = splitArrayItems(arrayContent);
                            for (String item : items) {
                                item = item.trim();
                                if (item.startsWith("\"") && item.endsWith("\"")) {
                                    item = item.substring(1, item.length() - 1);
                                }
                                stringList.add(item);
                            }
                        }
                        field.set(obj, stringList);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error setting field " + fieldName + " to " + value + ": " + e.getMessage());
        }
    }
    
    // Split array items handling quoted strings properly
    private static String[] splitArrayItems(String arrayContent) {
        List<String> items = new ArrayList<>();
        int start = 0;
        boolean inQuotes = false;
        
        for (int i = 0; i < arrayContent.length(); i++) {
            char c = arrayContent.charAt(i);
            if (c == '"' && (i == 0 || arrayContent.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                items.add(arrayContent.substring(start, i));
                start = i + 1;
            }
        }
        
        // Add last item
        if (start < arrayContent.length()) {
            items.add(arrayContent.substring(start));
        }
        
        return items.toArray(new String[0]);
    }

    // Get field by name including inherited fields
    private static Field getFieldByName(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
} 