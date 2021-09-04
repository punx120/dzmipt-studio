package studio.ui;

import studio.kdb.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

public class WidthAdjuster extends MouseAdapter {

    private int gap;
    public WidthAdjuster(JTable table) {
        this.table = table;
        table.getTableHeader().addMouseListener(this);
        gap = SwingUtilities.computeStringWidth(table.getFontMetrics(UIManager.getFont("Table.font")), "x") / 2;

    }

    public void mousePressed(MouseEvent evt) {
        if (evt.getClickCount() > 1 && usingResizeCursor())
            if ((table.getSelectedRowCount() == table.getRowCount()) && (table.getSelectedColumnCount() == table.getColumnCount()))
                resizeAllColumns();
            else
                resize(getLeftColumn(evt.getPoint()));
    }

    public void mouseClicked(final MouseEvent e) {
        if (!usingResizeCursor()) {
            JTableHeader h = (JTableHeader) e.getSource();
            TableColumnModel columnModel = h.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            if (viewColumn >= 0) {
                final int column = columnModel.getColumn(viewColumn).getModelIndex();

                KTableModel ktm = (KTableModel) table.getModel();
                if (ktm.isSortedAsc(column))
                    ktm.desc(column);
                else if (ktm.isSortedDesc(column))
                    ktm.removeSort();
                else
                    ktm.asc(column);

                ktm.fireTableDataChanged();
                h.repaint();
            }
        }
    }

    private JTableHeader getTableHeader() {
        return table.getTableHeader();
    }

    private boolean usingResizeCursor() {
        Cursor cursor = getTableHeader().getCursor();
        return cursor.equals(EAST) || cursor.equals(WEST);
    }
    private static final Cursor EAST = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
    private static final Cursor WEST = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
    //if near the boundary, will choose left column
    private int getLeftColumn(Point pt) {
        pt.x -= EPSILON;
        return getTableHeader().columnAtPoint(pt);
    }

    public void resizeAllColumns() {
        for (int i = 0;i < table.getColumnCount();i++)
            resize(i);
    }

    private void resize(int col) {
        TableColumnModel tcm = table.getColumnModel();
        TableColumn tc = tcm.getColumn(col);
        TableCellRenderer tcr = tc.getHeaderRenderer();
        if (tcr == null)
            tcr = table.getTableHeader().getDefaultRenderer();

        Component comp = tcr.getTableCellRendererComponent(table,tc.getHeaderValue(),false,false,0,col);
        int maxWidth = comp.getPreferredSize().width;

        int ub = table.getRowCount();

        int stepSize = ub / 1000;

        if (stepSize == 0)
            stepSize = 1;

        for (int i = 0;i < ub;i += stepSize) {
            tcr = table.getCellRenderer(i,col);
            Object obj = table.getValueAt(i,col);
            comp = tcr.getTableCellRendererComponent(table,obj,false,false,i,col);
            maxWidth = Math.max(maxWidth, 2 + gap + comp.getPreferredSize().width); // we need to add a gap for lines between cells
        }

        tc.setPreferredWidth(maxWidth); //remembers the value
        tc.setWidth(maxWidth);          //forces layout, repaint
    }
    private JTable table;
    private static final int EPSILON = 5;   //boundary sensitivity
}
