package com.gameZone.model;

/**
 * Enumerates the valid product categories used by category-based promotions.
 */
public enum Category {
    VIDEOGAME,
    CONSOLE,
    ACCESSORY;

    /**
     * Parses a string into a Category, case-insensitively.
     *
     * @param value the string to parse
     * @return the matching Category
     * @throws IllegalArgumentException if the value does not match any category
     */
    public static Category fromString(String value) {
        for (Category c : values()) {
            if (c.name().equalsIgnoreCase(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Invalid category: " + value);
    }
}