package ca.pragmaticcoding.examples.hexeditor

import javafx.application.Application
import javafx.beans.property.BooleanProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.util.Builder
import kotlin.random.Random

class HexEditorModel() {
    val rows: ObservableList<HexRow> = FXCollections.observableArrayList()
}

class HexEditorInteractor(private val hexEditorModel: HexEditorModel) {
    init {
        for (x in 0..500) {
            val fred = Random.Default.nextBytes(16)
            hexEditorModel.rows += HexRow(x * 16, fred)
        }
    }
}

class HexEditorViewBuilder(private val hexEditorModel: HexEditorModel) : Builder<Region> {
    private val cellControlModel = HexCellControlModel()
    override fun build(): Region = BorderPane().apply {
        top = HBox(14.0).apply {
            children += Label("Offset").apply {
                minWidth = 62.0
                alignment = Pos.CENTER_RIGHT
                styleClass += "hex-title"
            }
            children += Label().apply { minWidth = 62.0 }
            for (x in 0..15) {
                children += Label(x.toString()).apply {
                    minWidth = 24.0
                    alignment = Pos.CENTER
                    styleClass += "hex-title"
                }

            }

        }
        center = StackPane().apply {
            children += ListView<HexRow>().apply {
                items = hexEditorModel.rows
                setCellFactory { HexEditorCell(cellControlModel) }
            }
        }

        bottom = HBox(14.0).apply {
            children += boundCheckBox("Octal", cellControlModel.showOctal)
            children += boundCheckBox("Binary", cellControlModel.showBinary)
            children += boundCheckBox("Decimal", cellControlModel.showDecimal)
            children += boundCheckBox("Character", cellControlModel.showCharacter)
            padding = Insets(14.0)
            alignment = Pos.CENTER
        }
    }

    private fun boundCheckBox(text: String, boundProperty: BooleanProperty) = CheckBox(text).apply {
        selectedProperty().bindBidirectional(boundProperty)
    }
}


class HexRow(val offset: Int, var data: ByteArray) {}

class HexEditorController() {
    private val model = HexEditorModel()
    private val interactor = HexEditorInteractor(model)
    private val viewBuilder = HexEditorViewBuilder(model)

    fun getView() = viewBuilder.build()
}

class HexEditorApplication : Application() {
    override fun start(stage: Stage) {
        stage.scene = Scene(HexEditorController().getView(), 840.0, 800.0).apply {
            HexEditorApplication::class.java.getResource("hexeditor.css")?.toString()?.let { stylesheets += it }
        }
        stage.title = "Hexadecimal Editor"
        stage.show()
    }
}

fun main() = Application.launch(HexEditorApplication::class.java)