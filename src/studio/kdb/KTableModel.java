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

    protected int[] index;
    protected int sorted = 0;
    protected int sortedByColumn = -1;

    protected KTableModel(int rowCount) {
        index = new int[rowCount];
        initIndex();
    }

    private void initIndex() {
        for (int i = 0; i< this.index.length; i++) {
            index[i] = i;
        }
    }

    public int[] getIndex() {
        return index;
    }

    public void asc(int col) {
        K.KBaseVector<? extends K.KBase>  v = getColumn(col);
        index = v.gradeUp();
        sorted = 1;
        sortedByColumn = col;
    }

    public void desc(int col) {
        K.KBaseVector<? extends K.KBase> v = getColumn(col);
        index = v.gradeDown();
        sorted = -1;
        sortedByColumn = col;
    }

    public int getSortByColumn() {
        return sortedByColumn;
    }

    public boolean isSortedAsc(int column) {
        return sorted == 1 && sortedByColumn == column;
    }

    public boolean isSortedDesc(int column) {
        return sorted == -1 && sortedByColumn == column;
    }

    public void removeSort() {
        initIndex();
        sorted = 0;
        sortedByColumn = -1;
    }

    public Class getColumnClass(int col) {
        return getColumn(col).getClass();
    }
    //@TODO: add separate method which return K.KBase
    public Object getValueAt(int row,int col) {
        row = index[row];
        K.KBaseVector<? extends K.KBase> v = getColumn(col);
        return v.at(row);
    }

    public int getRowCount() {
        return getColumn(0).getLength();
    }

}
