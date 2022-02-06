package common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

//**partial implementation */
public class ObservableList<E> extends ArrayList<E> {
    transient private ArrayList<Consumer<ArrayListEvent>> listeners[];

    public ObservableList() {
        super();
        listeners = new ArrayList[3];
        for (int i = 0; i < listeners.length; i++) {
            listeners[i] = new ArrayList<Consumer<ArrayListEvent>>();
        }
    }

    public ObservableList(Collection<? extends E> c) {
        super(c);
    }

    public static final int ON_SIZE_CHANGED = 0;
    public static final int ON_ADD = 1;
    public static final int ON_REMOVE = 2;

    public void addListener(int type, Consumer<ArrayListEvent> listener) {
        listeners[type].add(listener);
    }

    @Override
    public boolean add(E e) {
        boolean add = super.add(e);
        dispatchEvent(ON_SIZE_CHANGED, size());
        dispatchEvent(ON_ADD, e);
        return add;
    }

    @Override
    public void clear() {
        super.clear();
        dispatchEvent(ON_SIZE_CHANGED, size());
    }

    @Override
    public boolean remove(Object o) {
        boolean remove = super.remove(o);
        dispatchEvent(ON_SIZE_CHANGED, size());
        dispatchEvent(ON_REMOVE, o);
        return remove;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean addAll = super.addAll(c);
        for (E e : c) {
            dispatchEvent(ON_ADD, e);
        }
        dispatchEvent(ON_SIZE_CHANGED, size());
        return addAll;
    }

    private void dispatchEvent(int i, Object eventData) {
        for (Consumer<ArrayListEvent> listener : listeners[i]) {
            ArrayListEvent arrayListEvent = new ArrayListEvent(this, eventData);
            listener.accept(arrayListEvent);
        }
    }
}
