// GraphLab Project: http://graphlab.sharif.edu
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/

package graphlab.ui;

import graphlab.platform.core.AbstractAction;
import graphlab.platform.core.BlackBoard;
import graphlab.platform.extension.Extension;
import graphlab.platform.lang.CommandAttitude;
import graphlab.platform.parameter.Parameter;
import graphlab.platform.parameter.Parametrizable;
import graphlab.plugins.reports.extension.GraphReportExtension;
import graphlab.plugins.reports.extension.GraphReportExtensionAction;
import graphlab.ui.components.ExtensionConfigFrame;
import graphlab.ui.components.GFrame;
import graphlab.ui.components.gmenu.GMenuBar;
import graphlab.ui.components.gmenu.GMenuItem;
import graphlab.ui.xml.UIHandlerImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * the base class for creating extension handlers
 * the implementing class will have a menu assigned to it automatically the name of the menu will be
 * from the constructors parametr(sp) and the will also listen to UI.getUIEvent(sp.getName())
 *
 * @author azin azadi
 */
public abstract class AbstractExtensionAction<t extends Extension> extends AbstractAction implements ActionListener {
    //todo: Write how to create new extension types document.
    protected JMenu parentMenu;
    public GMenuItem menuItem;
    ExtensionConfigFrame ecf;
    /**
     * a button which will be added to the right of menu item
     */
    protected JButton extraButton;

    public t getTarget() {
        return target;
    }

    public t target;


    static HashMap<String, JMenu> reportSubMenus = new HashMap<String, JMenu>();

    public AbstractExtensionAction(BlackBoard bb, t sp) {
        super(bb);
        target = sp;
        String name = getMenuNamePrefix() + sp.getName();
        String actionId = name + sp.getDescription() + target.getClass().getName();
        listen4Event(UIUtils.getUIEventKey(actionId));
        if (!name.equals("")) {
            menuItem = createMenuItem(name, actionId, bb);
            parentMenu = getParentMenu();
            if (parentMenu.getText().equalsIgnoreCase("reports")) {
                GraphReportExtension<t> temp = (GraphReportExtension<t>) sp;
                if (temp.getCategory() != null) {
                    JMenu categoryMenu;
                    if (!reportSubMenus.containsKey(temp.getCategory())) {
                        categoryMenu = new JMenu(temp.getCategory());
                        reportSubMenus.put(temp.getCategory(), categoryMenu);
                    } else
                        categoryMenu = reportSubMenus.get(temp.getCategory());
                    GMenuBar.insert(parentMenu, categoryMenu, getMenuPlace());
                    GMenuBar.insert(categoryMenu, menuItem, getMenuPlace());
                } else
                    GMenuBar.insert(parentMenu, menuItem, getMenuPlace());
            } else if (parentMenu != null)
                GMenuBar.insert(parentMenu, menuItem, getMenuPlace());
        }
        createExtensionCommandsForCommandLine();
    }

    /**
     * It is a XML-Based UI concept
     *
     * @return the place which the extension menu will be inserted in its parents menu, the place is
     *         only a comparative value, it means that Menu Items with bigger place will be inserted after smaller place Menue Items
     */
    protected int getMenuPlace() {
        return target.getName().charAt(0) * 1000 + target.getName().charAt(1);
    }

