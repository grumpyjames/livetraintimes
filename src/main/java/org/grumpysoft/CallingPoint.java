package org.grumpysoft;

public abstract class CallingPoint {

    public abstract void consume(final CallingPointConsumer callingPointConsumer);

    public static CallingPoint singlePoint(final String stationName, final String scheduledAtTime) {
        return new OneCallingPoint(stationName, scheduledAtTime);
    }

    private static class OneCallingPoint extends CallingPoint {
        private final String locationName;
        private final String scheduledAtTime;

        private OneCallingPoint(String locationName, String scheduledAtTime) {
            this.locationName = locationName;
            this.scheduledAtTime = scheduledAtTime;
        }

        @Override
        public void consume(CallingPointConsumer callingPointConsumer) {
            callingPointConsumer.onSinglePoint(locationName, scheduledAtTime);
        }
    }

    private static class Split extends CallingPoint {
        private final CallingPoint callingPointOne;
        private final CallingPoint callingPointTwo;

        private Split(CallingPoint callingPointOne, CallingPoint callingPointTwo) {
            this.callingPointOne = callingPointOne;
            this.callingPointTwo = callingPointTwo;
        }

        @Override
        public void consume(CallingPointConsumer callingPointConsumer) {
            callingPointConsumer.splitStart();
            callingPointOne.consume(callingPointConsumer);
            callingPointTwo.consume(callingPointConsumer);
            callingPointConsumer.splitEnd();
        }
    }

    public interface CallingPointConsumer {
        void onSinglePoint(String stationName, String scheduledAtTime);

        void splitStart();

        void splitEnd();
    }
}
