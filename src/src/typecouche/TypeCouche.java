package src.typecouche;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import src.colorpicker.ColorPickerButton;
import src.debugger.DebugLog;
import src.graph.GraphContact;
import src.spinner.CustomSpinner;

public class TypeCouche extends JPanel {

	/* auto UID serial Version */
	private static final long serialVersionUID = 1L;
	private Font style = new Font("SansSerif",Font.BOLD,12);

    private JTextField TnomCouche;
    private ColorPickerButton TboutonCouleur;
    private CustomSpinner TRho; 
    private CustomSpinner TVp; 
    private CustomSpinner TVs; 
    private CustomSpinner TPr; 
    /* Default transparency */
    JComboBox<String> TransparenceFluid;
	
	private double Rho;
	private double Vp;
	private double Vs;
	private double Pr;
    private String Nom;
    private double Transp;
    
	private ChangeListener clVp;
	private ChangeListener clVs;
	private ChangeListener clPoisson;
	private ChangeListener clRho;
    
    //Constructor specifying the default values
    public TypeCouche(String name, Color initColor, double rhoDefault, double vpDefault, double vsDefault, double prDefault) {
    	
    	super();
	    TnomCouche = new JTextField(name,6);
	    String[] transparence = {"0 %","10 %","20 %","30 %","50 %","80 %","100 %"};
	    this.TransparenceFluid = new JComboBox<String>(transparence);
	    this.TransparenceFluid.setSelectedIndex(3); //Default transparency
	    this.TransparenceFluid.setSize(25,26);
	    this.TboutonCouleur = new ColorPickerButton(initColor);
	    this.TboutonCouleur.setSize(20,26);
	    
	    TnomCouche.setHorizontalAlignment(JTextField.LEFT);
	    TnomCouche.setFont(style);
	    
	    /* Initialize CustomSpinners */
	    TRho = new CustomSpinner(rhoDefault,0.1d,3d,0.05d,3);
	    TRho.setSize(25,26);
	    TVp = new CustomSpinner(vpDefault,1000d,6000d,100d,0);
	    TVp.setSize(25,26);
	    TVs = new CustomSpinner(vsDefault,500d,4200d,100d,0);
	    TVs.setSize(25,26);
	    TPr = new CustomSpinner(prDefault,0.0d,0.49d,0.01d,3);
	    TPr.setSize(25,26);
	    Rho = rhoDefault;
	    Vp = vpDefault;
	    Vs = vsDefault;
	    Pr = prDefault;
	    
	    // ChangeListener for Vp
	    this.clVp = new ChangeListener () {
	        private final double sqrt2 = Math.sqrt(2d);
	        public void stateChanged(ChangeEvent e) {
	            double vp = TVp.getValeur();
	            double previous_vp = TVp.getPreviousSpinnerValue();
	            double vs = TVs.getValeur();
	            Vp = vp; Vs = vs;
	            if (previous_vp < vp) { //Vp increased
	            	DebugLog.log("Vp increased");
	            	if (vp > TVp.getValMax()) vp = TVp.getValMax();
	            	if (!checkCoherenceVpVs(vs, vp)) {
            			DebugLog.log("Vs Vp not coherent, making coherence between these two values");
            			vs = vp/sqrt2;
            			TVs.removeChangeListener(clVs);
						TVs.setValeur(vs);
						TVs.addChangeListener(clVs);
						Vs = vs;
					} else {
						DebugLog.log("Vs Vp coherent, do nothing");
					}
				} else { //Vp decreased
					DebugLog.log("Vp decreased");
					if (vp < TVp.getValMin()) vp = TVp.getValMin();
					if (!checkCoherenceVpVs(vs, vp)) {
						DebugLog.log("Vs Vp not coherent, making coherence between these two values");
						vs = vp/sqrt2;
            			TVs.removeChangeListener(clVs);
						TVs.setValeur(vs);
						TVs.addChangeListener(clVs);
						Vs = vs;
						TVp.removeChangeListener(clVp);
						TVp.setValeur(vp);
						TVp.addChangeListener(clVp);
						Vp = vp;
					} else {
						DebugLog.log("Vs Vp coherent, fixed Vp, calculate new Vs");
						double resVs = Math.max(vp/sqrt2, TVs.getValMin());
						TVs.removeChangeListener(clVs);
						TVs.setValeur(resVs);
						TVs.addChangeListener(clVs);
						Vs = resVs;			
					}
				}
	            double pr = calculateNewPr(Vs,Vp);
	            TPr.removeChangeListener(clPoisson);
	            TPr.setValeur(pr);
	            TPr.addChangeListener(clPoisson);
	            Pr = pr;
	        }
	    };
	    
	    // ChangeListener for Vs
	    this.clVs = new ChangeListener () {
	        private final double sqrt2 = Math.sqrt(2d);
	        public void stateChanged(ChangeEvent e) {
	            double vp = TVp.getValeur();
	            double vs = TVs.getValeur();
	            double previous_vs = TVs.getPreviousSpinnerValue();
	            Vp = vp; Vs = vs;
	            if (previous_vs < vs) { //Vs increased
					DebugLog.log("Vs increased");
					if (vs > TVs.getValMax()) vs = TVs.getValMax();
					if (!checkCoherenceVpVs(vs, vp)) {
            			DebugLog.log("Vs Vp not coherent, making coherence between these two values");
            			vp = vs*sqrt2;
            			TVp.removeChangeListener(clVp);
						TVp.setValeur(vp);
						TVp.addChangeListener(clVp);
						Vp = vp;
					} else {
						DebugLog.log("Vs Vp coherent, do nothing");
					}
				} else {
					DebugLog.log("Vs decreased");;
	            	if (vs < TVs.getValMin()) vs = TVs.getValMin();
	            	if (!checkCoherenceVpVs(vs, vp)) {
            			DebugLog.log("Vs Vp not coherent, making coherence between these two values");
            			vp = vs*sqrt2;
            			TVp.removeChangeListener(clVp);
						TVp.setValeur(vp);
						TVp.addChangeListener(clVp);
						Vp = vp;
					} else {
						DebugLog.log("Vs Vp coherent, do nothing");
					}
				}
	            double pr = calculateNewPr(Vs,Vp);
	            TPr.removeChangeListener(clPoisson);
	            TPr.setValeur(pr);
	            TPr.addChangeListener(clPoisson);
	            Pr = pr;
	        }
	    };
	
	    // ChangeListener for Poisson
	    this.clPoisson = new ChangeListener () {
	    	private final double sqrt2 = Math.sqrt(2d);
	        public void stateChanged(ChangeEvent e) {
	        	double pr = TPr.getValeur();
	        	double previous_pr = TPr.getPreviousSpinnerValue();
	            double vp = TVp.getValeur();
	            double vs = TVs.getValeur();
	        	if (previous_pr < pr) {
					DebugLog.log("Pr increased");
					double ratio = getVsOverVp(pr);
					double resVp = Math.min(TVp.getValMax(), vs/ratio);
					TVp.removeChangeListener(clVp);
					TVp.setValeur(resVp);
					TVp.addChangeListener(clVp);
					Vp = resVp;
					double resVs = resVp*ratio;
					TVs.setValeur(resVs);
					Vs = resVs;
				} else {
					DebugLog.log("Pr decreased");
					double ratio = getVsOverVp(pr);
					double resVp = vs/ratio;
					TVp.removeChangeListener(clVp);
					TVp.setValeur(resVp);
					TVp.addChangeListener(clVp);
					Vp = resVp;
				}
	        }
	    };
	    
	    // ChangeListener for Rho
	    this.clRho = new ChangeListener () {
	        public void stateChanged(ChangeEvent e) {
	            double rho = TRho.getValeur();
	            Rho = rho;
	        }
	    };
	    
	    TVp.addChangeListener(this.clVp);
	    TVs.addChangeListener(this.clVs);
	    TPr.addChangeListener(this.clPoisson);
	    TRho.addChangeListener(this.clRho);
	    
	    // Manage GUI
	    setLayout(new FlowLayout(FlowLayout.LEFT,5,0));//10
	    setTaille(50,20,50,50,50,50,24);//65
	    add(TnomCouche);
	    add(TRho);
	    add(TVp);
	    add(TVs);
	    add(TPr);
	    add(TboutonCouleur);
	    add(TransparenceFluid);
    }
    
