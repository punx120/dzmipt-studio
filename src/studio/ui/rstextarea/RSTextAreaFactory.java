package studio.ui.rstextarea;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rsyntaxtextarea.folding.CurlyFoldParser;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;
import org.fife.ui.rtextarea.ClipboardHistory;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RecordableTextAction;
import studio.kdb.Config;
import studio.qeditor.RSToken;
import studio.qeditor.RSTokenMaker;

import javax.swing.*;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class RSTextAreaFactory {

    static {
        java.util.List<Action> actions = new ArrayList<>();
        actions.addAll(Arrays.asList(new RSyntaxTextAreaEditorKit().getActions()));
        actions.add(new CopyCutAsStyledTextAction(false));
        actions.add(new CopyCutAsStyledTextAction(true));

        ActionMap map = new ActionMapUIResource();
        for (Action a : actions) {
            map.put(a.getValue(Action.NAME), a);
        }
        UIManager.put("RSyntaxTextAreaUI.actionMap", map);


        int shift = InputEvent.SHIFT_DOWN_MASK;
        int defaultModifier = RTextArea.getDefaultModifier();
        InputMap inputMap = new RSyntaxTextAreaDefaultInputMap();
        //@TODO: what other hotkeys should we add?
        //@TODO: Is it for MacOS only?
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), DefaultEditorKit.endLineAction);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), DefaultEditorKit.beginLineAction);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, shift), DefaultEditorKit.selectionEndLineAction);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, shift), DefaultEditorKit.selectionBeginLineAction);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C,      defaultModifier), CopyCutAsStyledTextAction.rstaCopyAsStyledTextAction);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_COPY,   0),      CopyCutAsStyledTextAction.rstaCopyAsStyledTextAction);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, defaultModifier), CopyCutAsStyledTextAction.rstaCopyAsStyledTextAction);

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_X,      defaultModifier), CopyCutAsStyledTextAction.rstaCutAsStyledTextAction);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_CUT,    0),      CopyCutAsStyledTextAction.rstaCutAsStyledTextAction);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, shift),           CopyCutAsStyledTextAction.rstaCutAsStyledTextAction);

        inputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, defaultModifier)); // used for execute current line
        inputMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_J, defaultModifier)); // used for adding thousand commas

        UIManager.put("RSyntaxTextAreaUI.inputMap", inputMap);

        FoldParserManager.get().addFoldParserMapping(RSTokenMaker.CONTENT_TYPE, new CurlyFoldParser());

        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
        atmf.putMapping(RSTokenMaker.CONTENT_TYPE, RSTokenMaker.class.getName());
    }


    public static RSyntaxTextArea newTextArea() {
        RSyntaxTextArea textArea = new RSyntaxTextArea("");
        textArea.setLineWrap(Config.getInstance().getBoolean(Config.RSTA_WORD_WRAP));
        textArea.setAnimateBracketMatching(Config.getInstance().getBoolean(Config.RSTA_ANIMATE_BRACKET_MATCHING));
        textArea.setHighlightCurrentLine(Config.getInstance().getBoolean(Config.RSTA_HIGHLIGHT_CURRENT_LINE));
        textArea.setCodeFoldingEnabled(true);
        textArea.setCloseCurlyBraces(true);

        textArea.setSyntaxEditingStyle(RSTokenMaker.CONTENT_TYPE);
        textArea.setSyntaxScheme(RSToken.getDefaulSyntaxScheme());

        return textArea;
    }


    // Taken from PR: https://github.com/bobbylight/RSyntaxTextArea/pull/406.
    // Once/if the library gets the change, probably the below we will need to remove
    public static class CopyCutAsStyledTextAction extends RecordableTextAction {

        public static final String rstaCutAsStyledTextAction = "kdbStudio.rstaCutAsStyledTextAction";
        public static final String rstaCopyAsStyledTextAction = "kdbStudio.rstaCopyAsStyledTextAction";

        private Theme theme;
        private boolean cutAction = false;

        private static final long serialVersionUID = 2L;

        private static String getActionName(boolean cutAction) {
            return cutAction ? rstaCutAsStyledTextAction : rstaCopyAsStyledTextAction;
        }

        public CopyCutAsStyledTextAction(boolean cutAction) {
            super(getActionName(cutAction));
            this.cutAction = cutAction;
        }

        @Override
        public void actionPerformedImpl(ActionEvent e, RTextArea textArea) {
            ((RSyntaxTextArea)textArea).copyAsStyledText(theme);
            ClipboardHistory.get().add(textArea.getSelectedText());
            if (cutAction) {
                int selStart = textArea.getSelectionStart();
                int selEnd = textArea.getSelectionEnd();

                try {
                    textArea.getDocument().remove(selStart, selEnd - selStart);
                } catch (BadLocationException ex) {
                    System.err.println("Ups... That's not expected: " + ex);
                    ex.printStackTrace();
                }
            }
            textArea.requestFocusInWindow();
        }

        @Override
        public final String getMacroID() {
            return getName();
        }

    }

}
