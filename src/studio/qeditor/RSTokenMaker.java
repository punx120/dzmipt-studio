package studio.qeditor;

import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMakerBase;
import studio.qeditor.syntax.QSyntaxParser;
import studio.qeditor.syntax.QToken;

import javax.swing.text.Segment;


public class RSTokenMaker extends TokenMakerBase {

    public static final String CONTENT_TYPE = "text/q";

    private QSyntaxParser parser = new QSyntaxParser();

    private RSToken[] tokens = new RSToken[] {
        RSToken.SYMBOL, RSToken.STRING, RSToken.IDENTIFIER, RSToken.OPERATOR, RSToken.BRACKET, RSToken.EOL_COMMENT,
        RSToken.ML_COMMENT,
        RSToken.KEYWORD, RSToken.WHITESPACE, RSToken.UNKNOWN, RSToken.INTEGER, RSToken.MINUTE, RSToken.SECOND,
        RSToken.TIME, RSToken.DATE, RSToken.MONTH, RSToken.FLOAT, RSToken.LONG, RSToken.SHORT, RSToken.REAL, RSToken.BYTE,
        RSToken.BOOLEAN, RSToken.DATETIME, RSToken.TIMESTAMP, RSToken.TIMESPAN, RSToken.SYSTEM, RSToken.COMMAND
    };

    @Override
    public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
        resetTokenList();

        boolean mlComment = initialTokenType == RSToken.ML_COMMENT.getTokenType();

        int offset = text.offset;
        int delta = startOffset - text.offset;

        if (text.count == 0) {
            if (mlComment) {
                addToken(text, offset, offset-1, RSToken.ML_COMMENT.getTokenType(), delta + offset);
            } else {
                addNullToken();
            }
            return firstToken;
        }

        int initState = mlComment ? QSyntaxParser.MLCommentInitState : QSyntaxParser.InitState;

        parser.init(initState, text.array, offset, text.offset + text.count);

        QToken lastToken = null;
        for (;;) {
            QToken token = parser.next();
            if (token == null) break;
            lastToken = token;
            addToken(text, offset, parser.getOffset() - 1, tokens[token.ordinal()].getTokenType(),delta + offset);
            offset = parser.getOffset();
        }
        if (lastToken != QToken.ML_COMMENT) {
            addNullToken();
        }

        return firstToken;
    }

    @Override
    public boolean getCurlyBracesDenoteCodeBlocks(int languageIndex) {
        return true;
    }

    @Override
    public boolean getShouldIndentNextLineAfter(Token t) {
        if (t!=null && t.length()==1) {
            char ch = t.charAt(0);
            return ch=='{' || ch=='(';
        }
        return false;
    }

}
