package com.spacefrontier.game.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.spacefrontier.game.models.PlanetProgress

object PlanetSelector {

    fun show(
        context: Context,
        progress: List<PlanetProgress>,
        currentIndex: Int,
        onPlanetSelected: (Int) -> Unit
    ) {

        val container =
            LinearLayout(context)

        container.orientation =
            LinearLayout.VERTICAL

        container.setPadding(
            24,
            20,
            24,
            20
        )

        container.setBackgroundColor(
            Color.rgb(5, 9, 20)
        )

        val title =
            TextView(context)

        title.text =
            "🪐 WYBÓR PLANETY"

        title.setTextColor(
            Color.WHITE
        )

        title.textSize =
            23f

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
            "Odblokowuj kolejne światy dzięki udanym misjom."

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

            if (selected) {

                card.setBackgroundColor(
                    Color.rgb(
                        20,
                        70,
                        90
                    )
                )

            } else if (unlocked) {

                card.setBackgroundColor(
                    Color.rgb(
                        18,
                        30,
                        48
                    )
                )

            } else {

                card.setBackgroundColor(
                    Color.rgb(
                        12,
                        17,
                        28
                    )
                )
            }

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
                30f

            icon.gravity =
                Gravity.CENTER

            val iconParams =
                LinearLayout.LayoutParams(
                    52,
                    52
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

            val missions =
                TextView(context)

            missions.text =
                if (unlocked) {

                    "Misje ukończone: ${planet.missionsCompleted}"

                } else {

                    "Ukończ poprzednią planetę"
                }

            missions.setTextColor(
                if (unlocked) {
                    Color.LTGRAY
                } else {
                    Color.DKGRAY
                }
            )

            missions.textSize =
                12f

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
                        "ZABLOKOWANA"
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
                11f

            status.setTypeface(
                null,
                Typeface.BOLD
            )

            status.gravity =
                Gravity.CENTER

            card.addView(
                status,
                LinearLayout.LayoutParams(
                    82,
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
                8

            container.addView(
                card,
                cardParams
            )
        }

        val dialog =
            AlertDialog.Builder(context)
                .setView(container)
                .setNegativeButton(
                    "ZAMKNIJ",
                    null
                )
                .create()

        dialog.window?.setBackgroundDrawableResource(
            android.R.color.transparent
        )

        dialog.show()
    }
}
