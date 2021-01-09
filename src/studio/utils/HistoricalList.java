package studio.utils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class HistoricalList<E> extends AbstractList<E> {

    private int depth;
    private List<E> list;

    public HistoricalList() {
        this(Integer.MAX_VALUE);
    }

    public HistoricalList(int depth) {
        this.depth = depth;
        list = new ArrayList<>(depth<Integer.MAX_VALUE ? depth : 0);
    }

    private void ensureCapacity() {
        int count = list.size();
        if (count <=depth) return;;
        list.subList(depth, count).clear();
    }

    public void setDepth(int depth) {
        this.depth = depth;
        ensureCapacity();
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean add(E e) {
        int index = list.indexOf(e);
        if (index > -1) list.remove(index);
        list.add(0, e);
        ensureCapacity();
        return true;
    }

    @Override
    public E remove(int index) {
        return list.remove(index);
    }

    @Override
    public E set(int index, E element) {
        return list.set(index, element);
    }
}
