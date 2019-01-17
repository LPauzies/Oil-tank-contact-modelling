package src.graph.tracesismique;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public interface ObjetGraphique
{
	public void dessiner(Graphics2D g,AffineTransform transf);
	
	public String getLegende();
	
	public void setLegende(String _legende);
}

