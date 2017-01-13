import scala.swing._
import javax.swing.border.EmptyBorder
import sys.process._
import scala.swing.event.MouseClicked
import javax.swing.table.DefaultTableModel
import javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS

object main extends SimpleSwingApplication {
	val version = "0.1"

	// empty border to space out elements a little
	val padding = new EmptyBorder(5, 5, 5, 5)

	// sample: https://gist.github.com/pawelprazak/1348118
	val headers = Seq("file", "line", "sample")
	val resultsTable = new Table

	// some defaults for easier testings
	val defaultSearchDir = "C:/Users/simon/Documents/git/scala-grep/src/"
	val defaultSearchTerm = "def .*"


	// main frame constructor
	def top = new MainFrame {
		title = "scala-grep v." + version
		contents = new BoxPanel(Orientation.Vertical) {
			contents ++= List(makeSearchArea, makeExplanationArea, makeMainTable)
		}
		size = new Dimension(800, 400)
		centerOnScreen()
	}


	/**
	 * explanation line under about the search terms
	 */
	def makeExplanationArea: BoxPanel = {
		new BoxPanel(Orientation.Vertical) {
			border = padding
			contents += new Label("Searches are using REGEX!")
		}
	}


	/**
	 * search area
	 */
	def makeSearchArea: BoxPanel = {
		// search text input (regex)
		val searchWhatLabel = new Label(" What? ")
		val searchWhatInput = new TextField(defaultSearchTerm)
//		searchInput.columns = 15

		// search folder
		val searchWhereLabel = new Label(" Where? ")
		val searchWhereInput = new TextField(defaultSearchDir)

		// button
		val searchButton = new Button("Search") {
			listenTo(mouse.clicks)
			reactions += {
				case MouseClicked(_, _, _, _, _) => {
					// idea for launching an external app:
					// http://alvinalexander.com/scala/how-to-execute-external-system-commands-in-scala

					val grepcommand = "grep -nHr \"" + searchWhatInput.text + "\" " + searchWhereInput.text
					try {
						// TODO make the grep flags as parameters?
						val results = Process(grepcommand).lines
						updateResults(searchWhatInput.text, searchWhereInput.text, results)
					}
					catch {
						case e: Exception => {
							println("error with the grep command: " + grepcommand)
							println("error: " + e)
						}
					}

				}
			}
		}


		// the search area itself
		new BoxPanel(Orientation.Horizontal) {
			contents ++= List(searchWhatLabel, searchWhatInput,searchWhereLabel, searchWhereInput, searchButton)
			border = padding
		}
	}


	/**
	 * update table listing
	 * TODO I am not sure that recreating a new model everytime is the best solution
	 */
	def updateResults(what: String, where:String, lines: Stream[String]) {
		val tableModel = new DefaultTableModel

		for(col <- headers) {
			tableModel.addColumn(col)
		}

		// data to adjust column width automatically
		var maxFileNameSize = 0 // count of characters
		// loop on data lines
		for(l <- lines) {
			// clear out the search path that is included in the results, that gives shorter file names
			val strippedLine= l.replace(where, "")
			val parts = strippedLine.split(":")
			val line:Array[Object] = Array(parts(0), parts(1), parts(2))
			// data size logic
			maxFileNameSize = maxFileNameSize.max(parts(0).length)
			// add line to model
			tableModel.addRow(line)
		}

		// set new model
		resultsTable.model = tableModel

		/* sample below not working...
		// adapt column size to number of characters max
		val fileColumn = resultsTable.peer.getColumnModel().getColumn(0)
		// TODO set a dynamic value for characters count?
		println(maxFileNameSize)
		fileColumn.setPreferredWidth( maxFileNameSize * 5 )
		*/

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

