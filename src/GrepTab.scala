import scala.swing.BorderPanel
import scala.swing.BoxPanel
import scala.swing.event.MouseClicked
import scala.swing.Button
import scala.swing.Label
import scala.swing.Table
import scala.swing.ScrollPane
import scala.swing.TextField
import scala.swing.Orientation
import java.awt.Color

import scala.sys.process.Process
import scala.swing.FlowPanel

// TODO add status line in panes
// TODO deal with case sensitivity (check box?)

class GrepTab(val main: String => Unit, val closeAction: () => Unit) extends BorderPanel {

	// width padding for results columns
	val textColPadding = 10

	// === Results Table ===
	// sample: https://gist.github.com/pawelprazak/1348118

	val resultsTable = new Table {
		peer.setShowGrid(false)
		// custom cell color for each line
		peer.setDefaultRenderer(classOf[String], new GrepTableCellRenderer)
		peer.setRowSelectionAllowed(true)
	}

	// some defaults for easier testings
	val defaultSearchDir = "C:/Users/simon/Documents/git/scala-grep/src" // at work
	//val defaultSearchDir = "C:/Users/simon/git/scala-grep/src" // at home
	//val defaultSearchDir = "/Users/simonced/Documents/git/scala-grep/src" // on my Macbook
	val defaultSearchTerm = "def .*"

	// search fields
	val searchWhereInput = new TextField(defaultSearchDir)
	val searchWhatInput = new TextField(defaultSearchTerm)
//		searchInput.columns = 15


	// ========================================
	/**
	 * explanation line under about the search terms
	 */
	def makeExplanationArea: BoxPanel = {
		new BoxPanel(Orientation.Vertical) {
			border = SharedParams.padding
			contents += new Label("Search is using REGEX!") {
				foreground = Color.red
			}
		}
	}


	/**
	 * search area
	 */
	def makeSearchArea: BoxPanel = {
		// search text input (regex)
		val searchWhatLabel = new Label(" What? ")

		// search folder
		val searchWhereLabel = new Label(" Where? ")

		// button
		val searchButton = new Button("Search") {
			listenTo(mouse.clicks)
			reactions += {
				case MouseClicked(_, _, _, _, _) => doSearch
			}
		}


		// the search area itself
		new BoxPanel(Orientation.Horizontal) {
			contents ++= List(searchWhatLabel, searchWhatInput,searchWhereLabel, searchWhereInput, searchButton)
			border = SharedParams.padding
		}
	}


	def doSearch {
		// TODO make the grep flags as parameters?
		val args = Seq("-nHr", searchWhatInput.text, searchWhereInput.text)
//			println( args )
		try {
			val results = Process("grep", args).!!
//			println(results)
			updateResults(searchWhatInput.text, searchWhereInput.text, results.split("\n") )
		}
		catch {
			case e: Exception => {
				println("error with the grep command")
				println("error: " + e)
			}
		}
	}


	/**
	 * update table listing
	 * TODO I am not sure that recreating a new model everytime is the best solution
	 */
	def updateResults(what: String, where:String, lines: Array[String]) {
		val tableModel = new GrepTableModel

		val jtable = resultsTable.peer
		val fm = jtable.getFontMetrics(jtable.getFont)

		// data to adjust column width automatically
		var maxFileNameSize = 0 // count of characters
		var maxLineNumSize = 0 // count of characters
		// loop on data lines
		for(l <- lines) {
			// clear out the search path that is included in the results, that gives shorter file names
			val strippedLine = l.replace(where, "")
			val parts = strippedLine.split(":").map(_.trim) // triming the spaces
			val line:Array[Object] = Array(parts(0), parts(1), parts(2))
			// data size logic
			maxFileNameSize = maxFileNameSize.max( fm.stringWidth(parts(0)) )
			maxLineNumSize = maxLineNumSize.max( fm.stringWidth(parts(1)) )
			// add line to model
			tableModel.addRow(line)
		}

		// set new model
		resultsTable.model = tableModel

		// adapt column size to number of characters max

		val fileColumn = jtable.getColumnModel().getColumn(0)
		val lineColumn = jtable.getColumnModel().getColumn(1)

		// file column size
		fileColumn.setMaxWidth( maxFileNameSize + textColPadding )
		fileColumn.setMinWidth( maxFileNameSize + textColPadding )
		// line number column
		lineColumn.setMaxWidth( maxLineNumSize + textColPadding )

		// send update signal to main
		mainFrame.updateTabTitle( searchWhatInput.text )
	}


	/**
	 * results table element
	 */
	def makeMainTable:ScrollPane = {
		new ScrollPane() {
			contents = resultsTable
		}
	}


	def makeCloseTabButton = {
		new BorderPanel {
			val button = new Button("Close") {
				listenTo(mouse.clicks)
				reactions += {
					case MouseClicked(_, _, _, _, _) => closeAction()
				}
			}
			border = SharedParams.padding
			layout( button ) = BorderPanel.Position.East
		}
	}

	// =====================================================================


	// constructor

		// for the tests, launch the search right away
		//doSearch

	val searchSection = new BoxPanel(Orientation.Vertical) {
		contents += makeCloseTabButton
		contents += makeSearchArea
		contents += makeExplanationArea
	}

	// set the main layou elements of that tab
	layout( searchSection ) = BorderPanel.Position.North
	layout( makeMainTable ) = BorderPanel.Position.Center
}