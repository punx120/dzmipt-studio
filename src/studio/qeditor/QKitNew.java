package studio.qeditor;

import org.netbeans.editor.Syntax;
import javax.swing.text.Document;

public class QKitNew extends QKit {

    public static final String CONTENT_TYPE = "text/q-new";

    public String getContentType() {
        return CONTENT_TYPE; // NOI18N
    }

    public Syntax createSyntax(Document document) {
        return new QSyntaxNew();
    }

}