    /* METHODS */
    
    /**
     * Check coherence between vs and vp parameters
     * vs <= vp/srqt(2)
     * @param vs : current vs
     * @param vp : current vp
     * @return true if vs and vp are coherent else false
     */
    public boolean checkCoherenceVpVs(double vs, double vp) {
    	return vs <= vp/Math.sqrt(2d);
    }
    
    /**
     * Calculate newPr with vs and vp as input, vs and vp need to be coherent
     * @param vs : current vs
     * @param vp : current vp
     * @return associated pr or 0 if not coherent negatively ( < 0) or 1 if not coherent positively ( > 1)
     */
    public double calculateNewPr(double vs, double vp) {
    	double ratio = vs/vp;
    	ratio = ratio*ratio;
    	double pr = (0.5d - ratio)/(1d - ratio);
    	if (pr <= 0) pr = 0;
    	if (pr >= 1) pr = 1;
    	return pr;
    }
    
    /**
     * Calculate the ratio vs/vp using pr as inupt
     * @param pr : current poisson ratio pr
     * @return vs/vp
     */
    public double getVsOverVp(double pr) {
    	double ratio = 0.5d*(1-2d*pr)/(1d-pr);
    	return Math.sqrt(ratio);
    }

    /* SETTERS */    
    public void setTransp(double val) {
    	Transp = val;
    }
   
