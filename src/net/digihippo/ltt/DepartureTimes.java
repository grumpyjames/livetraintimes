package net.digihippo.ltt;

public final class DepartureTimes
{
    public static Either<BadTrainState, String> infer(
        String expectedAtTime,
        String scheduledAtTime) {
        // Three cases:
        // On time => scheduled time = estimated time
        // Delayed or Cancelled => exactly that
        // Otherwise, etd is actually a usable time string.
        if ("On time".equals(expectedAtTime)) {
            return Either.right(scheduledAtTime);
        }
        for (BadTrainState badTrainState: BadTrainState.values()) {
            if (badTrainState.name().equals(expectedAtTime)) {
                return Either.left(badTrainState);
            }
        }

        return Either.right(expectedAtTime);
    }

    private DepartureTimes() {}
}
