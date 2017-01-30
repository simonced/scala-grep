
import scala.swing._
import scala.swing.TabbedPane
import scala.swing.TabbedPane.Page
import scala.swing.Alignment
import java.awt.FlowLayout
import java.awt.ComponentOrientation


// TODO need to be able to create multiple searches (tabs)
// TODO save last search parameters ? ie: in a simple text file


object mainFrame extends SimpleSwingApplication {
	val version = "0.3"


	val tabs = new TabbedPane {
		// first TAB
		val tab1 = new TabbedPane.Page( "New Search", new grepTab(updateTabTitle) )
		pages += tab1
//			pages += new TabbedPane.Page( "Page 2", new grepTab )
	}


	def makeTopBar = {
		// align that to the right (feels a bit hacky no?)
		new BorderPanel() {
			border = sharedParams.padding
			add(new Button("AddTab"), BorderPanel.Position.East)
			// TODO add button action
		}
	}


	// === main frame constructor ===
	def top = new MainFrame {
		title = "scala-grep v." + version

		contents = new BorderPanel() {
			layout(makeTopBar) = BorderPanel.Position.North
			layout(tabs) = BorderPanel.Position.Center
		}

		size = new Dimension(800, 600)
		centerOnScreen()
	}


	/**
	 * We update the current tab title when a search is launched
	 * @param title:String the new title to reflect the search
	 */
	def updateTabTitle(title:String) {

		val i = tabs.peer.getSelectedIndex
		tabs.peer.setTitleAt(i, title)

	}
}
