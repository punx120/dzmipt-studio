package studio.qeditor;

import studio.kdb.Config;
import java.awt.Color;
import java.awt.Font;
import org.netbeans.editor.Coloring;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsDefaults;
import org.netbeans.editor.SettingsUtil;
import org.netbeans.editor.TokenCategory;
import org.netbeans.editor.TokenContextPath;

class QTokenColoringInitializer extends SettingsUtil.TokenColoringInitializer
{
    Font boldFont=SettingsDefaults.defaultFont.deriveFont(Font.BOLD);
    Font italicFont=SettingsDefaults.defaultFont.deriveFont(Font.ITALIC);
    Settings.Evaluator boldSubst=new SettingsUtil.FontStylePrintColoringEvaluator(Font.BOLD);
    Settings.Evaluator italicSubst=new SettingsUtil.FontStylePrintColoringEvaluator(Font.ITALIC);
    Settings.Evaluator lightGraySubst=new SettingsUtil.ForeColorPrintColoringEvaluator(Color.lightGray);
    private Coloring CHARVECTOR_Coloring;
    private Coloring EOL_COMMENT_Coloring;
    private Coloring IDENTIFIER_Coloring;
    private Coloring OPERATOR_Coloring;
    private Coloring BOOLEAN_Coloring;
    private Coloring BYTE_Coloring;
    private Coloring SHORT_Coloring;
    private Coloring LONG_Coloring;
    private Coloring REAL_Coloring;
    private Coloring INTEGER_Coloring;
    private Coloring FLOAT_Coloring;
    private Coloring TIMESTAMP_Coloring;
    private Coloring TIMESPAN_Coloring;
    private Coloring DATETIME_Coloring;
    private Coloring DATE_Coloring;
    private Coloring MONTH_Coloring;
    private Coloring MINUTE_Coloring;
    private Coloring SECOND_Coloring;
    private Coloring TIME_Coloring;
    private Coloring SYMBOL_Coloring;
    private Coloring KEYWORD_Coloring;
    private Coloring COMMAND_Coloring;
    private Coloring SYSTEM_Coloring;
    private Coloring WHITESPACE_Coloring;
    private Coloring DEFAULT_Coloring;
    private Coloring BRACKET_Coloring;

    private Coloring buildColoring(String tokenName, Font font)
    {
        return new Coloring(font,
                            Coloring.FONT_MODE_APPLY_STYLE,
                            Config.getInstance().getColor("token." + tokenName),
                            null);
    }

    public QTokenColoringInitializer()
    {
        super(QTokenContext.context);
        CHARVECTOR_Coloring=buildColoring("CHARVECTOR", SettingsDefaults.defaultFont);
        EOL_COMMENT_Coloring=buildColoring("EOLCOMMENT", italicFont);
        IDENTIFIER_Coloring=buildColoring("IDENTIFIER", SettingsDefaults.defaultFont);
        OPERATOR_Coloring=buildColoring("OPERATOR", SettingsDefaults.defaultFont);
        BOOLEAN_Coloring=buildColoring("BOOLEAN", SettingsDefaults.defaultFont);
        BYTE_Coloring=buildColoring("BYTE",SettingsDefaults.defaultFont);
        SHORT_Coloring=buildColoring("SHORT",SettingsDefaults.defaultFont);
        LONG_Coloring=buildColoring("LONG",SettingsDefaults.defaultFont);
        REAL_Coloring=buildColoring("REAL",SettingsDefaults.defaultFont);
        INTEGER_Coloring=buildColoring("INTEGER",SettingsDefaults.defaultFont);
        FLOAT_Coloring=buildColoring("FLOAT",SettingsDefaults.defaultFont);
        TIMESTAMP_Coloring=buildColoring("TIMESTAMP",SettingsDefaults.defaultFont);
        TIMESPAN_Coloring=buildColoring("TIMESPAN",SettingsDefaults.defaultFont);
        DATETIME_Coloring=buildColoring("DATETIME",SettingsDefaults.defaultFont);
        DATE_Coloring=buildColoring("DATE",SettingsDefaults.defaultFont);
        MONTH_Coloring=buildColoring("MONTH",SettingsDefaults.defaultFont);
        MINUTE_Coloring=buildColoring("MINUTE",SettingsDefaults.defaultFont);
        SECOND_Coloring=buildColoring("SECOND",SettingsDefaults.defaultFont);
        TIME_Coloring=buildColoring("TIME",SettingsDefaults.defaultFont);
        SYMBOL_Coloring=buildColoring("SYMBOL",SettingsDefaults.defaultFont);
        KEYWORD_Coloring=buildColoring("KEYWORD", boldFont);
        COMMAND_Coloring=buildColoring("COMMAND", SettingsDefaults.defaultFont);
        SYSTEM_Coloring=buildColoring("SYSTEM", SettingsDefaults.defaultFont);
        WHITESPACE_Coloring=buildColoring("WHITESPACE", SettingsDefaults.defaultFont);
        DEFAULT_Coloring=buildColoring("DEFAULT", SettingsDefaults.defaultFont);
        BRACKET_Coloring=buildColoring("BRACKET", SettingsDefaults.defaultFont);
    }

