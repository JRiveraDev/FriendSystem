package org.runnerer.friends.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runnerer.friends.FriendManager;
import org.runnerer.friends.utils.C;
import org.runnerer.friends.utils.F;

import java.sql.SQLException;

public class FriendCommand implements CommandExecutor
{

    public void Help(Player p)
    {
        p.sendMessage(ChatColor.BLUE + "Friend Manager>" + ChatColor.GRAY + " Valid sub-commands for " + C.Gray + "/friend" + C.Gray + ":");
        p.sendMessage(C.Gray + "/friend " + C.Gray + "<player> " + C.Gray + "Default. Sends a friend request to a player.");
        p.sendMessage(C.Gray + "/friend " + C.Gray + "accept <player> " + C.Gray + "Default. Accepts a friend request.");
        p.sendMessage(C.Gray + "/friend " + C.Gray + "delete <player> " + C.Gray + "Default. Deletes a friend.");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String string, String[] args)
    {
        if (!(commandSender instanceof Player)) return false;

        Player caller = (Player) commandSender;

        if (command.getName().equalsIgnoreCase("f") || command.getName().equalsIgnoreCase("friend"))
        {
            if (args == null)
            {
                Help(caller);
                return true;
            }

            if (args.length >= 1)
            {
                if (args[0].equalsIgnoreCase("accept"))
                {
                    if (args.length == 2)
                    {
                        Player p = Bukkit.getPlayer(args[1]);

                        if (p == null)
                        {
                            caller.sendMessage(F
                                    .main("Player Search", "No players found!"));
                            return true;
                        }

                        if (p == caller)
                        {
                            caller.sendMessage(F.main("Friend Manager", "Why would you want to accept yourself?"));
                            return true;
                        }

                        if (FriendManager.isFriends(p.getUniqueId()
                                .toString(), caller.getUniqueId()
                                .toString()))
                        {
                            caller.sendMessage(F
                                    .main("Friend Manager", "You are already friends with " + C.Yellow + args[1] + C.Gray + "."));
                            return true;
                        }

                        if (!FriendManager.isWaiting(p.getUniqueId()
                                .toString(), caller.getUniqueId()
                                .toString()) && !FriendManager
                                .isWaiting(caller.getUniqueId()
                                        .toString(), p.getUniqueId()
                                        .toString()))
                        {
                            caller.sendMessage(F
                                    .main("Friend Manager", C.Yellow + args[1] + C.Gray + " hasn't sent you a friend request."));
                            return true;
                        }

                        caller.sendMessage(F
                                .main("Friend Manager", "You accepted " + C.Yellow + p
                                        .getName() + C.Gray + "'s friend request."));
                        p.sendMessage(F.main("Friend Manager", C.Yellow + caller
                                .getName() + C.Gray + " accepted your friend request."));
                        FriendManager.acceptRequestPlayer(p.getUniqueId()
                                .toString(), caller.getUniqueId().toString());
                        return true;
                    }
                    Help(caller);
                } else if (args[0].equalsIgnoreCase("remove") || args[0]
                        .equalsIgnoreCase("delete"))
                {
                    if (args.length == 2)
                    {
                        Player p = Bukkit.getPlayer(args[1]);

                        if (p == null)
                        {
                            caller.sendMessage(F
                                    .main("Player Search", "No players found!"));
                            return true;
                        }

                        if (p.getName() == caller.getName())
                        {
                            caller.sendMessage(F
                                    .main("Friend Manager", "You can not delete yourself as a friend."));
                            return true;
                        }
                        if ((!FriendManager.isFriends(p.getUniqueId()
                                .toString(), caller.getUniqueId()
                                .toString()) && !FriendManager
                                .isFriends(caller.getUniqueId()
                                        .toString(), p.getUniqueId()
                                        .toString())) || (!FriendManager
                                .isFriends(p
                                        .getUniqueId()
                                        .toString(), caller
                                        .getUniqueId()
                                        .toString()) && !FriendManager
                                .isFriends(caller
                                        .getUniqueId()
                                        .toString(), p
                                        .getUniqueId()
                                        .toString())))
                        {
                            caller.sendMessage(F
                                    .main("Friend Manager", "You are not friends with " + C.Yellow + args[1] + C.Gray + "."));
                            return true;
                        }

                        if ((FriendManager.isWaiting(p.getUniqueId()
                                .toString(), caller.getUniqueId()
                                .toString()) && FriendManager
                                .isWaiting(caller.getUniqueId()
                                        .toString(), p.getUniqueId()
                                        .toString())) || (FriendManager
                                .isWaiting(p
                                        .getUniqueId()
                                        .toString(), caller
                                        .getUniqueId()
                                        .toString()) && FriendManager
                                .isWaiting(caller
                                        .getUniqueId()
                                        .toString(), p
                                        .getUniqueId()
                                        .toString())))
                        {
                            caller.sendMessage(F
                                    .main("Friend Manager", C.Yellow + p
                                            .getName() + C.Gray + " has sent you a friend request and is not your friend. If you want to reject the friend request, " + C.Green + "/friend reject " + p
                                            .getName() + C.Gray + "."));
                            return true;
                        }

                        caller.sendMessage(F
                                .main("Friend Manager", "You removed " + C.Yellow + p
                                        .getName() + C.Gray + " as a friend."));
                        try
                        {
                            FriendManager.unfriendPlayer(caller.getUniqueId()
                                    .toString(), p.getUniqueId().toString());

                        }
                        catch (SQLException e)
                        {
                            e.printStackTrace();
                        }

                        try
                        {
                            FriendManager.unfriendPlayer(p.getUniqueId()
                                    .toString(), caller.getUniqueId().toString());

                        }
                        catch (SQLException e)
                        {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    Help(caller);
                } else
                {
                    Player p = Bukkit.getPlayer(args[0]);

                    if (p == null)
                    {
                        caller.sendMessage(F
                                .main("Player Search", "No players found!"));
                        return true;
                    }

                    if (caller.getName() == args[0])
                    {
                        caller.sendMessage(F.main("Friend Manager", "You can't add yourself. Are you friends with yourself?"));
                    }

                    if (FriendManager.isFriends(p.getUniqueId().toString(), caller
                            .getUniqueId().toString()) || FriendManager
                            .isFriends(caller.getUniqueId().toString(), p
                                    .getUniqueId().toString()))
                    {
                        caller.sendMessage(F
                                .main("Friend Manager", "You are already friends with " + C.Yellow + args[0] + C.Gray + "."));
                        return true;
                    }


                    if (FriendManager.isWaiting(p.getUniqueId().toString(), caller
                            .getUniqueId().toString()) || FriendManager
                            .isWaiting(caller.getUniqueId().toString(), p
                                    .getUniqueId().toString()))
                    {
                        caller.sendMessage(F
                                .main("Friend Manager", "You have already sent a friend request to " + C.Yellow + args[0] + C.Gray + "."));
                        return true;
                    }

                    FriendManager.friendPlayer(caller.getUniqueId().toString(), p
                            .getUniqueId().toString());
                    p.sendMessage(F.main("Friend Manager", C.Yellow + caller
                            .getName() + C.Gray + " sent you a friend request. To accept, do " + C.Green + "/friend accept " + caller
                            .getName() + C.Gray + "."));
                    caller.sendMessage(F
                            .main("Friend Manager", "You sent " + C.Yellow + p
                                    .getName() + C.Gray + " a friend request."));
                    return true;
                }
            }

            Help(caller);
        }
        return true;
    }
}