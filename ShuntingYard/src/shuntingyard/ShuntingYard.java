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
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public class ShuntingYard extends JApplet 
{
	static final long serialVersionUID=0;
	StatusTableModel stm = new StatusTableModel(new String [] {"Infix queue","Token stack","Postfix queue"});
	JTable shuntingTable = new JTable(stm)
	{   
		static final long serialVersionUID = 1;
	    public String getToolTipText(MouseEvent e) 
	    {
	        String tip = "";
	        java.awt.Point p = e.getPoint();
	        int rowIndex = rowAtPoint(p);
	        int colIndex = columnAtPoint(p);
	        if (rowIndex!=-1 && colIndex!=-1) tip = (String) getValueAt(rowIndex, colIndex);
	        return tip;
	    }
	};
	StatusTableModel stm2 = new StatusTableModel(new String [] {"Postfix queue","Token stack"});
	JTable evalTable = new JTable(stm2)
	{   
		static final long serialVersionUID = 1;
	    public String getToolTipText(MouseEvent e) 
	    {
	        String tip = "";
	        java.awt.Point p = e.getPoint();
	        int rowIndex = rowAtPoint(p);
	        int colIndex = columnAtPoint(p);
	        if (rowIndex!=-1 && colIndex!=-1) tip = (String) getValueAt(rowIndex, colIndex);
	        return tip;
	    }
	};
	JButton tokenise=new JButton("↓ Tokenise infix ↓");
	JButton shunt=new JButton("↓ Convert to postfix ↓");
	JButton eval=new JButton("↓ Evaluate postfix ↓");
	JTextField input = new JTextField(80);
	JScrollLabel tokens = new JScrollLabel(new Dimension(350,20));
	JScrollLabel postfix = new JScrollLabel(new Dimension(350,20));
	JScrollLabel result = new JScrollLabel(new Dimension(150,20));
	
	JScrollPane scrollPane1 = new JScrollPane(shuntingTable);
	JScrollPane scrollPane2 = new JScrollPane(evalTable);
	JScrollPane sp; 
	Parser p= new Parser();
	
    @Override
	public void init() 
	{
		String javaVersion = System.getProperty("java.version"); 
		boolean haveJRE16 = true;
		if (javaVersion.compareTo("1.6") < 0) {
                    haveJRE16=false;
                            }
		
			
			
		BoxLayout layout = new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS);
		getContentPane().setBackground(Color.WHITE);
		tokenise.setMaximumSize(new Dimension(170,30));
		tokenise.setAlignmentX(Component.CENTER_ALIGNMENT);
		shunt.setMaximumSize(new Dimension(200,30));
		shunt.setAlignmentX(Component.CENTER_ALIGNMENT);
		eval.setMaximumSize(new Dimension(200,30));
		eval.setAlignmentX(Component.CENTER_ALIGNMENT);
		input.setMaximumSize(new Dimension(350,30));
		input.setToolTipText("Enter an expression in infix format here");

		shuntingTable.setEnabled(false);
		shuntingTable.setGridColor(new Color(222,222,222));
		if (haveJRE16) shuntingTable.setFillsViewportHeight(true);
		shuntingTable.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
		shuntingTable.getColumnModel().getColumn(0).setPreferredWidth(230);
		shuntingTable.getColumnModel().getColumn(1).setPreferredWidth(80);
		shuntingTable.getColumnModel().getColumn(2).setPreferredWidth(190);
		shuntingTable.setRowHeight(14);
		scrollPane1.setPreferredSize(new Dimension(500,70)); 
		evalTable.setEnabled(false);
		evalTable.setGridColor(new Color(222,222,222));
		if (haveJRE16) evalTable.setFillsViewportHeight(true);
		evalTable.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
		evalTable.setRowHeight(14);
		scrollPane2.setPreferredSize(new Dimension(500,70));
		
		tokenise.addActionListener(new ButtonListener() {public void actionPerformed(ActionEvent e) {tokens.setText(p.Tokenise(input.getText()));}});
		shunt.addActionListener(new ButtonListener() {public void actionPerformed(ActionEvent e) {postfix.setText(p.ConvertToPostfix(stm));}});
		eval.addActionListener(new ButtonListener()	{public void actionPerformed(ActionEvent e){result.setText(p.EvaluatePostfix(stm2));}});
		
		int contentSpacing=8;
		getContentPane().setLayout(layout);
		getContentPane().add(input); getContentPane().add(Box.createRigidArea(new Dimension(0,contentSpacing)));
		getContentPane().add(tokenise); getContentPane().add(Box.createRigidArea(new Dimension(0,contentSpacing)));
		getContentPane().add(tokens); getContentPane().add(Box.createRigidArea(new Dimension(0,contentSpacing)));
		getContentPane().add(shunt); getContentPane().add(Box.createRigidArea(new Dimension(0,contentSpacing)));
		getContentPane().add(scrollPane1); getContentPane().add(Box.createRigidArea(new Dimension(0,contentSpacing)));
		getContentPane().add(postfix); getContentPane().add(Box.createRigidArea(new Dimension(0,contentSpacing)));
		getContentPane().add(eval); getContentPane().add(Box.createRigidArea(new Dimension(0,contentSpacing)));
		getContentPane().add(scrollPane2); getContentPane().add(Box.createRigidArea(new Dimension(0,contentSpacing)));
		getContentPane().add(result);
	}
}
