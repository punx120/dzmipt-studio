package studio.kdb;

import javax.swing.table.AbstractTableModel;

public abstract class KTableModel extends AbstractTableModel {

    public abstract boolean isKey(int column);
    public abstract K.KBaseVector<? extends K.KBase> getColumn(int col);
    public abstract String getColumnName(int col) ;

    public static KTableModel getModel(K.KBase obj) {
        if (obj instanceof K.Flip) {
            return new FlipTableModel((K.Flip) obj);
        }

        if (obj instanceof K.Dict) {
            return new DictTableModel((K.Dict)obj);
        }

        if ((obj instanceof K.KBaseVector) && obj.getType() != 10 && obj.getType() != 4) {
            return new ListModel((K.KBaseVector<? extends K.KBase>)obj);
        }
        return null;
    }

    protected int[] sortIndex = null;
    protected int sorted = 0;
    protected int sortedByColumn = -1;

    public void asc(int col) {
        sortIndex = null;
        K.KBaseVector<? extends K.KBase>  v = getColumn(col);
        sortIndex = v.gradeUp();
        sorted = 1;
        sortedByColumn = col;
    }

    public void desc(int col) {
        sortIndex = null;
        K.KBaseVector<? extends K.KBase> v = getColumn(col);
        sortIndex = v.gradeDown();
        sorted = -1;
        sortedByColumn = col;
    }

    public int getSortByColumn() {
        return sortedByColumn;
    }

    public boolean isSortedAsc() {
        return sorted == 1;
    }

    public boolean isSortedDesc() {
        return sorted == -1;
    }

    public void removeSort() {
        sortIndex = null;
        sorted = 0;
        sortedByColumn = -1;
    }

    public Class getColumnClass(int col) {
        return getColumn(col).getClass();
    }
    //@TODO: should return K.KBase
    public Object getValueAt(int row,int col) {
        row = (sortIndex == null) ? row : sortIndex[row];
        K.KBaseVector<? extends K.KBase> v = getColumn(col);
        return v.at(row);
    }

    public int getRowCount() {
        return getColumn(0).getLength();
    }

}
