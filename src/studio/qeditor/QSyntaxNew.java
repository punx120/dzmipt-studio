package studio.qeditor;

import org.netbeans.editor.Syntax;
import org.netbeans.editor.TokenID;

import java.util.*;

import static studio.qeditor.SyntaxStateMachine.Action.*;

public class QSyntaxNew extends Syntax {

    private enum State {Init, // start of a line (or file)
        AfterAtom, // - will means operator - after atom, identifier, brackets
        AfterOperator, // - could mean start of atom like -10
        AfterWhitespace,
        Dot,
        Long, Int, Short, Float, Real,
        Date, Time, Datetime, Timestamp, Timespan, Month, Minute, Second,
        Boolean, Byte, UnknownAtom,
        NullLong, NullFloat, InfLong, InfFloat,
        Zero, One, Two, ZeroOnes, Digits, DigitsDot, DigitsDotDigits, FractionE, FractionESign, Scientific,
        DigitsDotDigitsDot, DateLike,
        DigitsColon,MinuteLike,MinuteColon,SecondLike,SecondDot,TimeLikeD1,TimeLikeD2, TimeLikeD3, TimespanLike, //the order on this line is critical
            DateD1,DateD2,DateD3,DateD4,DateD5,DateD6,DateD7,DateD8,
            DateT1,DateT2,DateT3,DateT4,DateT5,DateT6,DateT7,DateT8,
            DigitsD1,DigitsD2,DigitsD3,DigitsD4,DigitsD5,DigitsD6,DigitsD7,DigitsD8,
        MinusZero,
        MinusForAtom, String, StringEscape, StringEscapeD, StringEscapeDD, Symbol, SymbolColon,
        Identifier,IdentifierDot,
        CommentEOL,
        CommentMLFirstLine, CommentMLInit, CommentML, CommentMLLastLine,
        InitBaskSlash, QCommandStart, QCommand, OSCommand
    }
    private static final State firstAtomState = State.Long;
    private static final State lastAtomState = State.MinusZero;

    private static final Set<String> keywords = new HashSet<>(Arrays.asList(
            "bin","asin","atan","exit","prd","prior","setenv","tan","wj","wj1","wsum","ej","aj0",
            "insert","acos","exp","wavg","avgs","log","sin","cos","sqrt","exec","abs","neg","not","null",
            "floor","string","reciprocal","ceiling","signum","div","mod","xbar","xlog","and","or","each",
            "mmu","lsq","inv","md5","ltime","gtime","count","first","var","dev","med","cov","cor","all",
            "any","rand","sums","prds","mins","maxs","fills","deltas","ratios","differ","prev","next",
            "rank","reverse","iasc","idesc","asc","desc","msum","mcount","mavg","mdev","xrank","mmin",
            "mmax","xprev","rotate","list","distinct","group","where","flip","type","key","til","get",
            "value","attr","cut","set","upsert","raze","union","inter","except","cross","ss","sv","vs",
            "sublist","read0","read1","hopen","hclose","hdel","hsym","hcount","peach","system","ltrim",
            "rtrim","trim","lower","upper","ssr","view","tables","views","cols","xcols","keys","xkey","xcol",
            "xasc","xdesc","fkeys","meta","uj","ij","lj","pj","aj","asof","fby","ungroup","xgroup","plist",
            "enlist","txf","save","load","rsave","rload","show","csv","parse","eval","over","scan","select",
            "from","where","within","update","in","delete","sum","avg","min","max","like","last","by","do",
            "while","if","getenv","xexp") );

    private final static SyntaxStateMachine stateMachine = new SyntaxStateMachine();

    public QSyntaxNew() {
        tokenContextPath=QTokenContext.contextPath;
    }

