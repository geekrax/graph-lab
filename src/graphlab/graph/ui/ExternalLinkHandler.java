// GraphLab Project: http://graphlab.sharif.edu
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/

package graphlab.graph.ui;

import graphlab.platform.core.BlackBoard;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Azin Azadi
 */
class ExternalLinkHandler implements HyperlinkHandler {

    public void handle(String url, BlackBoard b, URL currentURL) {
//        String path = currentURL.getProtocol() + "://" + currentURL.getPath();
//        if (!url.toLowerCase().startsWith("http:"))
//            url = path.substring(0, path.lastIndexOf("/")) + "/" + url;
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
