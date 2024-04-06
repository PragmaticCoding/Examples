package ca.pragmaticcoding.examples.hexeditor

import javafx.beans.property.*
import javafx.beans.value.ObservableBooleanValue
import javafx.beans.value.ObservableValue
import javafx.css.PseudoClass
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.VBox

class HexEditorCell(private val controlModel: HexCellControlModel) : ListCell<HexRow>() {
    private val rowString: StringProperty = SimpleStringProperty("")
    private val offsetString: StringProperty = SimpleStringProperty("")
    private val isEditing: BooleanProperty = SimpleBooleanProperty(false)
    private val editingPos: IntegerProperty = SimpleIntegerProperty(0)
    private val editorFocused: BooleanProperty = SimpleBooleanProperty(false)
    private val minimumWidth: ObservableValue<Double> = controlModel.showBinary.map { if (it) 60.0 else 24.0 }

    companion object {
        val editingPseudoClass: PseudoClass = PseudoClass.getPseudoClass("editing")
    }

    private val layout: Region = HBox(20.0).apply {
        children += Label().apply {
            textProperty().bind(offsetString)
            minWidth = 50.0
            alignment = Pos.CENTER_RIGHT
            styleClass += "hex-title"
        }
        children += VBox(12.0).apply {
            children += HBox(14.0).apply {
                children += VBox(
                    Label("Hex"),
                    hideableLabel(controlModel.showOctal, "Octal"),
                    hideableLabel(controlModel.showDecimal, "Decimal"),
                    hideableLabel(controlModel.showBinary, "Binary"),
                    hideableLabel(controlModel.showCharacter, "Char")
                ).apply {
                    alignment = Pos.TOP_RIGHT
                    minWidth = 60.0
                }
                for (x in 0..15) {
                    children += VBox().apply {
                        children += dataLabel(SimpleBooleanProperty(true)) {
                            extractHex(it, x)?.uppercase() ?: ""
                        }.apply {
                            styleClass += "hex-label"
                            editingPos.subscribe(Runnable {
                                this.pseudoClassStateChanged(
                                    editingPseudoClass,
                                    ((editingPos.value == x) && editorFocused.value)
                                )
                            })
                            isEditing.subscribe(Runnable {
                                this.pseudoClassStateChanged(
                                    editingPseudoClass,
                                    ((editingPos.value == x) && editorFocused.value)
                                )
                            })
                        }
                        children += dataLabel(controlModel.showOctal) { extractRadix(it, x, 8).padStart(3, '0') }
                        children += dataLabel(controlModel.showDecimal) { extractRadix(it, x, 10) }
                        children += dataLabel(controlModel.showBinary) { extractRadix(it, x, 2).padStart(8, '0') }
                        children += dataLabel(controlModel.showCharacter) { extractChar(it, x) }
                        alignment = Pos.TOP_CENTER
                    }
                }
                children += ToggleButton("Edit").apply { selectedProperty().bindBidirectional(isEditing) }
            }
            children += HBox(12.0).apply {
                children += TextField().apply {
                    textProperty().bindBidirectional(rowString)
                    editingPos.bind(caretPositionProperty().map { (it as Int) / 2 })
                    editorFocused.bind(focusedProperty())
                    minWidth = 250.0
                }
                children += Button("Commit").apply {
                    defaultButtonProperty().bind(editorFocused)
                    setOnAction { item.data = rowString.value.decodeHex() }
                }
                visibleProperty().bind(isEditing)
                managedProperty().bind(isEditing)
                alignment = Pos.CENTER
                styleClass += "hex-box"
            }
            styleClass += "hex-box"
        }
        styleClass += "hex-cell"
    }

    private fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }

        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

    private fun dataLabel(visibleBinding: ObservableBooleanValue, mapFunction: (String) -> String) =
        hideableLabel(visibleBinding).apply {
            textProperty().bind(rowString.map { mapFunction(it) })
            minWidthProperty().bind(minimumWidth)
            alignment = Pos.CENTER

        }

    private fun hideableLabel(visibleBinding: ObservableBooleanValue, text: String = "") = Label(text).apply {
        visibleProperty().bind(visibleBinding)
        managedProperty().bind(visibleBinding)
    }

    private fun extractHex(bigString: String, index: Int): String? =
        if (bigString.length > ((index * 2) + 1)) bigString.substring(index * 2, (index * 2) + 2) else null

    @OptIn(ExperimentalStdlibApi::class)
    private fun toRadix(hexString: String, radix: Int) = hexString.hexToInt().toString(radix)

    private fun extractRadix(bigString: String, index: Int, radix: Int) =
        extractHex(bigString, index)?.let { toRadix(it, radix) } ?: ""

    @OptIn(ExperimentalStdlibApi::class)
    private fun extractChar(bigString: String, index: Int) =
        extractHex(bigString, index)?.hexToInt()?.toChar()?.toString() ?: ""


    @OptIn(ExperimentalStdlibApi::class)
    @Override
    public override fun updateItem(hexRow: HexRow?, isEmpty: Boolean) {
        super.updateItem(hexRow, isEmpty)
        if (!isEmpty && (hexRow != null)) {
            offsetString.value = hexRow.offset.toString()
            rowString.value = hexRow.data.toHexString()
            isEditing.value = false
            text = null
            graphic = layout
        } else {
            graphic = null
            text = null
        }
    }
}

class HexCellControlModel() {
    val showOctal: BooleanProperty = SimpleBooleanProperty(true)
    val showBinary: BooleanProperty = SimpleBooleanProperty(false)
    val showDecimal: BooleanProperty = SimpleBooleanProperty(true)
    val showCharacter: BooleanProperty = SimpleBooleanProperty(true)
}