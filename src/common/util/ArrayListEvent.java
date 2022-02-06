package common.util;

import java.util.EventObject;

public class ArrayListEvent extends EventObject {

    private Object eventData;

    public ArrayListEvent(Object source) {
        super(source);
        // TODO Auto-generated constructor stub
    }

    public ArrayListEvent(Object source, Object eventData) {
        super(source);
        this.eventData = eventData;
    }

    public Object getEventData() {
        return eventData;
    }
}
