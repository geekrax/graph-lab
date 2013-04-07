// GraphLab Project: http://graphlab.sharif.edu
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/
package graphlab.plugins.reports;

import graphlab.platform.Application;
import graphlab.platform.core.BlackBoard;
import graphlab.platform.core.Listener;
import graphlab.platform.core.exception.ExceptionHandler;
import graphlab.platform.extension.ExtensionLoader;
import graphlab.platform.plugin.PluginInterface;
import graphlab.plugins.reports.extension.GraphReportExtension;
import graphlab.plugins.reports.extension.GraphReportExtensionHandler;
import graphlab.plugins.reports.ui.ReportsUI;
import graphlab.ui.AttributeSetView;
import graphlab.ui.UI;

/**
 * @author azin
 */
public class Init implements PluginInterface {
    static {
        ExtensionLoader.registerExtensionHandler(new GraphReportExtensionHandler());
    }

    public void init(BlackBoard blackboard) {
        UI ui = (UI) blackboard.getData(UI.name);
        try {
            ui.addXML("/graphlab/plugins/reports/config.xml", getClass());
        } catch (Exception e) {
            ExceptionHandler.catchException(e);
            System.out.println("xml file was not found , or IO error");
        }
        blackboard.addListener(Application.POST_INIT_EVENT, new Listener() {
            public void keyChanged(String key, Object value) {
                postInit();
            }
        });

    }

    private void postInit() {
        ReportsUI rui = ReportsUI.self;
        rui.initTable();
    }
}
