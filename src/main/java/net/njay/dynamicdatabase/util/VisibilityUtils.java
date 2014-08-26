package net.njay.dynamicdatabase.util;

import com.sk89q.minecraft.util.commands.ChatColor;

/**
 * Utilities to handle working with Visibilities
 */
public class VisibilityUtils {

    /**
     * Returns a ChatColor corresponding to a visibility
     *
     * @param visibility Visibility to get the color for
     * @return The appropriate color corresponding the the supplied visibility
     */
    public ChatColor getVisibilityColor(Visibility visibility) {
        switch (visibility) {
            case PUBLIC:
                return ChatColor.GREEN;
            case STAFF_ONLY:
                return ChatColor.GOLD;
            case PRIVATE:
                return ChatColor.RED;
        }
        return ChatColor.WHITE;
    }

    /**
     * Returns a colored message to display a server's visibility
     *
     * @param visibility Visibility to get the message for
     * @return A message with the appropriate color and display name for the visibility
     */
    public String visibilityText(Visibility visibility) {
        return getVisibilityColor(visibility) + visibility.displayName;
    }
}
