package com.moneydance.modules.features.mundane;

import com.infinitekind.moneydance.model.ParentTxn;
import com.infinitekind.moneydance.model.SplitTxn;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.awt.AwtUtil;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.stream.StreamSupport;

class FullTextTransactionSearchWindow extends JFrame {

    private final FeatureModuleContext context;
    private final JTextField txtSearchInput;

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
    private final JScrollPane scrollResults;

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
                        actionSearch.actionPerformed(new ActionEvent(txtSearchInput, 0, ""));
                        break;
                    default:
                        super.keyPressed(e);
                }
            }
        });
        root.add(txtSearchInput, new CC());

        final JButton btnSearch = new JButton(actionSearch);
        btnSearch.setMnemonic('s');
        root.add(btnSearch, new CC().wrap());

        scrollResults = new JScrollPane(new JPanel());
        root.add(scrollResults, new CC().spanX().wrap());

        final JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final JButton btnClose = new JButton(actionClose);
        pnlButtons.add(btnClose);

        root.add(pnlButtons, new CC().spanX());

        setSize(1000, 400);
        getContentPane().add(root);
        AwtUtil.centerWindow(this);
    }

    private static final Color RESULT_COLOR_DATE = new Color(51, 98, 175);
    private static final Color RESULT_COLOR_DESCRIPTION = new Color(139, 179, 244);
    private static final Color RESULT_COLOR_SOURCE = new Color(192, 209, 237);
    private static final Color RESULT_COLOR_DESTINATION = new Color(5, 44, 107);

    private void launchSearch() {
//        JOptionPane.showMessageDialog(
//                this,
//                String.format("Search launched with query \"%s\".", txtSearchInput.getText()),
//                "Search launched",
//                JOptionPane.INFORMATION_MESSAGE);
        final String query = txtSearchInput.getText();
        final JPanel resultPane = new JPanel(new MigLayout(
                new LC().gridGap("4px", "2px"),
                new AC(),
                new AC()
        ));
        StreamSupport.stream(context.getCurrentAccountBook().getTransactionSet().spliterator(), false)
                .filter(t -> t instanceof ParentTxn)
                .map(t -> (ParentTxn) t)
                .filter(t -> t.getDescription().contains(query) ||
                        t.getAttachmentKeys().stream().anyMatch(ak -> ak.contains(query)) ||
                        t.hasKeywordSubstring(query, false)
                )
                .forEach(t -> {

                    final String intDateToStr = Integer.toString(t.getDateInt());
                    final String date = String.format("%s-%s-%s", intDateToStr.substring(0, 4), intDateToStr.substring(4, 6), intDateToStr.substring(6, 8));
                    final JLabel dateLabel = new JLabel(date);
                    dateLabel.setOpaque(true);
                    dateLabel.setBackground(RESULT_COLOR_DATE);
                    dateLabel.setForeground(Color.WHITE);
                    resultPane.add(dateLabel, new CC().growX());

                    final JLabel descriptionLabel = new JLabel(t.getDescription());
                    descriptionLabel.setOpaque(true);
                    descriptionLabel.setBackground(RESULT_COLOR_DESCRIPTION);
                    descriptionLabel.setForeground(Color.BLACK);
                    resultPane.add(descriptionLabel, new CC().growX());

                    final JLabel sourceLabel = new JLabel(t.getAccount().getFullAccountName());
                    sourceLabel.setOpaque(true);
                    sourceLabel.setBackground(RESULT_COLOR_SOURCE);
                    sourceLabel.setForeground(Color.BLACK);
                    resultPane.add(sourceLabel, new CC().growX());

                    final JPanel destinations = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    for (int splitIndex = 0; splitIndex < t.getSplitCount(); ++splitIndex) {
                        final SplitTxn split = t.getSplit(splitIndex);
                        final String splitDesc = String.format("%s to %s", split.getAmount() / 100.0, split.getAccount().getFullAccountName());
                        final JLabel splitLabel = new JLabel(splitDesc);
                        splitLabel.setOpaque(true);
                        splitLabel.setBackground(RESULT_COLOR_DESTINATION);
                        splitLabel.setForeground(Color.WHITE);
                        destinations.add(splitLabel);
                    }
                    resultPane.add(destinations, new CC().growX().wrap());
                });

        scrollResults.setViewportView(resultPane);
    }

}
