package us.nsakt.dynamicdatabase.commands;

import com.sk89q.minecraft.util.commands.CommandException;
import us.nsakt.dynamicdatabase.daos.DAOService;
import us.nsakt.dynamicdatabase.documents.UserDocument;

import java.util.List;

public class CommandUtil {
    public static UserDocument getOneOfflinePlayer(String username) throws CommandException {
        List<UserDocument> matchingDocuments = DAOService.getUsers().getAllMatchingUsers(username, 1000);

        if (matchingDocuments == null || matchingDocuments.isEmpty())
            throw new CommandException("No players found by that username!");
        else if (matchingDocuments.size() > 1)
            throw new CommandException("Multiple players matching by name " + username + ". Please try to be more specific with your query.");
        else
            return matchingDocuments.get(0);
    }
}
