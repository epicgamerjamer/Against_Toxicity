package net.epicgamerjamer.mod.againsttoxicity.client;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

public class NameHelper {
    @Unique
    private static Config config = AutoConfig.getConfigHolder(Config.class).getConfig();
    public static boolean isPrivate;

    public static @Nullable String getUsername(@NotNull String input) {
        String name = null;
        input = input.replaceAll("»|>>",">")
                .replaceAll("\\[.+]", "");
        String[] split = input.split(" ");

        if (input.contains(" using ") && (input.contains(" was blown up by ") || input.contains(" was slain by ")))
            name = input.substring(input.indexOf(" by ") + 4, input.indexOf(" using"));

        else if (input.toLowerCase().contains(" -> you") || input.toLowerCase().contains(" -> me")) {
            name = input.substring(0, input.toLowerCase().indexOf(" -> "));
            isPrivate = true;
        }

        else if (input.toLowerCase().contains(" whispers to you:") || input.toLowerCase().contains(" whispers:")) {
            name = input.substring(0, input.indexOf(" whispers"));
            isPrivate = true;
        }

        else if (input.startsWith("<--") && input.contains(": ")) {
            name = input.substring(3, input.indexOf(":"));
            isPrivate = true;
        }

        else if (input.contains("<") && input.contains(">")) {
            int diff = (input.indexOf(">")) - (input.indexOf("<"));
            if (diff < 18 && diff > 2) {
                name = input.substring(input.indexOf("<") + 1, input.indexOf(">"));
                isPrivate = false;
            }
        }

        else if (input.contains(":")) {
            for (String s:split) {
                if (s.contains(":")) {
                    name = s.replace(":","");
                    isPrivate = false;
                    break;
                }
            }
        }
        else if (input.contains(" > ")) {
            String s;
            for (int i = 0; i < split.length - 1; i++) {
                s = split[i];
                if (s.length() > 2 && s.length() < 17 && (split[i+1].equals(">") || split[i+1].equals(">>"))) {
                    name = s;
                    isPrivate = false;
                    break;
                }
            }
        }

        if (name != null && name.length() > 1 && name.length() < 30 && !ignorePlayer(name)) {
            name = name.replaceAll("[^a-zA-Z0-9_ ]", "");
            if (name.contains(" ")) name = name.substring(name.indexOf(" "));
            return name.replace(" ", "");
        }
        else {
            return null;
        }
    }
    private static boolean ignorePlayer(String name) {
        assert MinecraftClient.getInstance().player != null;
        if (name.equals(MinecraftClient.getInstance().player.getName().getString())) return true;
        for (String s:config.lists.friends) if (name.equals(s)) return true;
        for (String s:Lists.IgnoreNames) if (name.equals(s)) return true;
        return false;
    }
}