package com.kirdow.mentioned.config;

import com.mojang.datafixers.util.Pair;
import com.sun.jna.platform.win32.Winsvc;

import java.util.ArrayList;
import java.util.List;

public class ModConfigProvider implements SimpleConfig.DefaultConfig {

    private StringBuilder configContent = new StringBuilder();
    private final List<Pair> configList = new ArrayList<>();
    private String _string = null;

    public List<Pair> getConfigList() {
        return configList;
    }

    public ModConfigProvider addComment(String...comments) {
        for (String comment : comments) {
            configContent.append("# ").append(comment).append("\n");
        }

        return this;
    }

    public ModConfigProvider skip() {
        configContent.append("\n");
        return this;
    }

    public <T> ModConfigProvider addKeyValuePair(String key, T value) {
        return addKeyValuePair(new Pair<>(key, value));
    }

    public <T> ModConfigProvider addKeyValuePair(String key, T value, String comment) {
        return addKeyValuePair(new Pair<>(key, value), comment);
    }

    public ModConfigProvider addKeyValuePair(Pair<String, ?> keyValuePair) {
        return addKeyValuePair(keyValuePair, null);
    }

    public ModConfigProvider addKeyValuePair(Pair<String, ?> keyValuePair, String comment) {
        _string = null;
        configList.add(keyValuePair);
        configContent.append(keyValuePair.getFirst()).append("=").append(keyValuePair.getSecond()).append(" # ");
        if (comment != null) {
            configContent.append(comment).append(" | ");
        }
        configContent.append("default: ").append(keyValuePair.getSecond()).append("\n");

        return this;
    }

    @Override
    public String get(String namespace) {
        if (_string != null) {
            return _string;
        }

        return _string = configContent.toString();
    }
}
