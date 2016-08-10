package com.moneydance.modules.features.Mundane;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.AccountBook;
import com.moneydance.awt.AwtUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

/**
 * Window used for Account List interface
 */

@SuppressWarnings("FieldCanBeLocal")
class AccountListWindow extends JFrame implements ActionListener {

    private Main extension;
    private JTree accountTree;
    private JButton refreshButton;
    private JButton closeButton;
    private JTextField inputArea;

    AccountListWindow(Main extension) {
        super("Account List Console");
        this.extension = extension;

        accountTree = new JTree();
        fillAccountTree();

        inputArea = new JTextField();
        inputArea.setEditable(true);
        refreshButton = new JButton("Refresh");
        closeButton = new JButton("Close");

        JPanel p = new JPanel(new MigLayout(
                "",
                "grow",
                "[grow][]"));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(new JScrollPane(accountTree), "span, grow, wrap");
        p.add(refreshButton);
        p.add(closeButton, "skip 2, align right");
        getContentPane().add(p);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        enableEvents(WindowEvent.WINDOW_CLOSING);
        closeButton.addActionListener(this);
        refreshButton.addActionListener(this);

        setSize(500, 400);
        AwtUtil.centerWindow(this);
    }

    private void fillAccountTree() {
        AccountBook rootAccount = extension.getUnprotectedContext().getCurrentAccountBook();
        if (rootAccount == null) return;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("JTree");
        addSubAccounts(rootAccount.getRootAccount(), root);
        accountTree.setModel(new DefaultTreeModel(root));
    }

    private static void addSubAccounts(Account parentAcct, DefaultMutableTreeNode node) {
        parentAcct.getSubAccounts().forEach(acct -> {
            DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(acct.getAccountName());
            addSubAccounts(acct, subNode);
            node.add(subNode);
        });
    }

    public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();
        if (src == closeButton) {
            extension.closeConsole();
        }
        if (src == refreshButton) {
            fillAccountTree();
        }
    }

    public final void processEvent(AWTEvent evt) {
        if (evt.getID() == WindowEvent.WINDOW_CLOSING) {
            extension.closeConsole();
            return;
        }
//        if (evt.getID() == WindowEvent.WINDOW_OPENED) {
//        }
        super.processEvent(evt);
    }

    void goAway() {
        setVisible(false);
        dispose();
    }

}
