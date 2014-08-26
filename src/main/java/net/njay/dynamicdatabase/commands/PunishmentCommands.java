package net.njay.dynamicdatabase.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import net.njay.dynamicdatabase.DynamicDatabasePlugin;
import net.njay.dynamicdatabase.MongoExecutionService;
import net.njay.dynamicdatabase.daos.DAOService;
import net.njay.dynamicdatabase.documents.PunishmentDocument;
import net.njay.dynamicdatabase.documents.UserDocument;
import net.njay.dynamicdatabase.tasks.PunishmentTasks;
import net.njay.dynamicdatabase.tasks.core.SaveTask;
import net.njay.dynamicdatabase.tasks.core.base.DBCallBack;
import net.njay.dynamicdatabase.util.BroadcastUtils;

import java.util.Date;
import java.util.UUID;

public class PunishmentCommands {
    @Command(
            aliases = {"punish", "p"},
            desc = "Punish a user for breaking the rules.",
            min = 2,
            max = -1
    )
    @CommandPermissions("dynamicdb.punishments.punish")
    public static void punish(final CommandContext args, final CommandSender sender) throws CommandException {
        String reason = args.getJoinedStrings(1);
        UserDocument punished = CommandUtil.getOneOfflinePlayer(args.getString(0));

        DBCallBack callBack = new DBCallBack() {
            @Override
            public void call(Object... objects) {
                PunishmentDocument document = (PunishmentDocument) objects[0];
                UUID player = document.getPunished();
                if (Bukkit.getPlayer(player) == null) return;
                Bukkit.getPlayer(player).kickPlayer(document.generateKickMessage());
                broadcastToStaff(document);
            }
        };

        PunishmentDocument punishmentDocument = generateDefaultPunish();

        punishmentDocument.setPunished(punished.getUuid());
        punishmentDocument.setPunisher((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);
        punishmentDocument.setReason(reason);
        PunishmentTasks.punish(punishmentDocument, callBack);
    }

    @Command(
            aliases = {"warn"},
            desc = "Warn a user for breaking the rules",
            min = 2,
            max = -1
    )
    @CommandPermissions("dynamicdb.punishments.warn")
    public static void warn(final CommandContext args, final CommandSender sender) throws CommandException {
        String reason = args.getJoinedStrings(1);
        final UserDocument punished = CommandUtil.getOneOfflinePlayer(args.getString(0));

        final PunishmentDocument punishmentDocument = generateDefaultPunish();

        punishmentDocument.setPunished(punished.getUuid());
        punishmentDocument.setPunisher((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);
        punishmentDocument.setReason(reason);
        punishmentDocument.setType(PunishmentDocument.PunishmentType.WARN);
        SaveTask runnable = new SaveTask(DAOService.getPunishments().getDatastore(), punishmentDocument) {
            @Override
            public void run() {
                getDatastore().save(punishmentDocument);
                Player player = Bukkit.getPlayer(punished.getUuid());
                if (player == null) return;
                player.sendMessage(punishmentDocument.generateWarnMessage());
                player.playSound(player.getLocation(), Sound.DONKEY_DEATH, 45, 0.4F);
                broadcastToStaff(punishmentDocument);
            }
        };
        MongoExecutionService.getExecutorService().execute(runnable);
    }

    @Command(
            aliases = {"kick"},
            desc = "Kick a user for breaking the rules",
            min = 2,
            max = -1
    )
    @CommandPermissions("dynamicdb.punishments.kick")
    public static void kick(final CommandContext args, final CommandSender sender) throws CommandException {
        String reason = args.getJoinedStrings(1);
        final UserDocument punished = CommandUtil.getOneOfflinePlayer(args.getString(0));

        final PunishmentDocument punishmentDocument = generateDefaultPunish();

        punishmentDocument.setPunished(punished.getUuid());
        punishmentDocument.setPunisher((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);
        punishmentDocument.setReason(reason);
        punishmentDocument.setType(PunishmentDocument.PunishmentType.KICK);
        SaveTask runnable = new SaveTask(DAOService.getPunishments().getDatastore(), punishmentDocument) {
            @Override
            public void run() {
                getDatastore().save(punishmentDocument);
                Player player = Bukkit.getPlayer(punished.getUuid());
                if (player == null) return;
                player.kickPlayer(punishmentDocument.generateKickMessage());
                broadcastToStaff(punishmentDocument);
            }
        };
        MongoExecutionService.getExecutorService().execute(runnable);
    }

    @Command(
            aliases = {"tb", "tempban"},
            desc = "Temporarily ban a user for breaking the rules",
            min = 3,
            usage = "<player> <days> <reason>",
            max = -1
    )
    @CommandPermissions("dynamicdb.punishments.tempban")
    public static void tempBan(final CommandContext args, final CommandSender sender) throws CommandException {
        String reason = args.getJoinedStrings(2);
        final UserDocument punished = CommandUtil.getOneOfflinePlayer(args.getString(0));

        final PunishmentDocument punishmentDocument = generateDefaultPunish();

        punishmentDocument.setPunished(punished.getUuid());
        punishmentDocument.setPunisher((sender instanceof Player) ? ((Player) sender).getUniqueId() : null);
        punishmentDocument.setReason(reason);
        punishmentDocument.setType(PunishmentDocument.PunishmentType.BAN);
        punishmentDocument.setExpires(new DateTime().plus(Duration.standardDays(Long.parseLong(args.getString(1)))).toDate());
        SaveTask runnable = new SaveTask(DAOService.getPunishments().getDatastore(), punishmentDocument) {
            @Override
            public void run() {
                getDatastore().save(punishmentDocument);
                Player player = Bukkit.getPlayer(punished.getUuid());
                if (player == null) return;
                player.kickPlayer(punishmentDocument.generateKickMessage());
                broadcastToStaff(punishmentDocument);
            }
        };
        MongoExecutionService.getExecutorService().execute(runnable);
    }

    private static PunishmentDocument generateDefaultPunish() {
        PunishmentDocument punishmentDocument = new PunishmentDocument();

        punishmentDocument.setCluster(DynamicDatabasePlugin.getInstance().getCurrentServerDocument().getCluster());
        punishmentDocument.setServer(DynamicDatabasePlugin.getInstance().getCurrentServerDocument());

        punishmentDocument.setActive(true);
        punishmentDocument.setAutomatic(false);
        punishmentDocument.setAppealable(true);

        punishmentDocument.setWhen(new Date());
        return punishmentDocument;
    }

    private static void broadcastToStaff(PunishmentDocument document) {
        String broadcast = BroadcastUtils.generatePunishmentMessage(document);
        Bukkit.broadcast(broadcast, "dynamicdb.punishments.see." + document.getType().toString().toLowerCase());
        Bukkit.getConsoleSender().sendMessage(broadcast);
    }
}
