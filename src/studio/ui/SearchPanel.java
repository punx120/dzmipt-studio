package studio.ui;

import javax.swing.*;
import java.awt.*;

public class SearchPanel extends JPanel {

    private JCheckBox chWholeWord;
    private JCheckBox chRegexp;
    private JCheckBox chCaseSensitive;
    private JCheckBox chWrapSearch;
    private JTextField txtFind;
    private JTextField txtReplace;

    public SearchPanel() {
        chWholeWord = new JCheckBox("W");
        chRegexp = new JCheckBox(".*");
        chCaseSensitive = new JCheckBox("Aa");
        chWrapSearch = new JCheckBox("∫");

        txtFind = new JTextField();
        txtReplace = new JTextField();

        JLabel lblFind = new JLabel("Find: ");
        JLabel lblReplace = new JLabel("Replace: " );

        Action findAction = UserAction.create("Find", e -> find(true));
        Action findBackAction = UserAction.create("Find Back", e -> find(false));
        Action markAllAction = UserAction.create("Mark All", e -> markAll());
        Action replaceAction = UserAction.create("Replace", e -> replace());
        Action replaceAllAction = UserAction.create("Replace All", e -> replaceAll());
        Action closeAction = UserAction.create("Close", e -> close());

        JButton btnFind = new JButton(findAction);
        JButton btnFindBack = new JButton(findBackAction);
        JButton btnMarkAll = new JButton(markAllAction);
        JButton btnReplace = new JButton(replaceAction);
        JButton btnReplaceAll = new JButton(replaceAllAction);
        JButton btnClose = new JButton(closeAction);

        JPanel find = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel replace = new JPanel(new FlowLayout(FlowLayout.LEFT));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                                .addGroup(
                                    layout.createParallelGroup().addComponent(lblFind).addComponent(lblReplace)
                                ).addGroup(
                                    layout.createParallelGroup().addComponent(txtFind).addComponent(txtReplace)
                                ).addGroup(
                                        layout.createParallelGroup()
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                            .addComponent(chWholeWord)
                                                            .addComponent(chRegexp)
                                                            .addComponent(chCaseSensitive)
                                                            .addComponent(chWrapSearch)
                                                            .addComponent(btnFind)
                                                            .addComponent(btnFindBack)
                                                            .addComponent(btnMarkAll)
                                                            .addComponent(btnClose)
                                                ).addGroup(
                                                        layout.createSequentialGroup()
                                                                .addComponent(btnReplace)
                                                                .addComponent(btnReplaceAll)
                                                )
                                )
                );


        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblFind)
                                        .addComponent(txtFind)
                                        .addComponent(chWholeWord)
                                        .addComponent(chRegexp)
                                        .addComponent(chCaseSensitive)
                                        .addComponent(chWrapSearch)
                                        .addComponent(btnFind)
                                        .addComponent(btnFindBack)
                                        .addComponent(btnMarkAll)
                                        .addComponent(btnClose)
                        ).addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblReplace)
                                        .addComponent(txtReplace)
                                        .addComponent(btnReplace)
                                        .addComponent(btnReplaceAll)
                        )
        );


        lblReplace.setVisible(false);
        txtReplace.setVisible(false);
        btnReplace.setVisible(false);
        btnReplaceAll.setVisible(false);
    }

    private void find(boolean forward) {

    }

    private void markAll() {

    }

    private void replace()  {

    }

    private void replaceAll() {

    }

    private void close() {

    }
}
