package studio.ui.action;

import studio.kdb.K;
import studio.kdb.KFormatContext;
import studio.ui.Util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CopyTableSelectionAction implements ActionListener {

    private static String newline = System.getProperty("line.separator");

    public enum Format {Excel, Html};

    private final JTable table;
    private final Format format;

    public CopyTableSelectionAction(Format format, JTable table) {
        this.table = table;
        this.format = format;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (format == Format.Excel) copyExcelFormat();
        else copyHtmlFormat();
    }

    private void copyExcelFormat() {
        StringBuilder sb = new StringBuilder();
        int numcols = table.getSelectedColumnCount();
        int numrows = table.getSelectedRowCount();
        int[] rowsselected = table.getSelectedRows();
        int[] colsselected = table.getSelectedColumns();
        if (!isTableSelectionValid()) {
            JOptionPane.showMessageDialog(null,
                    "Invalid Copy Selection",
                    "Invalid Copy Selection",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (table.getSelectedRowCount() == table.getRowCount()) {
            for (int col = 0; col < numcols; col++) {
                sb.append(table.getColumnName(colsselected[col]));
                if (col < numcols - 1)
                    sb.append("\t");
            }
            sb.append(newline);
        }

        for (int row = 0; row < numrows; row++) {
            if (row > 0)
                sb.append(newline);
            for (int col = 0; col < numcols; col++) {
                boolean symColumn = table.getColumnClass(colsselected[col]) == K.KSymbolVector.class;
                if (symColumn)
                    sb.append("\"");

                K.KBase b = (K.KBase) table.getValueAt(rowsselected[row], colsselected[col]);
                if (!b.isNull())
                    sb.append(b.toString(KFormatContext.NO_TYPE));
                if (symColumn)
                    sb.append("\"");
                if (col < numcols - 1)
                    sb.append("\t");
            }
        }
        Util.copyTextToClipboard(sb.toString());
    }

    private void copyHtmlFormat() {
        StringBuilder sb = new StringBuilder();
        int numcols = table.getSelectedColumnCount();
        int numrows = table.getSelectedRowCount();

        int[] rowsselected = table.getSelectedRows();
        int[] colsselected = table.getSelectedColumns();

        if (!isTableSelectionValid()) {
            JOptionPane.showMessageDialog(null,
                    "Invalid Copy Selection",
                    "Invalid Copy Selection",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        sb.append("<meta http-equiv=\"content-type\" content=\"text/html\"><table>");

        sb.append("<tr>");
        for (int col = 0; col < numcols; col++) {
            sb.append("<th>").append(table.getColumnName(colsselected[col])).append("</th>");
        }
        sb.append("</tr>").append(newline);

        for (int row = 0; row < numrows; row++) {
            if (row > 0) {
                sb.append(newline);
            }
            sb.append("<tr>");
            for (int col = 0; col < numcols; col++) {
                K.KBase b = (K.KBase) table.getValueAt(rowsselected[row], colsselected[col]);
                sb.append("<td>");
                if (!b.isNull())
                    sb.append(b.toString(KFormatContext.NO_TYPE));
                sb.append("</td>");
            }
            sb.append("</tr>");
        }

        sb.append("</table>");
        Util.copyHtmlToClipboard(sb.toString());
    }

    private boolean isTableSelectionValid() {
        int numcols = table.getSelectedColumnCount();
        int numrows = table.getSelectedRowCount();
        return numrows>0 && numcols>0;
    }

}
