package studio.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netbeans.editor.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;

public class DebugSyntaxHighlightingFrame extends JFrame {

    private static final Logger log = LogManager.getLogger();

    private JTextArea textArea;
    private JEditorPane editor;

    public DebugSyntaxHighlightingFrame() {
        super("Debug Syntax Highlighting");
        initComponents();
    }

    public void setEditor(JEditorPane pane) {
        this.editor = pane;
        refreshText();
        pane.getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        refreshText();
                    }
                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        refreshText();
                    }
                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        refreshText();
                    }
                }
        );
    }


    private void refreshText() {
        StringBuilder builder = new StringBuilder();
        try {
            TextUI textUI = editor.getUI();
            BaseKit baseKit = (BaseKit) textUI.getEditorKit(editor);
            Syntax syntax = baseKit.createSyntax(editor.getDocument());
            Document document = editor.getDocument();
            String text = document.getText(0, document.getLength());
            syntax.load(null, text.toCharArray(), 0, text.length(), true, text.length());

            TokenID token;
            while ( (token=syntax.nextToken()) != null) {
                builder.append(text, syntax.getTokenOffset(), syntax.getOffset())
                        .append("(")
                        .append(token.getName())
                        .append(")");
            }
        } catch (BadLocationException e) {
            log.error("Unexpected exception", e);
        }
        textArea.setText(builder.toString());
    }


    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane content = new JScrollPane(textArea);
        setContentPane(content);
        setPreferredSize(new Dimension(400, 800));
        pack();
        setVisible(true);
    }
}
