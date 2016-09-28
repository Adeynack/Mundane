package com.moneydance.modules.features.mundane;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinitekind.moneydance.model.AccountBook;
import com.moneydance.apps.md.controller.FeatureModule;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.modules.features.mundane.json.JsonAccount;
import com.moneydance.modules.features.mundane.utils.FrameSingleton;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayOutputStream;

/**
 * Pluggable module used to give users access to a Account List
 * interface to Moneydance.
 */
@SuppressWarnings("WeakerAccess")
public class Main extends FeatureModule {

    private AccountListWindow accountListWindow = null;
    private final FrameSingleton<FullTextTransactionSearchWindow> fullTextSearchWindow;
    private ObjectMapper mapper;

    private static final String INVOKE_SHOW_CONSOLE = "showConsole";
    private static final String INVOKE_FULL_TEXT_SEARCH = "fullTextSearch";
    private static final String INVOKE_ACCOUNTS_TO_JSON = "accountsToJson";

    public Main() {
        fullTextSearchWindow = new FrameSingleton<>(() -> new FullTextTransactionSearchWindow(getContext()));
    }

    public void init() {
        mapper = new ObjectMapper();
        FeatureModuleContext context = getContext();
        try {
            context.registerFeature(this, INVOKE_SHOW_CONSOLE, getIcon(), getName());
            context.registerFeature(this, INVOKE_FULL_TEXT_SEARCH, getIcon(), "Full Text Transaction Search");
            context.registerFeature(this, INVOKE_ACCOUNTS_TO_JSON, getIcon(), "Export account list to JSON in the clipboard");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public String getName() {
        return "Mundane";
    }

    public void cleanup() {
        closeAllWindows();
        fullTextSearchWindow.close();
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
        switch (uri) {
            case INVOKE_SHOW_CONSOLE:
                showConsole();
                break;
            case "CLOSE ALL":
                cleanup();
                break;
            case INVOKE_FULL_TEXT_SEARCH:
                fullTextSearchWindow.show();
                break;
            case INVOKE_ACCOUNTS_TO_JSON:
                exportToJson();
                break;
            default:
                throw new RuntimeException(String.format("Invoked with unrecognized URI \"%s\".", uri));
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

    synchronized void closeAllWindows() {
        if (accountListWindow != null) {
            accountListWindow.goAway();
            accountListWindow = null;
            System.gc();
        }
    }

    private void exportToJson() {
        AccountBook rootAccount = getUnprotectedContext().getCurrentAccountBook();
        if (rootAccount == null) return;
        try {
            JsonAccount[] accounts = JsonAccount.fromAccount(rootAccount.getRootAccount()).subAccounts;
            String json = mapper.writeValueAsString(accounts);
            StringSelection selection = new StringSelection(json);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        } catch (JsonProcessingException e) {
            JOptionPane.showMessageDialog(null, e.toString(), "Error exporting accounts to JSON", JOptionPane.ERROR_MESSAGE);
        }
    }
}
