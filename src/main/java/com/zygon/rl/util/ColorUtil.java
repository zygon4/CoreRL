package com.zygon.rl.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zygon
 */
public final class ColorUtil {

    private static final Map<String, ColorInfo> COLORS = new HashMap<>();

    static {
        // loads into COLORS static map
        load();
    }

    private ColorUtil() {

    }

    public static Color get(String name) {
        ColorInfo colorInfo = COLORS.get(name);
        if (colorInfo == null) {
            throw new UnsupportedOperationException("Cannot find color: " + name);
        }
        return colorInfo.getColor();
    }

    /**
     * Initialize the color list that we have.
     */
    private static void load() {

        put(new ColorInfo("AliceBlue", 0xF0, 0xF8, 0xFF));
        put(new ColorInfo("AntiqueWhite", 0xFA, 0xEB, 0xD7));
        put(new ColorInfo("Aqua", 0x00, 0xFF, 0xFF));
        put(new ColorInfo("Aquamarine", 0x7F, 0xFF, 0xD4));
        put(new ColorInfo("Azure", 0xF0, 0xFF, 0xFF));
        put(new ColorInfo("Beige", 0xF5, 0xF5, 0xDC));
        put(new ColorInfo("Bisque", 0xFF, 0xE4, 0xC4));
        put(new ColorInfo("Black", 0x00, 0x00, 0x00));
        put(new ColorInfo("BlanchedAlmond", 0xFF, 0xEB, 0xCD));
        put(new ColorInfo("Blue", 0x00, 0x00, 0xFF));
        put(new ColorInfo("BlueViolet", 0x8A, 0x2B, 0xE2));
        put(new ColorInfo("Brown", 0xA5, 0x2A, 0x2A));
        put(new ColorInfo("BurlyWood", 0xDE, 0xB8, 0x87));
        put(new ColorInfo("CadetBlue", 0x5F, 0x9E, 0xA0));
        put(new ColorInfo("Chartreuse", 0x7F, 0xFF, 0x00));
        put(new ColorInfo("Chocolate", 0xD2, 0x69, 0x1E));
        put(new ColorInfo("Coral", 0xFF, 0x7F, 0x50));
        put(new ColorInfo("CornflowerBlue", 0x64, 0x95, 0xED));
        put(new ColorInfo("Cornsilk", 0xFF, 0xF8, 0xDC));
        put(new ColorInfo("Crimson", 0xDC, 0x14, 0x3C));
        put(new ColorInfo("Cyan", 0x00, 0xFF, 0xFF));
        put(new ColorInfo("DarkBlue", 0x00, 0x00, 0x8B));
        put(new ColorInfo("DarkCyan", 0x00, 0x8B, 0x8B));
        put(new ColorInfo("DarkGoldenRod", 0xB8, 0x86, 0x0B));
        put(new ColorInfo("DarkGray", 0xA9, 0xA9, 0xA9));
        put(new ColorInfo("DarkGreen", 0x00, 0x64, 0x00));
        put(new ColorInfo("DarkKhaki", 0xBD, 0xB7, 0x6B));
        put(new ColorInfo("DarkMagenta", 0x8B, 0x00, 0x8B));
        put(new ColorInfo("DarkOliveGreen", 0x55, 0x6B, 0x2F));
        put(new ColorInfo("DarkOrange", 0xFF, 0x8C, 0x00));
        put(new ColorInfo("DarkOrchid", 0x99, 0x32, 0xCC));
        put(new ColorInfo("DarkRed", 0x8B, 0x00, 0x00));
        put(new ColorInfo("DarkSalmon", 0xE9, 0x96, 0x7A));
        put(new ColorInfo("DarkSeaGreen", 0x8F, 0xBC, 0x8F));
        put(new ColorInfo("DarkSlateBlue", 0x48, 0x3D, 0x8B));
        put(new ColorInfo("DarkSlateGray", 0x2F, 0x4F, 0x4F));
        put(new ColorInfo("DarkTurquoise", 0x00, 0xCE, 0xD1));
        put(new ColorInfo("DarkViolet", 0x94, 0x00, 0xD3));
        put(new ColorInfo("DeepPink", 0xFF, 0x14, 0x93));
        put(new ColorInfo("DeepSkyBlue", 0x00, 0xBF, 0xFF));
        put(new ColorInfo("DimGray", 0x69, 0x69, 0x69));
        put(new ColorInfo("DodgerBlue", 0x1E, 0x90, 0xFF));
        put(new ColorInfo("FireBrick", 0xB2, 0x22, 0x22));
        put(new ColorInfo("FloralWhite", 0xFF, 0xFA, 0xF0));
        put(new ColorInfo("ForestGreen", 0x22, 0x8B, 0x22));
        put(new ColorInfo("Fuchsia", 0xFF, 0x00, 0xFF));
        put(new ColorInfo("Gainsboro", 0xDC, 0xDC, 0xDC));
        put(new ColorInfo("GhostWhite", 0xF8, 0xF8, 0xFF));
        put(new ColorInfo("Gold", 0xFF, 0xD7, 0x00));
        put(new ColorInfo("GoldenRod", 0xDA, 0xA5, 0x20));
        put(new ColorInfo("Gray", 0x80, 0x80, 0x80));
        put(new ColorInfo("Green", 0x00, 0x80, 0x00));
        put(new ColorInfo("GreenYellow", 0xAD, 0xFF, 0x2F));
        put(new ColorInfo("HoneyDew", 0xF0, 0xFF, 0xF0));
        put(new ColorInfo("HotPink", 0xFF, 0x69, 0xB4));
        put(new ColorInfo("IndianRed", 0xCD, 0x5C, 0x5C));
        put(new ColorInfo("Indigo", 0x4B, 0x00, 0x82));
        put(new ColorInfo("Ivory", 0xFF, 0xFF, 0xF0));
        put(new ColorInfo("Khaki", 0xF0, 0xE6, 0x8C));
        put(new ColorInfo("Lavender", 0xE6, 0xE6, 0xFA));
        put(new ColorInfo("LavenderBlush", 0xFF, 0xF0, 0xF5));
        put(new ColorInfo("LawnGreen", 0x7C, 0xFC, 0x00));
        put(new ColorInfo("LemonChiffon", 0xFF, 0xFA, 0xCD));
        put(new ColorInfo("LightBlue", 0xAD, 0xD8, 0xE6));
        put(new ColorInfo("LightCoral", 0xF0, 0x80, 0x80));
        put(new ColorInfo("LightCyan", 0xE0, 0xFF, 0xFF));
        put(new ColorInfo("LightGoldenRodYellow", 0xFA, 0xFA, 0xD2));
        put(new ColorInfo("LightGray", 0xD3, 0xD3, 0xD3));
        put(new ColorInfo("LightGreen", 0x90, 0xEE, 0x90));
        put(new ColorInfo("LightPink", 0xFF, 0xB6, 0xC1));
        put(new ColorInfo("LightSalmon", 0xFF, 0xA0, 0x7A));
        put(new ColorInfo("LightSeaGreen", 0x20, 0xB2, 0xAA));
        put(new ColorInfo("LightSkyBlue", 0x87, 0xCE, 0xFA));
        put(new ColorInfo("LightSlateGray", 0x77, 0x88, 0x99));
        put(new ColorInfo("LightSteelBlue", 0xB0, 0xC4, 0xDE));
        put(new ColorInfo("LightYellow", 0xFF, 0xFF, 0xE0));
        put(new ColorInfo("Lime", 0x00, 0xFF, 0x00));
        put(new ColorInfo("LimeGreen", 0x32, 0xCD, 0x32));
        put(new ColorInfo("Linen", 0xFA, 0xF0, 0xE6));
        put(new ColorInfo("Magenta", 0xFF, 0x00, 0xFF));
        put(new ColorInfo("Maroon", 0x80, 0x00, 0x00));
        put(new ColorInfo("MediumAquaMarine", 0x66, 0xCD, 0xAA));
        put(new ColorInfo("MediumBlue", 0x00, 0x00, 0xCD));
        put(new ColorInfo("MediumOrchid", 0xBA, 0x55, 0xD3));
        put(new ColorInfo("MediumPurple", 0x93, 0x70, 0xDB));
        put(new ColorInfo("MediumSeaGreen", 0x3C, 0xB3, 0x71));
        put(new ColorInfo("MediumSlateBlue", 0x7B, 0x68, 0xEE));
        put(new ColorInfo("MediumSpringGreen", 0x00, 0xFA, 0x9A));
        put(new ColorInfo("MediumTurquoise", 0x48, 0xD1, 0xCC));
        put(new ColorInfo("MediumVioletRed", 0xC7, 0x15, 0x85));
        put(new ColorInfo("MidnightBlue", 0x19, 0x19, 0x70));
        put(new ColorInfo("MintCream", 0xF5, 0xFF, 0xFA));
        put(new ColorInfo("MistyRose", 0xFF, 0xE4, 0xE1));
        put(new ColorInfo("Moccasin", 0xFF, 0xE4, 0xB5));
        put(new ColorInfo("NavajoWhite", 0xFF, 0xDE, 0xAD));
        put(new ColorInfo("Navy", 0x00, 0x00, 0x80));
        put(new ColorInfo("OldLace", 0xFD, 0xF5, 0xE6));
        put(new ColorInfo("Olive", 0x80, 0x80, 0x00));
        put(new ColorInfo("OliveDrab", 0x6B, 0x8E, 0x23));
        put(new ColorInfo("Orange", 0xFF, 0xA5, 0x00));
        put(new ColorInfo("OrangeRed", 0xFF, 0x45, 0x00));
        put(new ColorInfo("Orchid", 0xDA, 0x70, 0xD6));
        put(new ColorInfo("PaleGoldenRod", 0xEE, 0xE8, 0xAA));
        put(new ColorInfo("PaleGreen", 0x98, 0xFB, 0x98));
        put(new ColorInfo("PaleTurquoise", 0xAF, 0xEE, 0xEE));
        put(new ColorInfo("PaleVioletRed", 0xDB, 0x70, 0x93));
        put(new ColorInfo("PapayaWhip", 0xFF, 0xEF, 0xD5));
        put(new ColorInfo("PeachPuff", 0xFF, 0xDA, 0xB9));
        put(new ColorInfo("Peru", 0xCD, 0x85, 0x3F));
        put(new ColorInfo("Pink", 0xFF, 0xC0, 0xCB));
        put(new ColorInfo("Plum", 0xDD, 0xA0, 0xDD));
        put(new ColorInfo("PowderBlue", 0xB0, 0xE0, 0xE6));
        put(new ColorInfo("Purple", 0x80, 0x00, 0x80));
        put(new ColorInfo("Red", 0xFF, 0x00, 0x00));
        put(new ColorInfo("RosyBrown", 0xBC, 0x8F, 0x8F));
        put(new ColorInfo("RoyalBlue", 0x41, 0x69, 0xE1));
        put(new ColorInfo("SaddleBrown", 0x8B, 0x45, 0x13));
        put(new ColorInfo("Salmon", 0xFA, 0x80, 0x72));
        put(new ColorInfo("SandyBrown", 0xF4, 0xA4, 0x60));
        put(new ColorInfo("SeaGreen", 0x2E, 0x8B, 0x57));
        put(new ColorInfo("SeaShell", 0xFF, 0xF5, 0xEE));
        put(new ColorInfo("Sienna", 0xA0, 0x52, 0x2D));
        put(new ColorInfo("Silver", 0xC0, 0xC0, 0xC0));
        put(new ColorInfo("SkyBlue", 0x87, 0xCE, 0xEB));
        put(new ColorInfo("SlateBlue", 0x6A, 0x5A, 0xCD));
        put(new ColorInfo("SlateGray", 0x70, 0x80, 0x90));
        put(new ColorInfo("Snow", 0xFF, 0xFA, 0xFA));
        put(new ColorInfo("SpringGreen", 0x00, 0xFF, 0x7F));
        put(new ColorInfo("SteelBlue", 0x46, 0x82, 0xB4));
        put(new ColorInfo("Tan", 0xD2, 0xB4, 0x8C));
        put(new ColorInfo("Teal", 0x00, 0x80, 0x80));
        put(new ColorInfo("Thistle", 0xD8, 0xBF, 0xD8));
        put(new ColorInfo("Tomato", 0xFF, 0x63, 0x47));
        put(new ColorInfo("Turquoise", 0x40, 0xE0, 0xD0));
        put(new ColorInfo("Violet", 0xEE, 0x82, 0xEE));
        put(new ColorInfo("Wheat", 0xF5, 0xDE, 0xB3));
        put(new ColorInfo("White", 0xFF, 0xFF, 0xFF));
        put(new ColorInfo("WhiteSmoke", 0xF5, 0xF5, 0xF5));
        put(new ColorInfo("Yellow", 0xFF, 0xFF, 0x00));
        put(new ColorInfo("YellowGreen", 0x9A, 0xCD, 0x32));
    }

    private static void put(ColorInfo colorName) {
        COLORS.put(colorName.getName(), colorName);
    }

    private static class ColorInfo {

        public int r, g, b;
        public String name;

        public ColorInfo(String name, int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.name = name;
        }

        public int computeMSE(int pixR, int pixG, int pixB) {
            return (int) (((pixR - r) * (pixR - r) + (pixG - g) * (pixG - g) + (pixB - b)
                    * (pixB - b)) / 3);
        }

        public int getR() {
            return r;
        }

        public int getG() {
            return g;
        }

        public int getB() {
            return b;
        }

        public String getName() {
            return name;
        }

        public Color getColor() {
            return new Color(r, g, b);
        }
    }
}
