//	Shunting yard algorithm demo.
//  Copyright Chris Johnson (http://www.chris-j.co.uk), 2008
//
//	This program is free software: you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation, either version 3 of the License, or
//	(at your option) any later version.
//	
//	This program is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//	
//	You should have received a copy of the GNU General Public License
//	along with this program.  If not, see <http://www.gnu.org/licenses/>.
package shuntingyard;
import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;


public class JScrollLabel extends JScrollPane
{
	static final long serialVersionUID = 1;
	JLabel lab=new JLabel();
	JScrollLabel(Dimension d)
	{
		lab.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		lab.setMaximumSize(new Dimension(300,30));
		lab.setAlignmentX(Component.CENTER_ALIGNMENT);
		lab.setOpaque(true);
		lab.setBackground(Color.WHITE);

		setMaximumSize(d);
		setPreferredSize(d);
		setMinimumSize(d);
		setOpaque(true);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		MouseMotionListener doScrollRectToVisible = new MouseMotionAdapter() {
		     public void mouseDragged(MouseEvent e) {
		         Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
		         ((JLabel)e.getSource()).scrollRectToVisible(r);
		     }
		  };
		lab.addMouseMotionListener(doScrollRectToVisible);
		setBorder(BorderFactory.createLineBorder(new Color(222,222,222)));
		getViewport().add(lab);
	}
	void setText(String arg0)
	{
		lab.setText(arg0);
	}
}
