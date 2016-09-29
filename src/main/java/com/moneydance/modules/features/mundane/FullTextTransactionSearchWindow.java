package com.moneydance.modules.features.mundane;

import com.infinitekind.moneydance.model.ParentTxn;
import com.infinitekind.moneydance.model.SplitTxn;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.awt.AwtUtil;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.MultiSplitLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class FullTextTransactionSearchWindow extends JFrame {

    private final FeatureModuleContext context;
    private final JTextField txtSearchInput;
    private final JTextArea txtResults = new JTextArea();

    private final Action actionSearch = new AbstractAction("Search") {
        @Override
        public void actionPerformed(ActionEvent e) {
            launchSearch();
        }
    };

    private final Action actionClose = new AbstractAction("Close") {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    };

    FullTextTransactionSearchWindow(FeatureModuleContext extensionContext) {
        super("Full Text Transaction Search");
        context = extensionContext;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        final JPanel root = new JPanel(new MigLayout(
                new LC(),
                new AC().grow().fill().gap()
                        .shrink(),
                new AC().shrink().gap()
                        .grow().fill().gap()
                        .shrink()
        ));

        txtSearchInput = new JTextField();
        txtSearchInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        actionSearch.actionPerformed(new ActionEvent(txtResults, 0, ""));
                        break;
                    default:
                        super.keyReleased(e);
                }
            }
        });
        root.add(txtSearchInput, new CC());

        final JButton btnSearch = new JButton(actionSearch);
        btnSearch.setMnemonic('s');
        root.add(btnSearch, new CC().wrap());

        txtResults.setFont(new Font("Courier", NORMAL, 14));

        final JScrollPane scrollTxtResult = new JScrollPane(txtResults);
        root.add(scrollTxtResult, new CC().spanX().wrap());

        final JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final JButton btnClose = new JButton(actionClose);
        pnlButtons.add(btnClose);

        root.add(pnlButtons, new CC().spanX());

        setSize(1000, 400);
        getContentPane().add(root);
        AwtUtil.centerWindow(this);
    }

    private void launchSearch() {
//        JOptionPane.showMessageDialog(
//                this,
//                String.format("Search launched with query \"%s\".", txtSearchInput.getText()),
//                "Search launched",
//                JOptionPane.INFORMATION_MESSAGE);
        final String query = txtSearchInput.getText();
        txtResults.setText(String.format("Starting search with query \"%s\".", query));

        List<String> results = StreamSupport
                .stream(context.getCurrentAccountBook().getTransactionSet().spliterator(), false)
                .filter(t -> t instanceof ParentTxn)
                .map(t -> (ParentTxn) t)
                .filter(t -> t.getDescription().contains(query) ||
                        t.getAttachmentKeys().stream().anyMatch(ak -> ak.contains(query)) ||
                        t.hasKeywordSubstring(query, false)
                )
                .map(t -> {
                    final String intDateToStr = Integer.toString(t.getDateInt());
                    final String date = String.format("%s-%s-%s", intDateToStr.substring(0, 4), intDateToStr.substring(4, 6), intDateToStr.substring(6, 8));
                    final String desc = t.getDescription();
                    final String source = t.getAccount().getFullAccountName();
                    String destinations;
                    if (t.getSplitCount() > 1) {
                        CharSequence[] splits = new CharSequence[t.getSplitCount()];
                        for (int i = 0; i < t.getSplitCount(); ++i) {
                            final SplitTxn split = t.getSplit(i);
                            splits[i] = String.format("%s to %s", split.getAmount() / 100.0, split.getAccount().getFullAccountName());
                        }
                        destinations = String.format("[ %s ]", String.join(", ", splits));
                    } else {
                        final SplitTxn split = t.getSplit(0);
                        destinations = String.format("%s to %s", split.getAmount() / 100.0, split.getAccount().getFullAccountName());
                    }
                    return String.format("%s: { %s } %s -> %s", date, desc, source, destinations);
                })
                .collect(Collectors.toList());

        txtResults.append("\n");
        txtResults.append(String.join("\n", results));
    }


}