    public void setRho(double val) {
    	TRho.setValeur(val);
    	Rho = val;
    }
    
    public void setVp(double val) {
    	TVp.setValeur(val);
    	Vp = val;
    }
    
    public void setVs(double val) {
    	TVs.setValeur(val);
    	Vs = val;
    }
    
    public void setPr(double val) {
    	TPr.setValeur(val);
    	Pr = val;
    }
    
    public void setNom(String val) {
	    Nom = val;
	    TnomCouche.setText(val);
    }
    
    public void setTaille(int w1,int w2,int w3,int w4, int w5, int w6, int h) {
    	
        this.setMaximumSize(new Dimension(400,h+5));
        TnomCouche.setPreferredSize(new Dimension(w1,h));
        TRho.setPreferredSize(new Dimension(w3,h));
        TVp.setPreferredSize(new Dimension(w4,h));
        TVs.setPreferredSize(new Dimension(w5,h));
        TPr.setPreferredSize(new Dimension(w6,h));
        TboutonCouleur.setPreferredSize(new Dimension(w2,h-5));
    }
    
    public void setParam(double _rho,double _vp,double _vs) {
	    Rho = _rho;
	    Vp = _vp;
	    Vs = _vs;
	    TVp.setValeur(_vp);
	    TVs.setValeur(_vs);
	    TRho.setValeur(_rho);
    }
    
    public void setParam(double _rho,double _vp,double _vs, double _pr) {
    	setParam(_rho, _vp, _vs);
	    Pr = _pr;
	    TPr.setValeur(_pr);
    }
    
    public void setParam(String _nom, double _rho,double _vp,double _vs, double _pr) {
    	setParam(_rho, _vp, _vs, _pr);
    	Nom = _nom;
	    TnomCouche.setText(_nom);
    }
    
    public void reset() {
    	this.TRho.reset();
    	this.TVp.reset();
    	this.TVs.reset();
    	this.TPr.reset();
    	this.TransparenceFluid.setSelectedIndex(3);
    }
    
