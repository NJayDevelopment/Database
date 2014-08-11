package us.nsakt.dynamicdatabase.util;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.sk89q.minecraft.util.commands.ChatColor;
import org.bukkit.util.ChatPaginator;

import java.util.Collection;
import java.util.Date;

/**
 * Methods found in OvercastNetwork's Commons and Whitelister
 *
 * @author OvercastNetwork
 */
public class StringUtils {
    /**
     * Pads a message using dashes
     *
     * @param String    message to pad
     * @param String    c character to pad message with
     * @param ChatColor dashColor Color of dashes
     * @param ChatColor messageColor Color of message
     * @return String padded message
     */
    public static String padMessage(String message, String c, ChatColor dashColor,
                                    ChatColor messageColor) {
        Iterable<String> messages = Splitter.fixedLength(45).split(message);
        StringBuilder dashedMessages = new StringBuilder();
        int size = 0;
        for (String messagepart : messages) {
            size++;
            String dashes =
                    Strings.repeat(c, (ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH
                            - ChatColor.stripColor(messagepart).length() - 2)
                            / (c.length() * 2));
            dashedMessages.append(dashColor + dashes + ChatColor.RESET + messageColor + messagepart + ChatColor.RESET
                    + dashColor + dashes);
            if (size > 1) dashedMessages.append("\n");
        }
        return dashedMessages.toString();
    }

    /**
     * Shorthand for listToEnglishCompound(list, "", "").
     *
     * @see #listToEnglishCompound(java.util.Collection, String, String)
     */
    public static String listToEnglishCompound(Collection<String> list) {
        return listToEnglishCompound(list, "", "");
    }

    /**
     * Converts a list of strings to a nice English list as a string.
     * For example: In: ["Anxuiz", "MonsieurApple", "Plastix"] Out: "Anxuiz, MonsieurApple and Plastix"
     *
     * @param list   List of strings to concatenate.
     * @param prefix Prefix to add before each element in the resulting string.
     * @param suffix Suffix to add after each element in the resulting string.
     * @return String version of the list of strings.
     */
    public static String listToEnglishCompound(Collection<?> list, String prefix, String suffix) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (Object str : list) {
            if (i != 0) {
                if (i == list.size() - 1) {
                    builder.append(" and ");
                } else {
                    builder.append(", ");
                }
            }
            builder.append(prefix).append(str).append(suffix);
            i++;
        }
        return builder.toString();
    }

    public static <T> T bestFuzzyMatch(String search, Collection<T> options, double threshold) {
        T bestObj = null;
        double bestScore = 0.0;
        for (T obj : options) {
            double score = LiquidMetal.score(obj.toString(), search);
            if (score > bestScore) {
                bestObj = obj;
                bestScore = score;
            } else if (score == bestScore) {
                bestObj = null;
            }
        }
        return bestScore < threshold ? null : bestObj;
    }

    /**
     * Sanitizes the provided message, removing any non-alphanumeric characters and swapping spaces with the specified string.
     * Examples: sanitize("Hello! :) How are you?", '-') --> "Hello--How-are-you" sanitize("I am great, thank you!", '*') --> "I*am*great*thank*you"
     *
     * @param string       The message to be sanitized.
     * @param spaceReplace The string to be substituted for spaces.
     * @return The sanitized string.
     */
    public static String sanitize(String string, String spaceReplace) {
        return string.replaceAll("[^\\dA-Za-z ]", "").replaceAll("\\s+", spaceReplace);
    }

    /**
     * Cuts a String to ensure it's length to be no longer than maximumLength
     *
     * @param string        String to cut
     * @param maximumLength Maximum length of string
     * @return String string with length no larger than maximumLength
     */
    public static String ensureStringSize(String string, int maximumLength) {
        return string.substring(0, Math.min(string.length(), maximumLength));
    }

    /**
     * Cuts a String to ensure it's length to be no longer than 16 characters
     *
     * @param string String to cut
     * @return String string with length no larger than maximumLength
     */
    public static String ensureStringSize(String string) {
        return string.substring(0, Math.min(string.length(), 16));
    }

    public static String _ago(Date when) {
        int tm = (int) (when.getTime() / 1000);
        int cur_tm = (int) (new Date().getTime() / 1000);
        int diff = cur_tm - tm;
        System.out.println(diff);

        String[] pds = {"second", "minute", "hour", "day", "week", "month", "year", "decade"};

        int[] lngh = {1, 60, 3600, 86400, 604800, 2630880, 31570560, 315705600};

        int v;
        double no = 0;

        for (v = lngh.length - 1; (v >= 0) && ((no = diff / lngh[v]) <= 1); v--) {
            if (v < 0) {
                v = 0;
            }

            tm = cur_tm - (diff % lngh[v]);
        }

        no = Math.floor(no);

        if (no != 1) {
            pds[v] += "s";
        }

        return String.valueOf(no) + " " + pds[v] + " ";
    }

}