    public Object getTokenColoring(TokenContextPath tokenContextPath,
                                   TokenCategory tokenIDOrCategory,
                                   boolean printingSet)
    {
        if(!printingSet)
        {
            switch(tokenIDOrCategory.getNumericID())
            {
                case QTokenContext.CHARVECTOR_ID:
                    return CHARVECTOR_Coloring;
                case QTokenContext.EOL_COMMENT_ID:
                case QTokenContext.ML_COMMENT_ID:
                    return EOL_COMMENT_Coloring;
                case QTokenContext.IDENTIFIER_ID:
                    return IDENTIFIER_Coloring;
                case QTokenContext.OPERATOR_ID:
                    return OPERATOR_Coloring;
                case QTokenContext.BRACKET_ID:
                    return BRACKET_Coloring;
                case QTokenContext.BOOLEAN_ID:
                    return BOOLEAN_Coloring;
                case QTokenContext.BYTE_ID:
                    return BYTE_Coloring;
                case QTokenContext.SHORT_ID:
                    return SHORT_Coloring;
                case QTokenContext.LONG_ID:
                    return LONG_Coloring;
                case QTokenContext.REAL_ID:
                    return REAL_Coloring;
                case QTokenContext.INTEGER_ID:
                    return INTEGER_Coloring;
                case QTokenContext.FLOAT_ID:
                    return FLOAT_Coloring;
                case QTokenContext.DATETIME_ID:
                    return DATETIME_Coloring;
                case QTokenContext.TIMESTAMP_ID:
                    return TIMESTAMP_Coloring;
                case QTokenContext.TIMESPAN_ID:
                    return TIMESPAN_Coloring;
                case QTokenContext.DATE_ID:
                    return DATE_Coloring;
                case QTokenContext.MONTH_ID:
                    return MONTH_Coloring;
                case QTokenContext.MINUTE_ID:
                    return MINUTE_Coloring;
                case QTokenContext.SECOND_ID:
                    return SECOND_Coloring;
                case QTokenContext.TIME_ID:
                    return TIME_Coloring;
                case QTokenContext.SYMBOL_ID:
                    return SYMBOL_Coloring;
                case QTokenContext.UNKNOWN_ID:
                    return new Coloring(boldFont,Coloring.FONT_MODE_APPLY_STYLE,Config.getInstance().getColor(Config.COLOR_ERROR), null);
                case QTokenContext.KEYWORD_ID:
                    return KEYWORD_Coloring;
                case QTokenContext.COMMAND_ID:
                    return COMMAND_Coloring;
                case QTokenContext.SYSTEM_ID:
                    return SYSTEM_Coloring;
                case QTokenContext.WHITESPACE_ID:
                    return WHITESPACE_Coloring;
                default:
                    return DEFAULT_Coloring;
            }
        }
        else
        { // printing set
            switch(tokenIDOrCategory.getNumericID())
            {
                default:
                    return SettingsUtil.defaultPrintColoringEvaluator;
            }
        }
    }
}
