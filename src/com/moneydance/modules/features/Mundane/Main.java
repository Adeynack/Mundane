package com.moneydance.modules.features.Mundane;

import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.FeatureModuleContext;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;

/**
 * Pluggable module used to give users access to a Account List
 * interface to Moneydance.
 */

@SuppressWarnings("WeakerAccess")
public class Main extends FeatureModule {

    private AccountListWindow accountListWindow = null;

    public void init() {
        FeatureModuleContext context = getContext();
        try {
            context.registerFeature(this, "showconsole", getIcon(), getName());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public String getName() {
        return "Mundane";
    }

    public void cleanup() {
        closeConsole();
    }

    private Image getIcon() {
        try {
            ClassLoader cl = getClass().getClassLoader();
            java.io.InputStream in = cl.getResourceAsStream("/com/moneydance/modules/features/myextension/icon.gif");
            if (in != null) {
                ByteArrayOutputStream bout = new ByteArrayOutputStream(1000);
                byte buf[] = new byte[256];
                int n;
                while ((n = in.read(buf, 0, buf.length)) >= 0)
                    bout.write(buf, 0, n);
                return Toolkit.getDefaultToolkit().createImage(bout.toByteArray());
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Process an invocation of this module with the given URI
     */
    public void invoke(String uri) {
//        String command = uri;
//        String parameters = "";
//        int theIdx = uri.indexOf('?');
//        if (theIdx >= 0) {
//            command = uri.substring(0, theIdx);
//            parameters = uri.substring(theIdx + 1);
//        } else {
//            theIdx = uri.indexOf(':');
//            if (theIdx >= 0) {
//                command = uri.substring(0, theIdx);
//            }
//        }

        switch (uri) {
            case "showconsole":
                showConsole();
                break;
            case "foo":
                JOptionPane.showMessageDialog(null, "foo");
                break;
            case "bar":
                JOptionPane.showMessageDialog(null, "bar");
                break;
        }

    }

    private synchronized void showConsole() {
        if (accountListWindow == null) {
            accountListWindow = new AccountListWindow(this);
            accountListWindow.setVisible(true);
        } else {
            accountListWindow.setVisible(true);
            accountListWindow.toFront();
            accountListWindow.requestFocus();
        }
    }

    FeatureModuleContext getUnprotectedContext() {
        return getContext();
    }

    synchronized void closeConsole() {
        if (accountListWindow != null) {
            accountListWindow.goAway();
            accountListWindow = null;
            System.gc();
        }
    }
}
