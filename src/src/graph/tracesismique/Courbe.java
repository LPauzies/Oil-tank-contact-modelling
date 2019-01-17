package src.graph.tracesismique;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;


public interface Courbe
{
	public void reset();
	
	public void addPoint(double x,double y);
	
	public void dessiner(Graphics2D g,AffineTransform transf);
	
	public boolean isEmpty();
	
	public void close ();
	
	public boolean isClosed();
	
	public void setCouleur(Color _couleur);
	
	public Color getCouleur();
	
	public void setLegende(String _legende);
	
	public String getLegende();
}
