
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.JTable
import java.awt.Color




class GrepTableCellRenderer extends DefaultTableCellRenderer {

	val myfocus = false
	
    override def getTableCellRendererComponent( table: JTable, value: Any, isSelected: Boolean, cellHasFocus: Boolean, row: Int, col: Int ) = {

        super.getTableCellRendererComponent( table, value, isSelected, myfocus && cellHasFocus, row, col )

        if( isSelected ) {
        	// selected row
        	setBackground( Color.blue )
        	setForeground( Color.white )
        }
        else if( row % 2 == 0 ) {
        	// odd lines
            setBackground( Color.white )
            setForeground( Color.black )
        }
        else {
        	// even lines
            setBackground( Color.decode("#a5f7ff") )
            setForeground( Color.black )
        }

        this // that's a return
    }

}