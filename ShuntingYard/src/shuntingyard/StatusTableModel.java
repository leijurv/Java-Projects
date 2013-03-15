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
import java.util.LinkedList;
import javax.swing.table.AbstractTableModel;

class StatusTableModel extends AbstractTableModel
{
		static final long serialVersionUID = 0;
		
		private String[] columnNames;
    	LinkedList<String[]> data = new LinkedList<String[]>();
    	int width;
    	
		StatusTableModel(String[] colNames)
		{
			columnNames=colNames;
			width=columnNames.length;
		}

        public int getColumnCount() {
            return width;
        }

        public int getRowCount() {
            return data.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data.get(row)[col];
        }

        public void setValueAt(String value, int row, int col)
        {
        	String [] tmp = data.get(row);
        	tmp[col] = value;
            data.set(row, tmp);
            fireTableCellUpdated(row, col);
        }
        
        public void addRow(Object [] rowdata)
        {
        	data.addLast((String [])rowdata);
        }
        public void removeRow(int row)
        {
        	data.remove(row);
        	fireTableRowsDeleted(row, row);
        }
        public void clear()
        {
        	fireTableRowsDeleted(0, data.size());
        	data.clear();
        }
   
}