    /**
     * if you want to create custom menues for your extension (i.e. want to add some extra components to it) you should
     * override this method.
     *
     * @param name
     * @param actionId
     * @param bb
     * @return
     * @see graphlab.plugins.algorithmanimator.extension.AlgorithmExtensionAction
     */
    protected final GMenuItem createMenuItem(String name, String actionId, BlackBoard bb) {
        int index = Math.max(name.indexOf(UIHandlerImpl.menueIndexChar), 0);
        String labelString = name.replace(UIHandlerImpl.menueIndexChar + "", "");
        if (isInsertExtraButtonToMenuItem()) {
            GMenuItem menuItem = new GMenuItem(labelString, actionId, bb, null, index) {
                public Dimension getPreferredSize() {
                    Dimension preferredSize = super.getPreferredSize();
                    Dimension bps = extraButton.getPreferredSize();
                    preferredSize.width += bps.getWidth() + 4;
                    preferredSize.height = (int) Math.max(preferredSize.height, bps.getHeight()) + 1;
                    return preferredSize;
                }

            };
            String imageURL = "/graphlab/plugins/main/resources/edit16_.gif";
            extraButton = new JButton(new ImageIcon(getClass().getResource(imageURL)));
            extraButton.setBorder(new EmptyBorder(0, 0, 0, 0));
            extraButton.addActionListener(this);
            extraButton.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    actionPerformed(null);
                }
            });
            menuItem.setLayout(new BorderLayout());
            menuItem.add(extraButton, BorderLayout.EAST);
            return menuItem;
        } else
            return new GMenuItem(name, actionId, bb);
    }

    /**
     * determines wheter to insert an extra button at the right side of mene item,
     * normally this is true whenever the target extension implements Parametrizable (have some parameters)
     * on this mode on pressing the button a dialog for setting the parameters will be shown, and then
     * it will be executed, if the user clicks the menu item directly the extension will be
     * performed without asking of parameters.
     * <p/>
     * if you want to override this method for your own ExtensionAction
     * NOTE that on pressing the button the actionPerformed will be called, so you can
     * do what you want to do by overriding that method.
     *
     * @return true to insert the extra button, false to no insert it.
     * @see Parametrizable
     * @see @Parameter
     * @see graphlab.plugins.algorithmanimator.extension.AlgorithmExtensionAction
     */
    protected boolean isInsertExtraButtonToMenuItem() {
        return false; //target instanceof Parametrizable; we dont want this button for now. just go with the simple interface
    }

    /**
     * inorder if you created a new type of extension and for that extension you want to do some thing different
     * when it is called on commandline (i.e. in normal state it uses some GUI functionalities and you want to avoid them)
     * you can override this method and do what you want, which will
     * be called whenever your extension called from commandline
     * for an example see GraphGeneratorExtensionAction
     *
     * @see graphlab.plugins.graphgenerator.core.extension.GraphGeneratorExtensionAction
     */
    public Object performExtensionInCommandLine() {
        performExtension();
        return null;
    }

    protected void createExtensionCommandsForCommandLine() {
        final AbstractExtensionAction<t> ths = this;
        final t trgClass = target;
        final CommandAttitude comati = trgClass.getClass().getAnnotation(CommandAttitude.class);

        String command = "";
        CommandAttitude c = comati;
        String cname;
        String abrv;
        String desc;
        if (c != null) {
            //            Shell.set_variable("_" + target.getClass().getSimpleName(), target);
            cname = c.name();
            abrv = c.abbreviation();
            desc = c.description();
            if (desc == null || desc.equals(""))
                desc = target.getDescription();
        } else {
            cname = target.getClass().getSimpleName();
            abrv = "";
            desc = target.getDescription();
        }
        command += cname + "(";
        String help = "";

        for (Field f : target.getClass().getFields()) {
            if (f.getAnnotation(Parameter.class) != null) {
                command += f.getType().getName()
                        + " "
                        + f.getName() + ",";
                help += "_" + target.getClass().getSimpleName()
                        + "."
                        + f.getName()
                        + " = "
                        + f.getName()
                        + ";\n";
            }
        }
        if (command.endsWith(","))
            command = command.substring(0, command.length() - 1);
        command += ")\n{";
        //        System.out.println(command);
        ExtensionShellCommandProvider.addCommand(ths, trgClass, cname, abrv, command, desc, help);

    }

    /**
     * to put a prefix before the name of your extension menu override this method
     */
    protected String getMenuNamePrefix() {
        return "";
    }

    /**
     * gets a menu for adding the sub menu, if returns null, no submenu will added!
     */
    protected JMenu getParentMenu() {
        GFrame f = UIUtils.getGFrame(blackboard);
        String mname = getParentMenuName();
        return f.getMenu().getUniqueMenu(mname, -1);
    }

    /**
     * first checks if o instanceof Parametrizable if so, shows an
     * editor for it's Parameters.
     *
     * @param o
     * @return true if o isn't an instance of Parametrizable or the user cancells the editing
     */
    public boolean testAndSetParameters(Object o) {
        if (o instanceof Parametrizable) {
            ParameterShower ps = new ParameterShower();

            return ps.show((Parametrizable) o);
        } else
            return true;
    }

    /**
     * removes all UI Components that are created for the extension (menues, ...)
     */
    public void removeCreatedUIComponents() {
        if (parentMenu != null && menuItem != null) {
            parentMenu.remove(menuItem);
            //if parent menu is empty remove it and so on.
            if (parentMenu.getMenuComponentCount() == 0) {
                parentMenu.getParent().remove(parentMenu);
            }
        }

    }

    /**
     * returns the menu name that the menuitem of this action is its child
     */
    public abstract String getParentMenuName();

    public final void performAction(String eventKey, Object value) {
        if (testAndSetParameters(target))
            performExtension();
    }

    /**
     * occurs whenever extraButton pressed
     * <p/>
     * =
     */
    public void actionPerformed(ActionEvent e) {
        parentMenu.setSelected(false);
        if (testAndSetParameters(target))
            performExtension();
    }

    public abstract void performExtension();

}
