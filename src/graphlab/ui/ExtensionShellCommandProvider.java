// GraphLab Project: http://graphlab.sharif.edu
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/

package graphlab.ui;

import graphlab.platform.extension.Extension;
import graphlab.platform.lang.CommandAttitude;

import java.util.HashMap;
import java.util.Vector;
/**
 * a class for holding added commands
 */
public class ExtensionShellCommandProvider {
    public AbstractExtensionAction ths;
    public Extension trgClass;
    public CommandAttitude comati;
    public String abrv;
    public String command;
    public String desc;
    public String help;
    public static HashMap<String, ExtensionShellCommandProvider> commandsDict = new HashMap<String, ExtensionShellCommandProvider>();
    public static Vector<ExtensionShellCommandProvider> commands = new Vector<ExtensionShellCommandProvider>();
    public String name;

    public ExtensionShellCommandProvider(AbstractExtensionAction ths, Extension trgClass, String name, String abrv, String command, String desc, String help) {
        this.ths = ths;
        this.trgClass = trgClass;
        this.name = name;
        this.abrv = abrv;
        this.command = command;
        this.desc = desc;
        this.help = help;
    }

    /**
     *
     * @param ths
     * @param trg
     * @param name
     * @param abrv
     * @param command
     * @param desc
     * @param help
     */
    public static void addCommand(AbstractExtensionAction ths, Extension trg, String name, String abrv, String command, String desc, String help) {
        ExtensionShellCommandProvider c = new ExtensionShellCommandProvider(ths, trg, name, abrv, command, desc, help);
        commands.add(c);
        commandsDict.put(name, c);
    }
}
