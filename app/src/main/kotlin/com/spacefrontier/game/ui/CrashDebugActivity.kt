package com.spacefrontier.game.ui

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.ScrollView
import android.widget.TextView
import com.spacefrontier.game.databinding.ActivityMainBinding
import com.spacefrontier.game.logic.GameEngine

class CrashDebugActivity : Activity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        val textView =
            TextView(this)

        textView.setTextColor(
            Color.WHITE
        )

        textView.setBackgroundColor(
            Color.rgb(
                5,
                9,
                20
            )
        )

        textView.setPadding(
            24,
            40,
            24,
            40
        )

        textView.textSize =
            15f

        textView.gravity =
            Gravity.TOP

        val scrollView =
            ScrollView(this)

        scrollView.addView(
            textView
        )

        setContentView(
            scrollView
        )

        try {

            textView.text =
                "TEST 1/3\n\n" +
                "Uruchamiam ActivityMainBinding..."

            val binding =
                ActivityMainBinding.inflate(
                    layoutInflater
                )

            textView.text =
                "TEST 2/3\n\n" +
                "activity_main.xml OK.\n\n" +
                "Uruchamiam GameEngine..."

            val engine =
                GameEngine(this)

            textView.text =
                "TEST 3/3\n\n" +
                "GameEngine OK.\n\n" +
                "DIAGNOSTYKA ZAKOŃCZONA.\n\n" +
                "Problem prawdopodobnie znajduje się " +
                "w MainActivity po uruchomieniu."

        } catch (
            exception: Throwable
        ) {

            val stack =
                android.util.Log
                    .getStackTraceString(
                        exception
                    )

            textView.text =
                "🔴 SPACE FRONTIER — BŁĄD STARTU\n\n" +
                "Typ błędu:\n" +
                "${exception.javaClass.name}\n\n" +
                "Komunikat:\n" +
                "${exception.message}\n\n" +
                "STACK TRACE:\n\n" +
                stack
        }
    }
}