    private TokenID getNext() {
        TokenID token = null;
        while (offset<stopOffset) {
            SyntaxStateMachine.Next next = stateMachine.getNext(State.values()[state-INIT], buffer[offset++]);
            if (next == null) {
                state = State.AfterAtom.ordinal() + INIT;
                if (token != null) {
                    offset--;
                    return token;
                }
                return QTokenContext.UNKNOWN;
            } else {
                state = next.nextState.ordinal() + INIT;
                if (next.action == Match) return next.token;
                if (next.action == MatchPrev) {
                    offset--;
                    return next.token;
                }
                token = next.token;
            }
        }

        return token;
    }

    public TokenID parseToken() {
        int start = offset;
        TokenID token = getNext();
        if (token == QTokenContext.IDENTIFIER) {
            String word = new String(buffer, start, offset-start);
            if (keywords.contains(word)) {
                token = QTokenContext.KEYWORD;
            }
        }
        return token;
    }

    private static void add(State fromState, String chars, State nextState, TokenID token, SyntaxStateMachine.Action action) {
        stateMachine.add(fromState, chars, nextState, token, action);
    }
    private static void add(State[] fromStates, String chars, State nextState, TokenID token, SyntaxStateMachine.Action action) {
        for(State fromState:fromStates) {
            add(fromState, chars, nextState, token, action);
        }
    }
    private static State[] s(State... states) {
        return states;
    }

    private static final String digits = "0123456789";
    private static final String a2z = "abcdefghijklmnopqrstuvwxyz";
    private static final String A2Z = a2z.toUpperCase();
    private static final String alpha = a2z + A2Z;
    private static final String alphaNumeric = alpha + digits;
    private static final String atomChars = alphaNumeric + ":.";
    private static final String brackets = "[](){}";
    private static final String operators = "|/&^:!+-*%$=~#;@\\.><,?_'";
    private static final String whitespace = " \t\r";

    private static String except(String src, String exclude) {
        for (int i=0; i<exclude.length(); i++) {
            int index = src.indexOf(exclude.charAt(i));
            if (index == -1) continue;
            src = src.substring(0,index) + src.substring(index+1);
        }
        return src;
    }

    private final static State[] typeStates = new State[] {
            State.Long, State.Int, State.Short, State.Float, State.Real,
            State.Date, State.Time, State.Datetime, State.Timestamp, State.Timespan,
            State.Month, State.Minute, State.Second, State.Boolean };
    private final static TokenID[] tokens = new TokenID[] {
            QTokenContext.LONG, QTokenContext.INTEGER, QTokenContext.SHORT, QTokenContext.FLOAT, QTokenContext.REAL,
            QTokenContext.DATE, QTokenContext.TIME, QTokenContext.DATETIME, QTokenContext.TIMESTAMP, QTokenContext.TIMESPAN,
            QTokenContext.MONTH, QTokenContext.MINUTE, QTokenContext.SECOND, QTokenContext.BOOLEAN };
    private final static String types = "jihfedtzpnmuvb";

    private static void initUnknownAtom(State firstAtomState, State lastAtomState) {
        add(State.UnknownAtom, "", State.AfterAtom, QTokenContext.UNKNOWN, MatchPrev);
        for (int i=0; i<typeStates.length; i++) {
            add(typeStates[i], "", State.AfterAtom, tokens[i], MatchPrev);
        }

        int firstIndex = firstAtomState.ordinal();
        int lastIndex = lastAtomState.ordinal();
        for(int index = firstIndex; index<=lastIndex; index++) {
            State state = State.values()[index];
            String wrongChars = except(atomChars, stateMachine.getChars(state));
            add(state, wrongChars, State.UnknownAtom, QTokenContext.UNKNOWN, LooksLike);
        }
    }

