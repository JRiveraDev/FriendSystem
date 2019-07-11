package org.runnerer.friends;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.runnerer.friends.database.MySQL;
import org.runnerer.friends.utils.C;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class FriendManager implements Listener
{

    private static String ADD_FRIEND_ENTRY = "INSERT INTO accountFriend (uuidFriender, uuidFriended, favorite, status) VALUES(?,?,?,?);";
    private static String RETRIEVE_FRIENDS_ENTRIES = "SELECT uuidFriender, uuidFriended FROM accountFriend;";

    public static HashMap<String, String> retrieveFriends() throws SQLException
    {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        HashMap<String, String> newsEntries = new HashMap<String, String>();

        preparedStatement = MySQL.getConnection()
                .prepareStatement(RETRIEVE_FRIENDS_ENTRIES);

        resultSet = preparedStatement.executeQuery();

        while (resultSet.next())
        {
            newsEntries.put(resultSet.getString(2), resultSet.getString(1));
        }

        return newsEntries;
    }

    public static boolean friendPlayer(String ignorer, String ignored)
    {
        int result = 0;
        PreparedStatement preparedStatement = null;

        try
        {
            preparedStatement = MySQL.getConnection()
                    .prepareStatement(ADD_FRIEND_ENTRY);
            preparedStatement.setString(1, ignorer);
            preparedStatement.setString(2, ignored);
            preparedStatement.setBoolean(3, false);
            preparedStatement.setString(4, "waiting");
            result = preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result != 0;
    }

    // DO NOT USE ME.
    public static boolean favoritePlayer(String ignorer, String ignored)
    {
        int result = 0;
        PreparedStatement preparedStatement = null;

        try
        {
            preparedStatement = MySQL.getConnection()
                    .prepareStatement("UPDATE accountFriend SET favorite='" + true + "' WHERE uuidFriender='" + ignorer + "';");
            preparedStatement.setBoolean(3, true);
            result = preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result != 0;
    }

    // DO NOT USE ME.
    public static boolean unfavoritePlayer(String ignorer, String ignored)
    {
        int result = 0;
        PreparedStatement preparedStatement = null;

        try
        {
            preparedStatement = MySQL.getConnection()
                    .prepareStatement("UPDATE accountFriend SET favorite='" + false + "' WHERE uuidFriender='" + ignorer + "';");
            preparedStatement.setBoolean(3, false);
            result = preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result != 0;
    }

    public static boolean unfriendPlayer(String ignorer, String ignored) throws SQLException
    {
        int result = 0;
        PreparedStatement preparedStatement = null;

        preparedStatement = MySQL.getConnection()
                .prepareStatement("DELETE FROM accountFriend WHERE uuidFriender = '" + ignorer + "' AND " + "uuidFriended = '" + ignored + "' ");

        result = preparedStatement.executeUpdate();

        return result != 0;
    }

    public static boolean isFriends(String player_name, String friender)
    {
        try
        {
            ResultSet res = MySQL
                    .querySQL("SELECT * FROM accountFriend WHERE uuidFriender = '" + friender + "' AND " + "uuidFriended = '" + player_name + "' AND " + "status = '" + "accepted" + "';");
            ResultSet res2 = MySQL
                    .querySQL("SELECT * FROM accountFriend WHERE uuidFriender = '" + player_name + "' AND " + "uuidFriended = '" + friender + "' AND " + "status = '" + "accepted" + "';");

            if (res.next())
            {
                if (res2.next())
                {
                    return true;
                }
                return true;
            }

            if (res2.next())
            {
                return true;
            }
            return false;
        }
        catch (SQLException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isWaiting(String player_name, String friender)
    {
        try
        {
            ResultSet res = MySQL
                    .querySQL("SELECT * FROM accountFriend WHERE uuidFriender = '" + friender + "' AND " + "uuidFriended = '" + player_name + "';");

            while (res.next())
            {
                String getIgnored = res.getString(2);

                if (player_name.equalsIgnoreCase(getIgnored))
                {
                    if (res.getString(4).equalsIgnoreCase("waiting"))
                    {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        }
        catch (SQLException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean acceptRequestPlayer(String ignorer, String ignored)
    {
        int result = 0;
        PreparedStatement preparedStatement = null;

        try
        {
            preparedStatement = MySQL.getConnection()
                    .prepareStatement("UPDATE accountFriend SET status='" + "accepted" + "' WHERE uuidFriender='" + ignorer + "';");

            result = preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result != 0;
    }

    @EventHandler
    public void FriendAlert(PlayerJoinEvent event)
    {
        for (Player pl : Bukkit.getOnlinePlayers())
        {
            if (isFriends(event.getPlayer().getUniqueId().toString(), pl
                    .getUniqueId().toString()))
            {
                pl.sendMessage(C.Blue + "Friend Join>" + C.Gray + " Your friend " + event
                        .getPlayer().getName() + " has joined your server.");
                return;
            }
            if (isFriends(pl.getUniqueId().toString(), event.getPlayer()
                    .getUniqueId().toString()))
            {
                pl.sendMessage(C.Blue + "Friend Join>" + C.Gray + " Your friend " + event
                        .getPlayer().getName() + " has joined your server.");
                return;
            }
        }
    }
}
