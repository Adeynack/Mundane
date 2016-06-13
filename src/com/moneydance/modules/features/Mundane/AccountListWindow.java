package com.moneydance.modules.features.Mundane;

import com.infinitekind.moneydance.model.Account;
import com.infinitekind.moneydance.model.AccountBook;
import com.moneydance.awt.AwtUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    private JTextArea accountListArea;
    private JButton clearButton;
    private JButton closeButton;
    private JTextField inputArea;

    AccountListWindow(Main extension) {
        super("Account List Console");
        this.extension = extension;

        accountListArea = new JTextArea();

        AccountBook book = extension.getUnprotectedContext().getCurrentAccountBook();
        StringBuffer acctStr = new StringBuffer();
        if (book != null) {
            addSubAccounts(book.getRootAccount(), acctStr);
        }
        accountListArea.setEditable(false);
        accountListArea.setText(acctStr.toString());
        inputArea = new JTextField();
        inputArea.setEditable(true);
        clearButton = new JButton("Clear");
        closeButton = new JButton("Close");

        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(new JScrollPane(accountListArea), AwtUtil.getConstraints(0, 0, 1, 1, 4, 1, true, true));
        p.add(Box.createVerticalStrut(8), AwtUtil.getConstraints(0, 2, 0, 0, 1, 1, false, false));
        p.add(clearButton, AwtUtil.getConstraints(0, 3, 1, 0, 1, 1, false, true));
        p.add(closeButton, AwtUtil.getConstraints(1, 3, 1, 0, 1, 1, false, true));
        getContentPane().add(p);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        enableEvents(WindowEvent.WINDOW_CLOSING);
        closeButton.addActionListener(this);
        clearButton.addActionListener(this);

        setSize(500, 400);
        AwtUtil.centerWindow(this);
    }

    private static void addSubAccounts(Account parentAcct, StringBuffer acctStr) {
        int sz = parentAcct.getSubAccountCount();
        for (int i = 0; i < sz; i++) {
            Account acct = parentAcct.getSubAccount(i);
            acctStr.append(acct.getFullAccountName());
            acctStr.append("\n");
            addSubAccounts(acct, acctStr);
        }
    }

    public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();
        if (src == closeButton) {
            extension.closeConsole();
        }
        if (src == clearButton) {
            accountListArea.setText("");
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
