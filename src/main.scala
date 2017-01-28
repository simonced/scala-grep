
import scala.swing._
import scala.swing.TabbedPane


// TODO need to be able to create multiple searches (tabs)
// TODO save last search parameters ? ie: in a simple text file


object main extends SimpleSwingApplication {
	val version = "0.2"

	// main frame constructor
	def top = new MainFrame {
		title = "scala-grep v." + version
		
		// TODO make first TAB
		contents = new TabbedPane {
			pages += new TabbedPane.Page( "Page 1", new grepTab )
			pages += new TabbedPane.Page( "Page 2", new grepTab )
		}
		
		size = new Dimension(800, 600)
		centerOnScreen()
	}
}
