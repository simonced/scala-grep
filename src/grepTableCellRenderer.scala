
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.JTable
import java.awt.Color




class grepTableCellRenderer extends DefaultTableCellRenderer {

    override def getTableCellRendererComponent( table: JTable, value: Any, isSelected: Boolean, cellHasFocus: Boolean, row: Int, col: Int ) = {

        val c:java.awt.Component = super.getTableCellRendererComponent( table, value,isSelected, cellHasFocus, row, col )
        if ( row % 2 == 0 ) {
        	// odd lines
            c.setBackground( Color.white )
        }
        else {
        	// even lines
            c.setBackground( Color.decode("#a5f7ff") )
        }

        c
    }

}