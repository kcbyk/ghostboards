
package com.ghostboards

import android.inputmethodservice.InputMethodService
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout

class GhostboardsKeyboardService : InputMethodService() {

    private var isShifted = false

    override fun onCreateInputView(): View {
        val keyboardView = LayoutInflater.from(this)
            .inflate(R.layout.keyboard_view, null) as LinearLayout

        setupButtons(keyboardView)
        return keyboardView
    }

    private fun setupButtons(layout: LinearLayout) {
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i)
            if (child is LinearLayout) {
                setupButtons(child)
            } else if (child is Button) {
                child.setOnClickListener { handleKeyPress(child.tag as String) }
            }
        }
    }

    private fun handleKeyPress(key: String) {
        val inputConnection = currentInputConnection ?: return

        when (key) {
            "delete" -&gt; {
                val selectedText = inputConnection.getSelectedText(0)
                if (selectedText.isNullOrEmpty()) {
                    inputConnection.deleteSurroundingText(1, 0)
                } else {
                    inputConnection.commitText("", 1)
                }
            }
            "space" -&gt; inputConnection.commitText(" ", 1)
            "enter" -&gt; inputConnection.commitText("\n", 1)
            "shift" -&gt; {
                isShifted = !isShifted
                updateKeyLabels()
            }
            "numeric" -&gt; {
                // Numerik klavye için geçici olarak basit tutuyoruz
            }
            else -&gt; {
                val text = if (isShifted) key.uppercase() else key
                inputConnection.commitText(text, 1)
                if (isShifted) {
                    isShifted = false
                    updateKeyLabels()
                }
            }
        }
    }

    private fun updateKeyLabels() {
        val inputView = window?.decorView ?: return
        updateLabelsInLayout(inputView)
    }

    private fun updateLabelsInLayout(view: View) {
        if (view is LinearLayout) {
            for (i in 0 until view.childCount) {
                updateLabelsInLayout(view.getChildAt(i))
            }
        } else if (view is Button) {
            val tag = view.tag as? String
            if (tag != null &amp;&amp; tag.length == 1 &amp;&amp; tag[0].isLetter()) {
                view.text = if (isShifted) tag.uppercase() else tag
            }
        }
    }
}

