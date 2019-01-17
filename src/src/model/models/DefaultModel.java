package src.model.models;

import java.awt.Color;

import src.model.IModel;
import src.model.Model;

public class DefaultModel extends Model implements IModel {
	
	public DefaultModel() {
		super(initLabelsTypeCouche(), initColors(), initRho(), initVp(), initVs(), initPr());
		initDefaultValues();
		initModel();
	}
	
	public static double[] initPr() {
		double[] pr = {0.33d,0.33d,0.33d,0.33d,0.33d};	
		return pr;
	}

	public static double[] initVs() {
		double[] vs = {1600d,1100d,1400d,1500d,1600d};
		return vs;
	}

	public static double[] initVp() {
		double[] vp = {3200d,2200d,2800d,3000d,3200d};
		return vp;
	}

	public static double[] initRho() {
		double[] rho = {2.55d,2d,2.25d,2.55d,2.55d};
		return rho;
	}

	public static Color[] initColors() {
		Color[] colors = {Color.GRAY,Color.RED,Color.GREEN,Color.BLUE,Color.GRAY};
		return colors;
	}
	
	public static String[] initLabelsTypeCouche() {
		String[] labels = {"Seal", "HC Sand", "Brine Sand", "Other", "Seal"};
		return labels;
	}

	@Override
	public void initDefaultValues() {
		this.thick = 20;
		this.dip = 25;
		this.ratio = 0.5;
		this.Buffer5Hz = 0.08;
	}
	
	@Override
	public void initModel() {
	   	LM=thick/Math.sin(Math.PI*dip/180d);
	   	LM= LM/ratio ; /* The model's length*/
	   	TM= 2*((LM*Math.tan(Math.PI*dip/180d))/vp[0])+2*(thick/(Math.cos(Math.PI*dip/180d)*vp[1])) + 2*Buffer5Hz ; /*Time*/
	   	ZM= LM*Math.tan(Math.PI*dip/180d) + thick/(Math.cos(Math.PI*dip/180d)) + Buffer5Hz*vp[0]; /*Depth*/
	   	ZContact = new double[2];
	   	for (int i = 0; i < ZContact.length; i++) {
			ZContact[i] = ZM/2;
		}
	}
	
    public void setContact(int i, double val) {
    	if (i >= ZContact.length || i < 0) {
    		ZContact[i]=val;
		}
    }

}