    /* Various ending can be found with the following code. We don't code all possible combinations. Only natural ones
     test:{[s]
        s:"",s;
        types:"jihfepmdznuvt";
        types: types where @'[{parse x; 1b};;0b] s,/:types;
        values: parse each s,/:types;
        defTypes: @[{[types;values;s]types where values~\:parse s}[types;values;];s;" "];
        (defTypes; types except defTypes)
    }
    test "100"
     */
    private static void addAtomTypeEnding(String endingTypes, State... fromStates) {
        for (State fromState: fromStates) {
            addAtomTypeEnding(fromState, endingTypes);
        }
    }
    private static void addAtomTypeEnding(State fromState, String endingTypes) {
        for (int i=0; i<endingTypes.length(); i++) {
            int index = types.indexOf(endingTypes.charAt(i));
            if (index==-1) throw new IllegalStateException("Unknown type");

            add(fromState, ""+types.charAt(index), typeStates[index], tokens[index], LooksLike);
        }
    }

    private static void addTemporal(State[] fromStates, State firstState, TokenID[] tokens, String types, String... charsArray) {
        String chars = charsArray[0];
        for(State fromState: fromStates) {
            add(fromState, chars, firstState, tokens[0], LooksLike);
        }

        State state = firstState;
        for(int index = 1; index<charsArray.length; index++) {
            chars = charsArray[index];
            State nextState = State.values()[state.ordinal()+1];
            add(state, chars, nextState, tokens[index], LooksLike);
            if (chars.equals(digits)) {
                add(nextState, digits, nextState, tokens[index], LooksLike);
            }
            addAtomTypeEnding(state, types);
            state = nextState;
        }
        addAtomTypeEnding(state, types);
    }

    private static void addTemporal(State[] fromStates, State firstState, TokenID token, String types, String... charsArray) {
        TokenID[] tokens = new TokenID[charsArray.length];
        Arrays.fill(tokens, token);
        addTemporal(fromStates, firstState, tokens, types, charsArray);
    }

    private static void addTemporal(State fromState, State firstState, TokenID token, String types, String... charsArray) {
        addTemporal(new State[] {fromState}, firstState, token, types, charsArray);
    }

    /*
    rules for -
    INIT,whitespace -> sign
    operator ->sign

    bracket -> operator
    atom -> operator
    identifier -> operator

     */

