package src;

public class Modele {
	
	/* default values */
	public double thick=20;
    public double dip=25;
    public double ratio=0.5;
    public double Buffer5Hz=0.08;
    public double [] rho = {2.55d,2d,2.25d,2.55d,2.55d};
    public double [] vp = {3200d,2200d,2800d,3000d,3200d};
    public double [] vs = {1600d,1100d,1400d,1500d,1600d};
    public double [] pr = {0.33d,0.33d,0.33d,0.33d,0.33d};
    
	public double TM;
	public double LM;
	public double ZM;
	public double [] ZContact = new double [2];
   
    
    public Modele() {
    	 LM=thick/Math.sin(Math.PI*dip/180d);
    	 LM= LM/ratio ; /* The model's length*/
    	 TM= 2*((LM*Math.tan(Math.PI*dip/180d))/vp[0])+2*(thick/(Math.cos(Math.PI*dip/180d)*vp[1])) + 2*Buffer5Hz ; /*Time*/
    	 ZM= LM*Math.tan(Math.PI*dip/180d) + thick/(Math.cos(Math.PI*dip/180d)) + Buffer5Hz*vp[0]; /*Depth*/
    	 ZContact[0]=ZM/2;
    	 ZContact[1]=ZM/2;
    }

    public void setContact(int i, double val) {
    	ZContact[i]=val;
    }
}
