package me.vivace.game.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {
    private Commands commands = new Commands();
    List<String> arguments = new ArrayList<String>();

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        arguments.clear();

        if (cmd.getName().equalsIgnoreCase(commands.cmd1)) {
            if (args.length == 1) {
                arguments.add("title");
                arguments.add("chat");
            } else if (args.length == 2) {
                arguments.add("0");
                arguments.add("0 0");
                arguments.add("0 0 0");
                arguments.add("#000000");
            } else { //(args.length > 2)
                if (args[1].contains("#")) {
                    if (args.length == 3) {
                        arguments.add("#ffffff");
                    } else if (args.length == 4) {
                        arguments.add("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
                        arguments.add("마아아아아비이이이이익어어어어엉더어어어엉뽀오오오올로오오옹");
                    } else if (args.length == 5) {
                        arguments.add("<int>");
                        arguments.add("3");
                    }
                } else {
                    if (args.length == 5) {
                        arguments.add("255");
                        arguments.add("255 255");
                        arguments.add("255 255 255");
                    } else if (args.length == 8) {
                        arguments.add("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
                        arguments.add("마아아아아비이이이이익어어어어엉더어어어엉뽀오오오올로오오옹");
                    } else if (args.length == 9) {
                        arguments.add("<int>");
                    }
                }

            }

            return arguments;
        } else if (cmd.getName().equalsIgnoreCase(commands.cmd3)) {
            if (args.length == 1) {
                arguments.add("add");
                arguments.add("attr");
                arguments.add("pi");
            } else if (args[0] == "add") {
                arguments.add("100"); arguments.add("50");
            }

            return arguments;
        }

        return null;
    }
}
