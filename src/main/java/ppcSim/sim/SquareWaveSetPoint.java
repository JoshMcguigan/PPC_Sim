package ppcSim.sim;


public class SquareWaveSetPoint extends AbstractSetPoint {

    private boolean setPointHigh;
    private double timeLastSwitch; // time stamp of simulation last time state was changed
    private double switchTime;


    public SquareWaveSetPoint(SetPointSettings settings){
        super(settings);

        setPointHigh = true;
        timeLastSwitch = 0;
        switchTime = settings.period / 2;


    }

    @Override
    public double getSetPoint(double timeStamp) {

        if ( timeStamp > (timeLastSwitch + switchTime) ){

            timeLastSwitch = timeStamp;

            setPointHigh = !setPointHigh;

        }

        if (setPointHigh){
            return settings.baseSetPoint + settings.range;
        } else {
            return settings.baseSetPoint - settings.range;
        }

    }

}
