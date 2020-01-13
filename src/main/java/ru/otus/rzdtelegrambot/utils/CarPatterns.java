package ru.otus.rzdtelegrambot.utils;

import java.util.regex.Pattern;

/**
 * @author Sergei Viacheslaev
 */
public class CarPatterns {
    public static final Pattern KUPE_START = Pattern.compile("Купе:.*от\\s");
    public static final Pattern KUPE_END = Pattern.compile("Купе:.*от\\s\\d*");
    public static final Pattern PLACKART_START = Pattern.compile("Плац:.*от\\s");
    public static final Pattern PLACKART_END = Pattern.compile("Плац:.*от\\s\\d*");
    public static final Pattern LUX_START = Pattern.compile("Люкс:.*от\\s");
    public static final Pattern LUX_END = Pattern.compile("Люкс:.*от\\s\\d*");
    public static final Pattern MIAGKIY_START = Pattern.compile("Мягкий:.*от\\s");
    public static final Pattern MIAGKIY_END = Pattern.compile("Мягкий:.*от\\s\\d*");
    public static final Pattern SIDYACHI_START = Pattern.compile("Сид:.*от\\s");
    public static final Pattern SIDYACHI_END = Pattern.compile("Сид:.*от\\s\\d*");

}
