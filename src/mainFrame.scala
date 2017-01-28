
import scala.swing._
import scala.swing.TabbedPane
import scala.swing.TabbedPane.Page


// TODO need to be able to create multiple searches (tabs)
// TODO save last search parameters ? ie: in a simple text file


object mainFrame extends SimpleSwingApplication {
	val version = "0.3"


	val tabs = new TabbedPane {
		// first TAB
		val tab1 = new TabbedPane.Page( "Search", new grepTab(updateTabTitle) )
		pages += tab1
//			pages += new TabbedPane.Page( "Page 2", new grepTab )
	}


	// === main frame constructor ===
	def top = new MainFrame {
		title = "scala-grep v." + version

		contents = tabs

		size = new Dimension(800, 600)
		centerOnScreen()
	}


	def updateTabTitle(title:String) {

		val i = tabs.peer.getSelectedIndex
		tabs.peer.setTitleAt(i, title)

	}
}
