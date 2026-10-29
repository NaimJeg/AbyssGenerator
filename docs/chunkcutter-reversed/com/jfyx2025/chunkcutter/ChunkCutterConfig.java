/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.loader.api.FabricLoader
 */
package com.jfyx2025.chunkcutter;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

public final class ChunkCutterConfig {
    public static final String FILE_NAME = "chunkcutter.properties";
    public boolean enabled = true;
    public int minSectionY = -32;
    public int maxSectionY = 256;
    public final boolean migrateCubesOnLoad = true;
    private static volatile ChunkCutterConfig INSTANCE;

    private ChunkCutterConfig() {
    }

    public static boolean isEnabled() {
        return ChunkCutterConfig.get().enabled;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static ChunkCutterConfig get() {
        ChunkCutterConfig c = INSTANCE;
        if (c != null) return c;
        Class<ChunkCutterConfig> clazz = ChunkCutterConfig.class;
        synchronized (ChunkCutterConfig.class) {
            if (INSTANCE != null) return INSTANCE;
            INSTANCE = ChunkCutterConfig.load();
            return INSTANCE;
        }
    }

    public static synchronized ChunkCutterConfig load() {
        ChunkCutterConfig c = new ChunkCutterConfig();
        Path cfg = ChunkCutterConfig.file();
        if (!Files.exists(cfg, new LinkOption[0])) {
            c.save();
            INSTANCE = c;
            return c;
        }
        try (InputStream in = Files.newInputStream(cfg, new OpenOption[0]);){
            Properties p = new Properties();
            p.load(in);
            c.enabled = ChunkCutterConfig.parseSwitch(p, "enabled", c.enabled);
            c.minSectionY = ChunkCutterConfig.parseInt(p, "minSectionY", c.minSectionY);
            c.maxSectionY = ChunkCutterConfig.parseInt(p, "maxSectionY", c.maxSectionY);
        }
        catch (Exception exception) {
            // empty catch block
        }
        INSTANCE = c;
        return c;
    }

    public synchronized void save() {
        Path cfg = ChunkCutterConfig.file();
        StringBuilder sb = new StringBuilder();
        sb.append("# ===== ChunkCutter \u914d\u7f6e\u6587\u4ef6 =====\n");
        sb.append("# \u4fee\u6539\u540e\u9700\u91cd\u542f\u6e38\u620f\u751f\u6548\u3002\n");
        sb.append("# \u5ba2\u6237\u7aef\u4e0e\u670d\u52a1\u7aef\u5e94\u4fdd\u6301\u4e00\u81f4\u3002\n\n");
        sb.append("# \u6a21\u7ec4\u603b\u5f00\u5173\uff1a1 = \u5f00\u542f\uff0c0 = \u5173\u95ed\uff08\u5173\u95ed\u540e\u56de\u5230\u539f\u7248\uff09\u3002\n");
        sb.append("enabled=").append(this.enabled ? "1" : "0").append("\n\n");
        sb.append("# \u4e16\u754c\u5782\u76f4\u8303\u56f4\uff08section\uff1b1 section = 16 \u683c\uff09\u3002\n");
        sb.append("# y \u8303\u56f4 = minSectionY*16 .. (maxSectionY+1)*16-1\n");
        sb.append("minSectionY=").append(this.minSectionY).append("\n");
        sb.append("maxSectionY=").append(this.maxSectionY).append("\n");
        try {
            Files.createDirectories(cfg.getParent(), new FileAttribute[0]);
            Files.writeString(cfg, (CharSequence)sb.toString(), StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public String describe() {
        int minY = this.minSectionY * 16;
        int maxY = (this.maxSectionY + 1) * 16 - 1;
        return "enabled=" + (this.enabled ? "1" : "0") + ", sections=[" + this.minSectionY + ".." + this.maxSectionY + "] (y " + minY + ".." + maxY + ", " + (this.maxSectionY - this.minSectionY + 1) + " sections)";
    }

    public static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    private static boolean parseSwitch(Properties p, String key, boolean def) {
        String v = p.getProperty(key);
        if (v == null) {
            return def;
        }
        String value = v.trim();
        if ("1".equals(value)) {
            return true;
        }
        if ("0".equals(value)) {
            return false;
        }
        return def;
    }

    private static int parseInt(Properties p, String key, int def) {
        String v = p.getProperty(key);
        return v == null ? def : Integer.parseInt(v.trim());
    }
}