    static {
        add(s(State.Init,State.AfterOperator,State.AfterAtom,State.AfterWhitespace), "0", State.Zero, QTokenContext.LONG, LooksLike);
        add(s(State.Init,State.AfterOperator,State.AfterAtom,State.AfterWhitespace), "1", State.One, QTokenContext.LONG, LooksLike);
        add(s(State.Init,State.AfterOperator,State.AfterAtom,State.AfterWhitespace), "2", State.Two, QTokenContext.LONG, LooksLike);
        add(s(State.Init,State.AfterOperator,State.AfterAtom,State.AfterWhitespace), except(digits, "012"), State.Digits, QTokenContext.LONG, LooksLike);
        add(s(State.Init,State.AfterOperator,State.AfterAtom,State.AfterWhitespace), ".", State.Dot, QTokenContext.OPERATOR, LooksLike);
        add(s(State.Init,State.AfterOperator,State.AfterAtom,State.AfterWhitespace), brackets, State.AfterAtom, QTokenContext.BRACKET, Match);
        add(s(State.Init,State.AfterOperator,State.AfterAtom,State.AfterWhitespace), except(operators, ".-\\/"), State.AfterOperator, QTokenContext.OPERATOR, Match);
        add(State.Init, whitespace, State.Init, QTokenContext.WHITESPACE, Match);
        add(s(State.AfterOperator,State.AfterAtom,State.AfterWhitespace), whitespace, State.AfterWhitespace, QTokenContext.WHITESPACE, Match);
        add(s(State.Init,State.AfterOperator,State.AfterAtom,State.AfterWhitespace), "\n", State.Init, QTokenContext.WHITESPACE, Match);
        add(s(State.Init,State.AfterOperator,State.AfterAtom,State.AfterWhitespace), "\"", State.String, QTokenContext.CHAR_VECTOR, LooksLike);
        add(s(State.Init,State.AfterOperator,State.AfterAtom,State.AfterWhitespace), "`", State.Symbol, QTokenContext.SYMBOL, LooksLike);
        add(s(State.Init,State.AfterOperator,State.AfterAtom,State.AfterWhitespace), alpha, State.Identifier, QTokenContext.IDENTIFIER, LooksLike);

        add(s(State.Init,State.AfterOperator,State.AfterWhitespace), "-", State.MinusForAtom, QTokenContext.OPERATOR, LooksLike);
        add(State.AfterAtom, "-", State.AfterOperator, QTokenContext.OPERATOR, Match);

        add(State.Init, "/", State.CommentMLFirstLine, QTokenContext.EOL_COMMENT, LooksLike);
        add(State.AfterWhitespace, "/", State.CommentEOL, QTokenContext.EOL_COMMENT, LooksLike);
        add(State.Init, "\\", State.InitBaskSlash, QTokenContext.OPERATOR, LooksLike);
        add(State.AfterWhitespace, "\\", State.AfterOperator, QTokenContext.OPERATOR, Match);
        add(s(State.AfterAtom,State.AfterOperator), "\\/", State.AfterOperator, QTokenContext.OPERATOR, Match);

        add(State.CommentEOL, "\n", State.Init, QTokenContext.EOL_COMMENT, Match);
        add(State.CommentEOL, "", State.CommentEOL, QTokenContext.EOL_COMMENT, LooksLike);

        add(State.CommentMLFirstLine, whitespace, State.CommentMLFirstLine, QTokenContext.EOL_COMMENT, LooksLike);
        add(State.CommentMLFirstLine, "\n", State.CommentMLInit, QTokenContext.EOL_COMMENT, Match);
        add(State.CommentMLFirstLine, "", State.CommentEOL, QTokenContext.EOL_COMMENT, LooksLike);

        add(State.CommentMLInit, whitespace, State.CommentMLInit, QTokenContext.EOL_COMMENT, LooksLike);
        add(State.CommentMLInit, "\\", State.CommentMLLastLine, QTokenContext.EOL_COMMENT, LooksLike);
        add(State.CommentMLInit, "\n", State.CommentMLInit, QTokenContext.EOL_COMMENT, Match);
        add(State.CommentMLInit, "", State.CommentML, QTokenContext.EOL_COMMENT, LooksLike);

        add(State.CommentML, "\n", State.CommentMLInit, QTokenContext.EOL_COMMENT, Match);
        add(State.CommentML, "", State.CommentML, QTokenContext.EOL_COMMENT, LooksLike);

        add(State.CommentMLLastLine, whitespace, State.CommentMLLastLine, QTokenContext.EOL_COMMENT, LooksLike);
        add(State.CommentMLLastLine, "\n", State.Init, QTokenContext.EOL_COMMENT, Match);
        add(State.CommentMLLastLine, "", State.CommentML, QTokenContext.EOL_COMMENT, LooksLike);

        add(State.InitBaskSlash, alpha, State.QCommandStart, QTokenContext.COMMAND, LooksLike);
        add(State.InitBaskSlash, "", State.AfterOperator, QTokenContext.OPERATOR, MatchPrev);

        add(State.QCommandStart, whitespace, State.QCommand, QTokenContext.COMMAND, LooksLike);
        add(State.QCommandStart, "\n", State.Init, QTokenContext.COMMAND, Match);
        add(State.QCommandStart, "", State.OSCommand, QTokenContext.SYSTEM, LooksLike);

        add(State.QCommand, "\n", State.Init, QTokenContext.COMMAND, Match);
        add(State.QCommand, "", State.QCommand, QTokenContext.COMMAND, LooksLike);

        add(State.OSCommand, "\n", State.Init, QTokenContext.SYSTEM, Match);
        add(State.OSCommand, "", State.OSCommand, QTokenContext.SYSTEM, LooksLike);

        add(State.MinusForAtom, except(digits,"0"), State.Digits, QTokenContext.LONG, LooksLike);
        add(State.MinusForAtom, "0", State.MinusZero, QTokenContext.LONG, LooksLike);
        add(State.MinusForAtom, ".", State.Dot, QTokenContext.OPERATOR, LooksLike);
        add(State.MinusForAtom, "", State.AfterOperator, QTokenContext.OPERATOR, MatchPrev);

        add(s(State.Zero, State.One), "01", State.ZeroOnes, QTokenContext.LONG, LooksLike);
        add(s(State.Zero, State.One), except(digits,"01"), State.Digits, QTokenContext.LONG, LooksLike);
        add(s(State.Zero, State.One, State.Two, State.MinusZero, State.ZeroOnes, State.Digits), ".", State.DigitsDot, QTokenContext.FLOAT, LooksLike);
        add(s(State.Zero, State.One, State.Two, State.MinusZero,
                State.ZeroOnes, State.Digits, State.DigitsDot, State.DigitsDotDigits),
                "e", State.FractionE, QTokenContext.REAL, LooksLike);
        add(State.Zero, "x", State.Byte, QTokenContext.BYTE, LooksLike);
        add(State.Zero, "n", State.NullFloat, QTokenContext.FLOAT, LooksLike);
        add(State.Zero, "N", State.NullLong, QTokenContext.LONG, LooksLike);
        add(s(State.Zero, State.MinusZero), "w", State.InfFloat, QTokenContext.FLOAT, LooksLike);
        add(s(State.Zero, State.MinusZero), "W", State.InfLong, QTokenContext.LONG, LooksLike);
        addAtomTypeEnding("jihfpuvtb", State.Zero, State.One, State.Two, State.MinusZero);
        addAtomTypeEnding("jihfepmdznuvt", State.NullFloat, State.NullLong, State.InfFloat, State.InfLong);
        add(s(State.NullFloat, State.NullLong), "g", State.AfterAtom, QTokenContext.KEYWORD, Match);
        add(s(State.NullFloat, State.NullLong), "c", State.AfterAtom, QTokenContext.CHAR_VECTOR, Match);

        add(s(State.MinusZero, State.Digits), digits, State.Digits, QTokenContext.LONG, LooksLike);

        add(State.Two, digits, State.Digits, QTokenContext.LONG, LooksLike);
        add(s(State.Zero,State.One,State.Two),":",State.AfterOperator, QTokenContext.OPERATOR, Match);

        add(State.ZeroOnes, "01", State.ZeroOnes, QTokenContext.LONG, LooksLike);
        add(State.ZeroOnes, except(digits, "01"), State.Digits, QTokenContext.LONG, LooksLike);
        addAtomTypeEnding(State.ZeroOnes, "jihfpnuvtb");

        addAtomTypeEnding(State.Digits, "jihfpnuvt");

        add(State.DigitsDot, ".", State.UnknownAtom, QTokenContext.UNKNOWN, LooksLike);
        add(State.DigitsDot, digits, State.DigitsDotDigits, QTokenContext.FLOAT, LooksLike);
        addAtomTypeEnding(State.DigitsDot,"fpnt");

        add(State.DigitsDotDigits, digits, State.DigitsDotDigits, QTokenContext.FLOAT, LooksLike);
        add(State.DigitsDotDigits, ".", State.DigitsDotDigitsDot, QTokenContext.UNKNOWN, LooksLike);
        addAtomTypeEnding(State.DigitsDotDigits, "fpmnt");

        add(State.Dot, digits, State.DigitsDotDigits, QTokenContext.FLOAT, LooksLike);
        add(State.Dot, "", State.AfterOperator, QTokenContext.OPERATOR, MatchPrev);

        add(State.FractionE, "+-", State.FractionESign, QTokenContext.UNKNOWN, LooksLike);
        add(State.FractionE, digits, State.Scientific, QTokenContext.FLOAT, LooksLike);

        add(State.FractionESign, digits, State.Scientific, QTokenContext.FLOAT, LooksLike);

        add(State.Scientific, digits, State.Scientific, QTokenContext.FLOAT, LooksLike);
        addAtomTypeEnding(State.Scientific, "fem");

        add(State.DigitsDotDigitsDot, digits, State.DateLike, QTokenContext.DATE, LooksLike);
        add(State.DateLike, digits, State.DateLike, QTokenContext.DATE, LooksLike);
        addAtomTypeEnding(State.DateLike, "dpmzn");

        addTemporal(State.DateLike, State.DateD1, QTokenContext.TIMESTAMP,"pmzn","D",digits,":",digits,":",digits,".",digits);
        addTemporal(State.DateLike, State.DateT1, QTokenContext.DATETIME,"pmzn","T",digits,":",digits,":",digits,".",digits);
        addTemporal(s(State.Zero, State.One, State.Two, State.ZeroOnes, State.Digits), State.DigitsD1, QTokenContext.TIMESPAN, "pmzn", "D",digits,":",digits,":",digits,".",digits);

        String d1 ="9876543210"; // to not equals to digits
        addTemporal(s(State.ZeroOnes, State.Digits),State.DigitsColon,
                new TokenID[] {
                        QTokenContext.MINUTE, QTokenContext.MINUTE, QTokenContext.MINUTE,
                        QTokenContext.SECOND, QTokenContext.TIME, QTokenContext.TIME, QTokenContext.TIME, QTokenContext.TIME,
                        QTokenContext.TIMESPAN
                },"uvtpn", ":",digits,":",digits,".",d1,d1,d1,digits);


        add(State.Byte, digits + "abcdefABCDEF", State.Byte, QTokenContext.BYTE, LooksLike);

        initUnknownAtom(firstAtomState, lastAtomState);

        add(State.String, "\\", State.StringEscape, QTokenContext.CHAR_VECTOR, Match);
        add(State.String, "\"", State.AfterAtom, QTokenContext.CHAR_VECTOR, Match);
        add(State.String, "\n", State.String, QTokenContext.CHAR_VECTOR, Match);
        add(State.String, "", State.String, QTokenContext.CHAR_VECTOR, LooksLike);
        add(State.StringEscape, "nrt\\\"", State.String, QTokenContext.CHAR_VECTOR, Match);
        add(State.StringEscape, "0123", State.StringEscapeD, QTokenContext.UNKNOWN, LooksLike);
        add(State.StringEscape, "", State.String, QTokenContext.UNKNOWN, Match);
        add(State.StringEscapeD, "01234567", State.StringEscapeDD, QTokenContext.UNKNOWN, LooksLike);
        add(State.StringEscapeD, "", State.String, QTokenContext.UNKNOWN, Match);
        add(State.StringEscapeDD, "01234567", State.String, QTokenContext.CHAR_VECTOR, Match);
        add(State.StringEscapeDD, "", State.String, QTokenContext.UNKNOWN, Match);

        add(State.Symbol, alphaNumeric + "`_.", State.Symbol, QTokenContext.SYMBOL, LooksLike);
        add(State.Symbol, ":", State.SymbolColon, QTokenContext.SYMBOL, LooksLike);
        add(State.Symbol, "", State.AfterAtom, QTokenContext.SYMBOL, MatchPrev);

        add(State.SymbolColon, alphaNumeric + "_.:/", State.SymbolColon, QTokenContext.SYMBOL, LooksLike);
        add(State.SymbolColon, "`", State.Symbol, QTokenContext.SYMBOL, LooksLike);
        add(State.SymbolColon, "", State.AfterAtom, QTokenContext.SYMBOL, MatchPrev);

        add(State.Identifier, alphaNumeric +"_", State.Identifier, QTokenContext.IDENTIFIER, LooksLike);
        add(State.Identifier, ".", State.IdentifierDot, QTokenContext.IDENTIFIER, LooksLike);
        add(State.IdentifierDot, alpha, State.Identifier, QTokenContext.IDENTIFIER, LooksLike);
        add(State.Dot, alpha, State.Identifier, QTokenContext.IDENTIFIER, LooksLike);
    }

    static {
        stateMachine.init();
    }

}
