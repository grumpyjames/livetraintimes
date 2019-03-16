package net.digihippo.ltt;

public final class DepartureTimes
{
    public static Either<BadTrainState, String> infer(String etd, String std) {
        // Three cases:
        // On time => scheduled time = estimated time
        // Delayed or Cancelled => exactly that
        // Otherwise, etd is actually a usable time string.
        if ("On time".equals(etd)) {
            return Either.right(std);
        }
        for (BadTrainState badTrainState: BadTrainState.values()) {
            if (badTrainState.name().equals(etd)) {
                return Either.left(badTrainState);
            }
        }

        return Either.right(etd);
    }

    private DepartureTimes() {}
}
