package us.nsakt.dynamicdatabase.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.daos.DAOService;
import us.nsakt.dynamicdatabase.daos.Users;
import us.nsakt.dynamicdatabase.documents.PunishmentDocument;
import us.nsakt.dynamicdatabase.documents.UserDocument;
import us.nsakt.dynamicdatabase.tasks.PunishmentTasks;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallBack;

import java.util.Date;
import java.util.UUID;

public class PunishmentCommands {
    @Command(
            aliases = {"p", "mason", "punish", "destroy", "rek", "lolgetowned"},
            desc = "Take away all the OllyCoins of a player for being very naughty",
            min = 2,
            max = -1
    )
    @CommandPermissions("dynamicdb.punish")
    public static void staffList(final CommandContext args, final CommandSender sender) throws CommandException {
        String reason = args.getJoinedStrings(1);
        UserDocument toWrek = CommandUtil.getOneOfflinePlayer(args.getString(0));

        DBCallBack callBack = new DBCallBack() {
            @Override
            public void call(Object... objects) {
                PunishmentDocument document = (PunishmentDocument) objects[0];
                UUID player = document.getPunished();
                if (Bukkit.getPlayer(player) == null) return;
                Bukkit.getPlayer(player).kickPlayer(document.generateKickmessage());
            }
        };

        PunishmentDocument punishmentDocument = new PunishmentDocument();
        punishmentDocument.setActive(true);
        punishmentDocument.setAutomatic(false);
        punishmentDocument.setCluster(DynamicDatabasePlugin.getInstance().getCurrentServerDocument().getCluster());
        punishmentDocument.setPunished(toWrek.getUuid());
        punishmentDocument.setWhen(new Date());
        punishmentDocument.setPunisher((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);
        punishmentDocument.setReason(reason);
        punishmentDocument.setServer(DynamicDatabasePlugin.getInstance().getCurrentServerDocument());
        PunishmentTasks.punish(punishmentDocument, callBack);
    }
}
