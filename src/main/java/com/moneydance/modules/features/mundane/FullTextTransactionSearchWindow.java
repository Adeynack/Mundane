package com.moneydance.modules.features.mundane;

import com.infinitekind.moneydance.model.AbstractTxn;
import com.moneydance.apps.md.controller.FeatureModuleContext;
import com.moneydance.awt.AwtUtil;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
//                "debug",
                new LC(),
//                "[grow, fill]",
                new AC().grow().fill().gap()
                        .shrink(),
//                "[][grow, fill][]"
                new AC().shrink().gap()
                        .grow().fill().gap()
                        .shrink().gap()
                        .fill()
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

        final JScrollPane scrollTxtResult = new JScrollPane(txtResults);
        root.add(scrollTxtResult, new CC().spanX().wrap());

        final JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final JButton btnClose = new JButton(actionClose);
        pnlButtons.add(btnClose);

        root.add(pnlButtons, new CC().spanX().wrap());

        final JFXPanel fxPanel = new JFXPanel();
        root.add(fxPanel, new CC().spanX());
        Platform.runLater(() -> {
//            Group rootGrp = new Group();
//            Scene scene = new Scene(rootGrp, Color.ALICEBLUE);
//
//            Text text = new Text();
//            text.setX(40);
//            text.setY(100);
//            text.setFont(new Font(25));
//            text.setText("Welcome JavaFX!");
//            rootGrp.getChildren().add(text);
//
//            Button button = new Button("Clear search");
//            button.setOnAction(e ->
//                    SwingUtilities.invokeLater(() ->
//                            txtSearchInput.setText("")));
//            rootGrp.getChildren().add(button);

            try {
                final Parent parent = FXMLLoader.load(getClass().getResource("Foo.fxml"));
                final Scene scene = new Scene(parent);
                fxPanel.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        setSize(500, 400);
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
                .filter(t -> t.getDescription().contains(query))
                .map(AbstractTxn::getDescription)
                .collect(Collectors.toList());

        txtResults.append("\n");
        txtResults.append(String.join("\n", results));
    }


}
