package bwillows.itemcooldown;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Utils {
    public String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        if (minutes > 0) {
            return minutes + "m " + seconds + "s";
        } else {
            return seconds + "s";
        }
    }

    public boolean isAtLeastMinecraft_1_11() {
        String version = Bukkit.getBukkitVersion(); // e.g. "1.21.4-R0.1-SNAPSHOT"
        String[] parts = version.split("\\.");

        try {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);

            return major > 1 || (major == 1 && minor >= 11);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Could not parse version string — assume not safe
            return false;
        }
    }

    /**
     * Checks if the server is running on a version >= 1.13.
     *
     * @return true if the server version is 1.13 or higher, false otherwise
     */
    public boolean isNewerVersion() {
        // Get the full version string from Bukkit
        String version = Bukkit.getVersion();  // e.g. "git-PaperSpigot-445 (MC: 1.8.8)"

        // Extract the version number after "MC:" in the string
        String[] versionParts = version.split("MC: ")[1].split("\\."); // Split at "MC: " and then by "."

        try {
            int majorVersion = Integer.parseInt(versionParts[0]);  // Get the major version (e.g., 1)
            int minorVersion = Integer.parseInt(versionParts[1]);  // Get the minor version (e.g., 8 for 1.8)

            // Check if the version is 1.13 or later
            return majorVersion > 1 || (majorVersion == 1 && minorVersion >= 13);
        } catch (Exception e) {
            // If the version string is not in the expected format, log an error and return false
            Bukkit.getLogger().warning("Failed to parse Minecraft version string: " + version);
            return false;
        }
    }

    public boolean isGoldenApple(ItemStack itemStack) {
        if(isNewerVersion()) {
            if(itemStack.getType().equals(Material.GOLDEN_APPLE)) {
                return true;
            }
        } else {
            if(itemStack.getType().equals(Material.GOLDEN_APPLE) && itemStack.getDurability() == 0) {
                return true;
            }
        }
        return false;
    }
    public boolean isEnchantedGoldenApple(ItemStack itemStack) {
        if(isNewerVersion()) {
            if(itemStack.getType().equals(Material.ENCHANTED_GOLDEN_APPLE)) {
                return true;
            }
        } else {
            if(itemStack.getType().equals(Material.GOLDEN_APPLE) && itemStack.getDurability() == 1) {
                return true;
            }
        }
        return false;
    }
}
