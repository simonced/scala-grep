import java.awt.Dimension
import scala.swing.BorderPanel
import scala.swing.BoxPanel
import scala.swing.Button
import scala.swing.Label
import scala.swing.MainFrame
import scala.swing.Orientation
import scala.swing.ScrollPane
import scala.swing.SimpleSwingApplication
import scala.swing.Table
import scala.swing.TextField
import scala.swing.event.MouseClicked
import scala.sys.process.Process

import javax.swing.border.EmptyBorder
import javax.swing.table.DefaultTableModel
import java.awt.Color


object main extends SimpleSwingApplication {
	val version = "0.2"

	// empty border to space out elements a little
	val padding = new EmptyBorder(5, 5, 5, 5)

	// width padding for results columns
	val textColPadding = 10

	// === Results Table ===
	// sample: https://gist.github.com/pawelprazak/1348118
	val headers = Seq("file", "line", "sample")
	val resultsTable = new Table {
		peer.setShowGrid(false)
		// custom cell color for each line
		peer.setDefaultRenderer(classOf[String], new grepTableCellRenderer)
		peer.setRowSelectionAllowed(true)
	}

	// some defaults for easier testings
	//val defaultSearchDir = "C:/Users/simon/Documents/git/scala-grep/src" // at work
	//val defaultSearchDir = "C:/Users/simon/git/scala-grep/src" // at home
	val defaultSearchDir = "/Users/simonced/Documents/git/scala-grep/src" // on my Macbook
	val defaultSearchTerm = "def .*"

	// search fields
	val searchWhereInput = new TextField(defaultSearchDir)
	val searchWhatInput = new TextField(defaultSearchTerm)
//		searchInput.columns = 15

	// =====================================================================

	// for the tests, launch the search right away
	doSearch

	// main frame constructor
	def top = new MainFrame {
		title = "scala-grep v." + version
		val searchSection = new BoxPanel(Orientation.Vertical) {
			contents += makeSearchArea
			contents += makeExplanationArea
		}
		contents = new BorderPanel() {
			layout( searchSection ) = BorderPanel.Position.North
			layout( makeMainTable ) = BorderPanel.Position.Center
		}
		size = new Dimension(800, 600)
		centerOnScreen()
	}


	/**
	 * explanation line under about the search terms
	 */
	def makeExplanationArea: BoxPanel = {
		new BoxPanel(Orientation.Vertical) {
			border = padding
			contents += new Label("Search is using REGEX!")
			foreground = Color.red // TODO not working
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
			border = padding
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
		val tableModel = new DefaultTableModel

		for(col <- headers) {
			tableModel.addColumn(col)
		}

		val jtable = resultsTable.peer
		val fm = jtable.getFontMetrics(jtable.getFont)

		// data to adjust column width automatically
		var maxFileNameSize = 0 // count of characters
		var maxLineNumSize = 0 // count of characters
		// loop on data lines
		for(l <- lines) {
			// clear out the search path that is included in the results, that gives shorter file names
			val strippedLine= l.replace(where, "")
			val parts = strippedLine.split(":")
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
	}


	/**
	 * results table element
	 */
	def makeMainTable:ScrollPane = {
		new ScrollPane() {
			contents = resultsTable
		}
	}
}

