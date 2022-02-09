package common;

import java.util.Random;

public enum Type {
    Sconosciuta(0), Attivazione(1), Aggiornamento(2), Feedback(3);

    public final int i;

    private Type(int i) {
        this.i = i;
    }

    private static final Type[] TYPES = Type.values();
    private static final Random RANDOM = new Random();

    public static Type randomType() {
        return TYPES[RANDOM.nextInt(TYPES.length)];
    }

    public static Type getByValue(int i) {
        for (Type type : TYPES) {
            if (type.i == i) {
                return type;
            }
        }
        throw new IllegalArgumentException();
    }

    public static final Type[] CHOOSABLE_TYPES = new Type[] { Type.Aggiornamento,
            Type.Attivazione, Type.Feedback };
}
