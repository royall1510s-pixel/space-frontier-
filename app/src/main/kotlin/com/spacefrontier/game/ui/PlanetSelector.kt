package com.spacefrontier.game.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.spacefrontier.game.models.PlanetProgress

object PlanetSelector {

    fun show(
        context: Context,
        progress: List<PlanetProgress>,
        currentIndex: Int,
        onPlanetSelected: (Int) -> Unit
    ) {

        val scrollView =
            ScrollView(context)

        val container =
            LinearLayout(context)

        container.orientation =
            LinearLayout.VERTICAL

        container.setPadding(
            20,
            18,
            20,
            18
        )

        container.setBackgroundColor(
            Color.rgb(5, 9, 20)
        )

        scrollView.addView(
            container
        )

        val title =
            TextView(context)

        title.text =
            "🪐  WYBÓR PLANETY"

        title.setTextColor(
            Color.WHITE
        )

        title.textSize =
            24f

        title.gravity =
            Gravity.CENTER

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        container.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val subtitle =
            TextView(context)

        subtitle.text =
            "Wybierz świat dla swojej następnej misji."

        subtitle.setTextColor(
            Color.LTGRAY
        )

        subtitle.textSize =
            13f

        subtitle.gravity =
            Gravity.CENTER

        val subtitleParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        subtitleParams.topMargin =
            6

        container.addView(
            subtitle,
            subtitleParams
        )

        progress.forEachIndexed {
                index,
                planet ->

            val card =
                LinearLayout(context)

            card.orientation =
                LinearLayout.HORIZONTAL

            card.gravity =
                Gravity.CENTER_VERTICAL

            card.setPadding(
                14,
                14,
                14,
                14
            )

            val unlocked =
                planet.unlocked

            val selected =
                index == currentIndex

            val background =
                GradientDrawable()

            background.cornerRadius =
                18f

            when {

                selected -> {

                    background.setColor(
                        Color.rgb(
                            20,
                            75,
                            95
                        )
                    )

                    background.setStroke(
                        3,
                        Color.rgb(
                            85,
                            255,
                            170
                        )
                    )
                }

                unlocked -> {

                    background.setColor(
                        Color.rgb(
                            17,
                            31,
                            50
                        )
                    )

                    background.setStroke(
                        2,
                        Color.rgb(
                            45,
                            100,
                            135
                        )
                    )
                }

                else -> {

                    background.setColor(
                        Color.rgb(
                            12,
                            17,
                            28
                        )
                    )

                    background.setStroke(
                        2,
                        Color.rgb(
                            45,
                            50,
                            60
                        )
                    )
                }
            }

            card.background =
                background

            val planetEmoji =
                when (index) {

                    0 -> "🌙"

                    1 -> "🔴"

                    2 -> "🔵"

                    3 -> "🟠"

                    4 -> "🔵"

                    else -> "🪐"
                }

            val icon =
                TextView(context)

            icon.text =
                if (unlocked) {
                    planetEmoji
                } else {
                    "🔒"
                }

            icon.textSize =
                32f

            icon.gravity =
                Gravity.CENTER

            val iconParams =
                LinearLayout.LayoutParams(
                    58,
                    58
                )

            card.addView(
                icon,
                iconParams
            )

            val info =
                LinearLayout(context)

            info.orientation =
                LinearLayout.VERTICAL

            val infoParams =
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )

            infoParams.marginStart =
                12

            val name =
                TextView(context)

            name.text =
                when {

                    selected ->
                        "✅ ${planet.planetName}"

                    unlocked ->
                        planet.planetName

                    else ->
                        "🔒 ${planet.planetName}"
                }

            name.setTextColor(
                if (unlocked) {
                    Color.WHITE
                } else {
                    Color.GRAY
                }
            )

            name.textSize =
                18f

            name.setTypeface(
                null,
                Typeface.BOLD
            )

            info.addView(
                name
            )

            val description =
                TextView(context)

            description.text =
                when (index) {

                    0 ->
                        "Baza wypraw • pierwszy świat"

                    1 ->
                        "Czerwona planeta • trudniejsze misje"

                    2 ->
                        "Lodowy księżyc • ekstremalne warunki"

                    3 ->
                        "Gigantyczny księżyc • daleka wyprawa"

                    4 ->
                        "Lodowy gigant • najwyższe wyzwanie"

                    else ->
                        "Nieznany świat"
                }

            description.setTextColor(
                if (unlocked) {
                    Color.LTGRAY
                } else {
                    Color.DKGRAY
                }
            )

            description.textSize =
                11f

            val descriptionParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            descriptionParams.topMargin =
                4

            info.addView(
                description,
                descriptionParams
            )

            val missions =
                TextView(context)

            missions.text =
                if (unlocked) {

                    "🏆 Ukończone misje: " +
                            planet.missionsCompleted

                } else {

                    "🔒 Ukończ poprzednią planetę"
                }

            missions.setTextColor(
                if (unlocked) {
                    Color.rgb(
                        180,
                        210,
                        225
                    )
                } else {
                    Color.DKGRAY
                }
            )

            missions.textSize =
                11f

            val missionsParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            missionsParams.topMargin =
                4

            info.addView(
                missions,
                missionsParams
            )

            card.addView(
                info,
                infoParams
            )

            val status =
                TextView(context)

            status.text =
                when {

                    selected ->
                        "AKTYWNA"

                    unlocked ->
                        "WYBIERZ"

                    else ->
                        "ZAMKNIĘTA"
                }

            status.setTextColor(
                when {

                    selected ->
                        Color.rgb(
                            85,
                            255,
                            170
                        )

                    unlocked ->
                        Color.rgb(
                            80,
                            190,
                            255
                        )

                    else ->
                        Color.DKGRAY
                }
            )

            status.textSize =
                10f

            status.setTypeface(
                null,
                Typeface.BOLD
            )

            status.gravity =
                Gravity.CENTER

            card.addView(
                status,
                LinearLayout.LayoutParams(
                    72,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )

            if (unlocked) {

                card.setOnClickListener {

                    onPlanetSelected(
                        index
                    )
                }
            }

            val cardParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            cardParams.topMargin =
                10

            container.addView(
                card,
                cardParams
            )
        }

        val footer =
            TextView(context)

        footer.text =
            "🚀 Udane misje odblokowują kolejne światy."

        footer.setTextColor(
            Color.rgb(
                120,
                150,
                170
            )
        )

        footer.textSize =
            11f

        footer.gravity =
            Gravity.CENTER

        val footerParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        footerParams.topMargin =
            14

        footerParams.bottomMargin =
            4

        container.addView(
            footer,
            footerParams
        )

        val dialog =
            AlertDialog.Builder(context)
                .setView(scrollView)
                .setNegativeButton(
                    "ZAMKNIJ",
                    null
                )
                .create()

        dialog.setOnShowListener {

            dialog.window?.setBackgroundDrawableResource(
                android.R.color.transparent
            )
        }

        dialog.show()
    }
}
