package net.njay.dynamicdatabase.util;

import com.sk89q.minecraft.util.commands.ChatColor;
import com.sk89q.minecraft.util.pagination.PaginatedResult;

public abstract class PrettyPaginatedResult<T> extends PaginatedResult<T> {
    protected final String header;

    public PrettyPaginatedResult(String header) {
        this(header, 8);
    }

    public PrettyPaginatedResult(String header, int resultsPerPage) {
        super(resultsPerPage);
        this.header = header;
    }

    @Override
    public String formatHeader(int page, int maxPages) {
        ChatColor dashColor = ChatColor.GREEN;
        ChatColor textColor = ChatColor.DARK_AQUA;
        ChatColor highlight = ChatColor.AQUA;

        String message = this.header + textColor + " (" + highlight + page + textColor + " of " + highlight + maxPages + textColor + ")";
        return StringUtils.padMessage(message, "-", dashColor, textColor);
    }
}
