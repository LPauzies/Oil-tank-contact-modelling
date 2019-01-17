package src.model;

import java.awt.Color;
import java.util.Vector;

import src.typecouche.TypeCouche;

public class Model {
	
    public String[] labels;
    public Color[] colors;
    public double[] rho;
    public double[] vp;
    public double[] vs;
    public double[] pr;
    
	public double thick;
    public double dip;
    public double ratio;
    public double Buffer5Hz;
    
	public double TM;
	public double LM;
	public double ZM;
	public double[] ZContact;
    
    public Model(String[] labels, Color[] colors, double[] rho, double[] vp, double[] vs, double[] pr) {
    	this.labels=labels;
    	this.colors=colors;
    	this.rho=rho;
    	this.vp=vp;
    	this.vs=vs;
    	this.pr=pr;
    }
    
    public Vector<TypeCouche> generateTypeCouche() {
    	
    	Vector<TypeCouche> typeCouches = new Vector<>();
    	int nbTypeCouches = labels.length;
    	if (nbTypeCouches == rho.length && nbTypeCouches == vp.length && nbTypeCouches == vs.length && nbTypeCouches == pr.length) {
    		for (int i = 0; i < nbTypeCouches; i++) {
    			typeCouches.add(new TypeCouche(labels[i], colors[i], rho[i], vp[i], vs[i], pr[i]));
			}
		}
    	return typeCouches;
    }
	
}
