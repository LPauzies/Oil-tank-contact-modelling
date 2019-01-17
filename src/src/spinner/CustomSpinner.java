package src.spinner;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;

public class CustomSpinner extends JSpinner {
	
    /* auto UID serial Version */
	private static final long serialVersionUID = 1L;
	MySpinnerNumberModel model;
    private double valDef;
    private double valMin;
    private double valMax;
    private double step;
    
    /* Constructor */
    public CustomSpinner(double _valDef, double _valMin , double _valMax,double _step,int precision) {
        super();
        valDef = _valDef;
        valMin = _valMin;
        valMax = _valMax;
        step = _step;
        model = new MySpinnerNumberModel(valDef,valMin,valMax,step);
        this.setModel(model);
        String pattern = "####";
        if (precision > 0) {
            pattern += ".";
            for (int i = 0 ; i < precision ; i++) {pattern+="#";}
        }
        JSpinner.NumberEditor numEdit = new JSpinner.NumberEditor(this, pattern);
        setEditor(numEdit);
        JFormattedTextField tf = ((JSpinner.DefaultEditor)this.getEditor()).getTextField();
        setBorder(BorderFactory.createEtchedBorder());
        tf.setBorder(null);
    }
    
    public double getValeur() {
        Double val = (Double) super.getValue();
        return val.doubleValue();
    }
    
    public void setValeur(double val) {
        super.setValue(new Double(val));
    }
    
    public void reset() {
    	setValeur(valDef);
    }

    public double getValMin() {
    	return valMin;
    }
        
    public double getValMax() {
    	return valMax;
    }
    
    public double getStep() {
        return step;
    }
   
}
