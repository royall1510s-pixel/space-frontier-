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
import com.spacefrontier.game.graphics.SpaceMapView
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
            16,
            16,
            16,
            16
        )

        container.setBackgroundColor(
            Color.rgb(
                5,
                9,
                20
            )
        )

        scrollView.addView(
            container
        )

        val title =
            TextView(context)

        title.text =
            "🌌  MAPA KOSMOSU"

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
            "Wybierz odblokowany świat dla swojej następnej misji."

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
            5

        container.addView(
            subtitle,
            subtitleParams
        )

        val mapView =
            SpaceMapView(context)

        val mapParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                430
            )

        mapParams.topMargin =
            10

        mapView.setData(
            progress = progress,
            currentPlanetIndex = currentIndex,
            selectedPlanetIndex = currentIndex
        ) { index ->

            onPlanetSelected(
                index
            )
        }

        container.addView(
            mapView,
            mapParams
        )

        val listTitle =
            TextView(context)

        listTitle.text =
            "🪐  DOSTĘPNE ŚWIATY"

        listTitle.setTextColor(
            Color.WHITE
        )

        listTitle.textSize =
            18f

        listTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        val listTitleParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        listTitleParams.topMargin =
            12

        listTitleParams.bottomMargin =
            4

        container.addView(
            listTitle,
            listTitleParams
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

            val emoji =
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
                    emoji
                } else {
                    "🔒"
                }

            icon.textSize =
                30f

            icon.gravity =
                Gravity.CENTER

            card.addView(
                icon,
                LinearLayout.LayoutParams(
                    55,
                    55
                )
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
                    Color.LTGRAY
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
                5

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
                9

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

        dialog.show()

        dialog.window?.setBackgroundDrawableResource(
            android.R.color.transparent
        )
    }
}
