package common;

import java.util.Random;

public enum Category {
    Sconosciuta(0), Gestionale(1), Packaging(2);

    public final int i;

    private Category(int i) {
        this.i = i;
    }

    private static final Category[] CATEGORIES = Category.values();
    private static final Random RANDOM = new Random();

    public static Category randomCategory() {
        return CATEGORIES[RANDOM.nextInt(CATEGORIES.length)];
    }

    public static Category getByValue(int i) {
        for (Category category : CATEGORIES) {
            if (category.i == i) {
                return category;
            }
        }
        throw new IllegalArgumentException();
    }
}
