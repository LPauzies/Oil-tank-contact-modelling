package src.spinner;

import javax.swing.SpinnerNumberModel;

public class MySpinnerNumberModel extends SpinnerNumberModel {
	
    /* auto UID serial Version */
	private static final long serialVersionUID = 1L;
	private double step,valMin,valMax;
    
    public MySpinnerNumberModel (double value, double minimum, double maximum, double stepSize) {
        super(value,minimum,maximum,stepSize);
        step = stepSize;
        valMin = minimum;
        valMax = maximum;
    }

    public Object getNextValue() {
        Double next = (Double)super.getNextValue();
        if (next == null) {
        	return new Double(valMax);
        } else {
            int n = Math.round((float)((next.doubleValue()-valMin)/step));
            return new Double(valMin+n*step);
        }
    }
    
    public Object getPreviousValue() {
        Double previous = (Double)super.getPreviousValue();
        if (previous == null) {
        	return new Double (valMin);
        } else {
            int n = Math.round((float)((previous.doubleValue()-valMin)/step));
            return new Double(valMin+n*step);
        }
    }
}
