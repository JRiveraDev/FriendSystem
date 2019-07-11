package org.runnerer.friends;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.runnerer.friends.commands.FriendCommand;
import org.runnerer.friends.database.MySQL;

public class Friends extends JavaPlugin
{

    @Override
    public void onEnable()
    {
        registerEngine(new FriendManager());
        getCommand("friend").setExecutor(new FriendCommand());

        new MySQL();
        try
        {
            MySQL.Instance.openConnection();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void registerEngine(Listener listener)
    {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

}
