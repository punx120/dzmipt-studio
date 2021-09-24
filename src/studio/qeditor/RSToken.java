package studio.qeditor;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.TokenTypes;

import java.awt.*;
import java.util.Arrays;

public enum RSToken {

    NULL(TokenTypes.NULL, Color.BLACK),
    SYMBOL(TokenTypes.DEFAULT_NUM_TOKEN_TYPES, new Color(179,0,134)),
    CHAR_VECTOR(TokenTypes.LITERAL_CHAR, new Color(0,200,20)),
    IDENTIFIER(TokenTypes.IDENTIFIER, new Color(180,160,0)),
    OPERATOR(TokenTypes.OPERATOR, Color.BLACK),
    BRACKET(TokenTypes.SEPARATOR, Color.BLACK),
    EOL_COMMENT(TokenTypes.COMMENT_EOL, Font.ITALIC, Color.GRAY),
    ML_COMMENT(TokenTypes.COMMENT_MULTILINE, Font.ITALIC, Color.GRAY),
    KEYWORD(TokenTypes.RESERVED_WORD, Font.BOLD, Color.BLUE),
    WHITESPACE(TokenTypes.WHITESPACE, Color.BLACK),
    UNKNOWN(TokenTypes.ERROR_NUMBER_FORMAT, Font.BOLD, Color.RED),
    INTEGER(TokenTypes.DEFAULT_NUM_TOKEN_TYPES + 1,new Color(51,104,255)),
    MINUTE(TokenTypes.DEFAULT_NUM_TOKEN_TYPES + 2, new Color(184,138,0)),
    SECOND(TokenTypes.DEFAULT_NUM_TOKEN_TYPES + 3, new Color(184,138,0)),
    TIME(TokenTypes.DEFAULT_NUM_TOKEN_TYPES + 4, new Color(184,138,0)),
    DATE(TokenTypes.DEFAULT_NUM_TOKEN_TYPES + 5, new Color(184,138,0)),
    MONTH(TokenTypes.DEFAULT_NUM_TOKEN_TYPES + 6, new Color(184,138,0)),
    FLOAT(TokenTypes.LITERAL_NUMBER_FLOAT, new Color(51,104,255)),
    LONG(TokenTypes.LITERAL_NUMBER_DECIMAL_INT, new Color(51,104,255)),
    SHORT(TokenTypes.DEFAULT_NUM_TOKEN_TYPES + 7, new Color(51,104,255)),
    REAL(TokenTypes.DEFAULT_NUM_TOKEN_TYPES + 8, new Color(51,104,255)),
    BYTE(TokenTypes.DEFAULT_NUM_TOKEN_TYPES + 9, new Color(51,104,255)),
    BOOLEAN(TokenTypes.LITERAL_BOOLEAN, new Color(51,104,255)),
    DATETIME(TokenTypes.DEFAULT_NUM_TOKEN_TYPES + 10, new Color(184,138,0)),
    TIMESTAMP(TokenTypes.DEFAULT_NUM_TOKEN_TYPES + 11, new Color(184,138,0)),
    TIMESPAN(TokenTypes.DEFAULT_NUM_TOKEN_TYPES + 12, new Color(184,138,0)),
    COMMAND(TokenTypes.VARIABLE, new Color(240,180,0)),
    SYSTEM(TokenTypes.PREPROCESSOR, new Color(240,180,0));

    public final static int NUM_TOKEN_TYPES = TokenTypes.DEFAULT_NUM_TOKEN_TYPES + 13;

    private static RSToken[] tokenTypesToQToken = new RSToken[NUM_TOKEN_TYPES];
    static {
        Arrays.fill(tokenTypesToQToken, null);
        for (RSToken token: values()) {
            tokenTypesToQToken[token.getTokenType()] = token;
        }
    }

    public static RSToken fromTokenType(int tokenType) {
        RSToken result = tokenTypesToQToken[tokenType];
        if (result == null) throw new IllegalArgumentException(String.format("Token type %d is not defined", tokenType));
        return result;
    }

    public int getTokenType() {
        return tokenType;
    }

    private int tokenType;
    private Style style;
    public Style getStyle() {
        return style;
    }

    RSToken(int tokenType, int fontStyle, Color color) {
        this.tokenType = tokenType;
        Font font = RSyntaxTextArea.getDefaultFont();
        if (fontStyle != Font.PLAIN) font = font.deriveFont(fontStyle);
        style = new Style(color, null, font);
    }

    RSToken(int tokenType, Color color) {
        this(tokenType, Font.PLAIN, color);
    }

    RSToken(int tokenType) {
        this.tokenType = tokenType;
    }

    public static SyntaxScheme getDefaulSyntaxScheme() {
        SyntaxScheme scheme = new SyntaxScheme(false);
        Style[] defaultStyles = scheme.getStyles();
        Style[] styles = new Style[NUM_TOKEN_TYPES];
        System.arraycopy(defaultStyles, 0, styles, 0, defaultStyles.length);
        for (RSToken token: RSToken.values()) {
            styles[token.getTokenType()] = token.getStyle();
        }
        scheme.setStyles(styles);
        return scheme;
    }

    
}
