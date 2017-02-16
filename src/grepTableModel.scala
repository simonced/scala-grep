import javax.swing.table.DefaultTableModel


class grepTableModel extends DefaultTableModel {

	val headers = Seq("file", "line", "sample")

	// init headesr
	for(col <- headers) {
		addColumn(col)
	}


	override def isCellEditable(row: Int, col: Int) = false

}