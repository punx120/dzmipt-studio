package studio.ui;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import studio.qeditor.RSToken;
import studio.qeditor.RSTokenMaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class EditorPane extends JPanel {

    private final RSyntaxTextArea textArea;
    private final MinSizeLabel lblRowCol;
    private final MinSizeLabel lblInsStatus;
    private final JLabel lblStatus;

    private final int yGap;
    private final int xGap;

    public EditorPane() {
        super(new BorderLayout());
        FontMetrics fm = getFontMetrics(UIManager.getFont("Label.font"));
        yGap = Math.round(0.1f * fm.getHeight());
        xGap = Math.round(0.25f * SwingUtilities.computeStringWidth(fm, "x"));

        textArea = new RSyntaxTextArea("");
        textArea.setLineWrap(true);
        textArea.setAnimateBracketMatching(true);
        textArea.setCodeFoldingEnabled(true);
        textArea.setCloseCurlyBraces(true);

        textArea.setSyntaxEditingStyle(RSTokenMaker.CONTENT_TYPE);
        textArea.setSyntaxScheme(RSToken.getDefaulSyntaxScheme());

        textArea.addCaretListener(e -> updateRowColStatus());
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateTextModeStatus();
            }
        });

        lblRowCol = new MinSizeLabel("");
        lblRowCol.setHorizontalAlignment(JLabel.CENTER);
        lblRowCol.setMinimumWidth("9999:9999");
        setBorder(lblRowCol);

        lblInsStatus = new MinSizeLabel("INS");
        lblInsStatus.setHorizontalAlignment(JLabel.CENTER);
        lblInsStatus.setMinimumWidth("INS", "OVR");
        setBorder(lblInsStatus);
        lblStatus = new JLabel("Ready status");
        Box boxStatus = Box.createHorizontalBox();
        boxStatus.add(lblStatus);
        boxStatus.add(Box.createHorizontalGlue());
        setBorder(boxStatus);

        Box statusBar = Box.createHorizontalBox();
        statusBar.add(boxStatus);
        statusBar.add(lblInsStatus);
        statusBar.add(lblRowCol);

        add(new RTextScrollPane(textArea), BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    public RSyntaxTextArea getTextArea() {
        return textArea;
    }

    private void updateRowColStatus() {
        int row = textArea.getCaretLineNumber() + 1;
        int col = textArea.getCaretPosition() - textArea.getLineStartOffsetOfCurrentLine() + 1;
        lblRowCol.setText("" + row + ":" + col);
    }

    private void updateTextModeStatus() {
        String text = textArea.getTextMode() == RSyntaxTextArea.INSERT_MODE ? "INS" : "OVR";
        lblInsStatus.setText(text);
    }

    private void setBorder(JComponent component) {
        component.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(yGap,xGap,yGap,xGap),
                            BorderFactory.createLineBorder(Color.LIGHT_GRAY)
                    ),
                    BorderFactory.createEmptyBorder(2*yGap, 2*xGap, yGap, 2*xGap)
                )
        );
    }
}
