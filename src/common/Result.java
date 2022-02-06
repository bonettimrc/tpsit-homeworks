package common;

import java.util.Random;

public enum Result {
    Failed(0), Succeded(1);

    public final int i;

    private Result(int i) {
        this.i = i;
    }

    private static final Result[] RESULTS = Result.values();
    private static final Random RANDOM = new Random();

    public static Result randomResult() {
        return RESULTS[RANDOM.nextInt(RESULTS.length)];
    }

    public static Result getByValue(int i) {
        for (Result result : RESULTS) {
            if (result.i == i) {
                return result;
            }
        }
        throw new IllegalArgumentException();
    }
}
