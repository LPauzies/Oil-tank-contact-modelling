package src.typecouche;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import src.colorpicker.ColorPickerButton;
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
    private Color Couleur;
    private double Transp;

    //Constructor by default
    public TypeCouche(String name, Color initColor) {
    	
    	super();
	    TnomCouche = new JTextField(name,6);
	    String[] transparence = {"0 %","10 %","20 %","30 %","50 %","80 %","100 %"};
	    this.TransparenceFluid = new JComboBox<String>(transparence);
	    this.TransparenceFluid.setSelectedIndex(3); //Default transparency
	    this.TboutonCouleur = new ColorPickerButton(initColor);
	    
	    TnomCouche.setHorizontalAlignment(JTextField.LEFT);
	    TnomCouche.setFont(style);
	    
	    /* Initialize CustomSpinners */
	    TRho = new CustomSpinner(2d,0.1d,3d,0.05d,3);
	    TVp = new CustomSpinner(3000d,100d,6000d,100d,0);
	    TVs = new CustomSpinner(1500d,100d,6000d,100d,0);
	    TPr = new CustomSpinner(0.33d,0.0d,0.5d,0.01d,3);
	    
	    // MousePressed Listener
	    this.addMouseListener(new MouseAdapter () {
	        public void mousePressed(MouseEvent e) {
	            fireFocus();
	        }
	    });
	    
	    // FocusAdapter Listener
	    FocusAdapter fa = new FocusAdapter() {
	        public void focusGained(FocusEvent e) {
	            fireFocus();
	        }
	    };
	    
	    TnomCouche.addFocusListener(fa);
	    TRho.addFocusListener(fa);
	    TVp.addFocusListener(fa);
	    TVs.addFocusListener(fa);
	    TPr.addFocusListener(fa);
	    TransparenceFluid.addFocusListener(fa);
	    
	    // ChangeListener for Vp
	    final ChangeListener clVp = new ChangeListener () {
	        private final double sqrt2 = Math.sqrt(2d);
	        public void stateChanged(ChangeEvent e) {
	            double vp = TVp.getValeur();
	            double vs = TVs.getValeur();
	            Vp = vp; Vs = vs;
	            if (vp < vs*sqrt2) {
	                if (vp/sqrt2 >= TVs.getValMin()) {
	                    TVp.setValeur(vs*sqrt2);
	                	Vp = vs*sqrt2;
	                } else {
	                    TVs.setValeur(TVs.getValMin());
	                    Vs = TVs.getValMin();
	                    TVp.setValeur(TVs.getValMin()*sqrt2);
	                    Vp = TVs.getValMin()*sqrt2;
	                }
	            }
	            double pr = updatePr();
	            TPr.setValeur(pr);
	            Pr = pr;
	            firePropertyChange("Elasticchanged",null,null);  
	        }
	    };
	    
	    TVp.addChangeListener(clVp);
	
	    // ChangeListener for Vs
	    final ChangeListener clVs = new ChangeListener () {
	        private final double sqrt2 = Math.sqrt(2d);
	        public void stateChanged(ChangeEvent e) {
	            double vp = TVp.getValeur();
	            double vs = TVs.getValeur();
	            Vp = vp; Vs = vs;
	            if (vp < vs*sqrt2) {
	                if (vp/sqrt2 >= TVs.getValMin()) {
	                    TVs.setValeur(vp/sqrt2);
	                	Vs = vp/sqrt2;
	                } else {
	                    TVs.setValeur(TVs.getValMin());
	                    TVp.setValeur(TVs.getValMin()*sqrt2);
	                    Vs = TVs.getValMin();
	                    Vp = Vs*sqrt2;                
	                }
	            }
	            double pr = updatePr();
	            TPr.setValeur(pr);
	            Pr = pr;
	            firePropertyChange("Elasticchanged",null,null);
	        }
	    };
	    
	    TVs.addChangeListener(clVs);
	
	    // ChangeListener for Poisson
	    final ChangeListener clPoisson = new ChangeListener () {
	        public void stateChanged(ChangeEvent e) {
	            double vp = TVp.getValeur();
	            double ratio = getVsOverVp();
	            double vs = vp * ratio; 
	            TVs.setValeur(vs);
	            Vs = vs;
	            firePropertyChange("Elasticchanged",null,null);
	        }
	    };
	    
	    TPr.addChangeListener(clPoisson);
	    
	    // ChangeListener for Rho
	    final ChangeListener clRho = new ChangeListener () {
	        public void stateChanged(ChangeEvent e) {
	            double rho = TRho.getValeur();
	            Rho = rho;
	            firePropertyChange("Elasticchanged",null,null);
	        }
	    };
	    
	    TRho.addChangeListener(clRho);
	    
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
    
    //Constructor specifying the default values
    public TypeCouche(String name, Color initColor, double rhoDefault, double vpDefault, double vsDefault, double prDefault) {
    	
    	super();
	    TnomCouche = new JTextField(name,6);
	    String[] transparence = {"0 %","10 %","20 %","30 %","50 %","80 %","100 %"};
	    this.TransparenceFluid = new JComboBox<String>(transparence);
	    this.TransparenceFluid.setSelectedIndex(3); //Default transparency
	    this.TboutonCouleur = new ColorPickerButton(initColor);
	    
	    TnomCouche.setHorizontalAlignment(JTextField.LEFT);
	    TnomCouche.setFont(style);
	    
	    /* Initialize CustomSpinners */
	    TRho = new CustomSpinner(rhoDefault,0.1d,3d,0.05d,3);
	    TVp = new CustomSpinner(vpDefault,100d,6000d,100d,0);
	    TVs = new CustomSpinner(vsDefault,100d,6000d,100d,0);
	    TPr = new CustomSpinner(prDefault,0.0d,0.5d,0.01d,3);
	    Rho = rhoDefault;
	    Vp = vpDefault;
	    Vs = vsDefault;
	    Pr = prDefault;
	    
	    // MousePressed Listener
	    this.addMouseListener(new MouseAdapter () {
	        public void mousePressed(MouseEvent e) {
	            fireFocus();
	        }
	    });
	    
	    // FocusAdapter Listener
	    FocusAdapter fa = new FocusAdapter() {
	        public void focusGained(FocusEvent e) {
	            fireFocus();
	        }
	    };
	    
	    TnomCouche.addFocusListener(fa);
	    TRho.addFocusListener(fa);
	    TVp.addFocusListener(fa);
	    TVs.addFocusListener(fa);
	    TPr.addFocusListener(fa);
	    TransparenceFluid.addFocusListener(fa);
	    
	    // ChangeListener for Vp
	    final ChangeListener clVp = new ChangeListener () {
	        private final double sqrt2 = Math.sqrt(2d);
	        public void stateChanged(ChangeEvent e) {
	            double vp = TVp.getValeur();
	            double vs = TVs.getValeur();
	            Vp = vp; Vs = vs;
	            if (vp < vs*sqrt2) {
	                if (vp/sqrt2 >= TVs.getValMin()) {
	                    TVp.setValeur(vs*sqrt2);
	                	Vp = vs*sqrt2;
	                } else {
	                    TVs.setValeur(TVs.getValMin());
	                    Vs = TVs.getValMin();
	                    TVp.setValeur(TVs.getValMin()*sqrt2);
	                    Vp = TVs.getValMin()*sqrt2;
	                }
	            }
	            double pr = updatePr();
	            TPr.setValeur(pr);
	            Pr = pr;
	            firePropertyChange("Elasticchanged",null,null);  
	        }
	    };
	    
	    TVp.addChangeListener(clVp);
	
	    // ChangeListener for Vs
	    final ChangeListener clVs = new ChangeListener () {
	        private final double sqrt2 = Math.sqrt(2d);
	        public void stateChanged(ChangeEvent e) {
	            double vp = TVp.getValeur();
	            double vs = TVs.getValeur();
	            Vp = vp; Vs = vs;
	            if (vp < vs*sqrt2) {
	                if (vp/sqrt2 >= TVs.getValMin()) {
	                    TVs.setValeur(vp/sqrt2);
	                	Vs = vp/sqrt2;
	                } else {
	                    TVs.setValeur(TVs.getValMin());
	                    TVp.setValeur(TVs.getValMin()*sqrt2);
	                    Vs = TVs.getValMin();
	                    Vp = Vs*sqrt2;                
	                }
	            }
	            double pr = updatePr();
	            TPr.setValeur(pr);
	            Pr = pr;
	            firePropertyChange("Elasticchanged",null,null);
	        }
	    };
	    
	    TVs.addChangeListener(clVs);
	
	    // ChangeListener for Poisson
	    final ChangeListener clPoisson = new ChangeListener () {
	        public void stateChanged(ChangeEvent e) {
	            double vp = TVp.getValeur();
	            double ratio = getVsOverVp();
	            double vs = vp * ratio; 
	            TVs.setValeur(vs);
	            Vs = vs;
	            firePropertyChange("Elasticchanged",null,null);
	        }
	    };
	    
	    TPr.addChangeListener(clPoisson);
	    
	    // ChangeListener for Rho
	    final ChangeListener clRho = new ChangeListener () {
	        public void stateChanged(ChangeEvent e) {
	            double rho = TRho.getValeur();
	            Rho = rho;
	            firePropertyChange("Elasticchanged",null,null);
	        }
	    };
	    
	    TRho.addChangeListener(clRho);
	    
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
    public void fireFocus() {
        firePropertyChange("focusGained",null,this);
    }
    
    protected void firePropertyChange(String prop, Object oldValue ,Object newValue) {
        super.firePropertyChange(prop,oldValue,newValue);
    }
    
    /* SETTERS */
    public void setCouleur(Color _couleur) {
    	Couleur = _couleur;
    }
    
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
    public Color getCouleur() {
    	return Couleur;
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
        
    public double updatePr() {
    	double ratio = TVp.getValeur()/TVs.getValeur();
        ratio *= ratio;
        return (0.5d*ratio-1d)/(ratio-1d);
    }
    
    public double getVsOverVp() {
        double pr = TPr.getValeur();
        double ratio = 0.5d*(1-2d*pr)/(1d-pr);
        return Math.sqrt(ratio);
    }
        
    public String getNom() {
        return TnomCouche.getText();
    }
    
    /* AddingChangeListener */
    private void addChangeListenerNom(CaretListener ccl) {
        TnomCouche.addCaretListener(ccl);
    }  
    private void addChangeListenerRho(ChangeListener cl) {
        TRho.addChangeListener(cl);
    }
    private void addChangeListenerVp(ChangeListener cl) {
        TVp.addChangeListener(cl);
    }
    private void addChangeListenerVs(ChangeListener cl) {
        TVs.addChangeListener(cl);
    }
    private void addChangeListenerPr(ChangeListener cl) {
        TPr.addChangeListener(cl);
    }
    private void addChangeListenerCouleur(PropertyChangeListener pcl) {
        TboutonCouleur.addPropertyChangeListener(pcl);
    }
    
    //Bind all change listeners for a TypeCouche
    public void bindChangeListeners(CaretListener clnom, ChangeListener clrho, ChangeListener clvp, ChangeListener clvs, ChangeListener clpr, PropertyChangeListener pclcolor) {
    	addChangeListenerNom(clnom);
    	addChangeListenerRho(clrho);
    	addChangeListenerVp(clvp);
    	addChangeListenerVs(clvs);
    	addChangeListenerPr(clpr);
    	addChangeListenerCouleur(pclcolor);
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
    public Map<String, CustomSpinner> getCustomSpinners() {
		Map<String, CustomSpinner> mapKeyCustomSpinner = new HashMap<>();
		mapKeyCustomSpinner.put("Rho", this.TRho);
		mapKeyCustomSpinner.put("Vp", this.TVp);
		mapKeyCustomSpinner.put("Vs", this.TVs);
		mapKeyCustomSpinner.put("Pr", this.TPr);
		return mapKeyCustomSpinner;
    }

        
}