    /* GETTERS */    
    public Color getColor() {
    	return this.TboutonCouleur.getCouleur();
    }
    
    public double getTransp() {
	    StringTokenizer tk = new StringTokenizer((String)TransparenceFluid.getSelectedItem());
	    Transp = Double.parseDouble(tk.nextToken())*255/100;
	    return Transp;
    }
    
    public double getRho() {
    	Rho = TRho.getValeur();
    	return TRho.getValeur();
    }
    
    public double getVp() {
    	Vp = TVp.getValeur();
        return TVp.getValeur();
    }
    
    public double getVs() {
        Vs = TVs.getValeur();
        return TVs.getValeur();
    }
    
    public double getPr() {
    	Pr = TPr.getValeur();
    	return TPr.getValeur();
    }
        
    public String getNom() {
        return TnomCouche.getText();
    }
    
    /* AddingChangeListener */
    public void addChangeListenerNom(CaretListener ccl) {
        TnomCouche.addCaretListener(ccl);
    }  
    public void addChangeListenerRho(ChangeListener cl) {
        TRho.addChangeListener(cl);
    }
    public void addChangeListenerVp(ChangeListener cl) {
        TVp.addChangeListener(cl);
    }
    public void addChangeListenerVs(ChangeListener cl) {
        TVs.addChangeListener(cl);
    }
    public void addChangeListenerPr(ChangeListener cl) {
        TPr.addChangeListener(cl);
    }
    public void addChangeListenerCouleur(PropertyChangeListener pcl) {
        TboutonCouleur.addPropertyChangeListener(pcl);
    }
    public void addActionListenerTransparence(ActionListener al) {
    	TransparenceFluid.addActionListener(al);
    }

    public double getAttrRho() {
        return Rho;
    }
    public double getAttrVp() {
        return Vp;
    }
    public double getAttrVs() {
        return Vs;
    }
    public String getAttrNom() {
        return Nom;
    } 
    public double getAttrPr() {
    	return Pr;
    }
    public double getIp() {
        return TRho.getValeur()*TVp.getValeur();
    }
    public double getIs() {
        return TRho.getValeur()*TVs.getValeur();
    }
    public ColorPickerButton getColorPickerButton() {
    	return this.TboutonCouleur;
    }
    public Map<String, CustomSpinner> getCustomSpinners() {
		Map<String, CustomSpinner> mapKeyCustomSpinner = new HashMap<>();
		mapKeyCustomSpinner.put("Rho", this.TRho);
		mapKeyCustomSpinner.put("Vp", this.TVp);
		mapKeyCustomSpinner.put("Vs", this.TVs);
		mapKeyCustomSpinner.put("Pr", this.TPr);
		return mapKeyCustomSpinner;
    }
    public double getTransparence() {
    	StringTokenizer tk = new StringTokenizer((String)this.TransparenceFluid.getSelectedItem());
		return Double.parseDouble(tk.nextToken())*255/100;
    }
    
    /* Own changeListener handler */
    public void removeOwnChangeListenerRho() {
    	this.TRho.removeChangeListener(clRho);
    }
    public void addOwnChangeListenerRho() {
    	this.TRho.addChangeListener(clRho);
    }
    public void removeOwnChangeListenerVp() {
    	this.TVp.removeChangeListener(clVp);
    }
    public void addOwnChangeListenerVp() {
    	this.TVp.addChangeListener(clVp);
    }
    public void removeOwnChangeListenerVs() {
    	this.TVs.removeChangeListener(clVs);
    }
    public void addOwnChangeListenerVs() {
    	this.TVs.addChangeListener(clVs);
    }
    public void removeOwnChangeListenerPr() {
    	this.TPr.removeChangeListener(clPoisson);
    }
    public void addOwnChangeListenerPr() {
    	this.TPr.addChangeListener(clPoisson);
    }
        
}

