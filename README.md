# 🚀 KOSMICZNA PRZYGODA - Space Frontier

Mobilna gra kosmiczna na Androida napisana w **Kotlinie** z systemem TAP-TO-PLAY.

## 🎮 Mechanika gry

Gra opiera się na prostej, ale addictive mechanice **trzech tapnięć**:

```
TAP #1 → Silnik 1 aktywuje się (Stage 1)
TAP #2 → Silnik 2 aktywuje się (Stage 2)  
TAP #3 → Silnik 3 aktywuje się (Stage 3)
↓
Rakieta automatycznie przyspiesza i leci
↓
Automatyczne lądowanie na wybranej planecie
↓
Zdobywasz monety za udane lądowanie ✅
```

## 🌍 Planety do odkrywania

| Planeta | Cel Wysokości | Min-Max | Monety |
|---------|--------------|---------|---------|
| 🌍 Ziemia | 0.1 km | 0.05-0.15 km | 10 |
| 🌙 Księżyc | 1 km | 0.8-1.2 km | 25 |
| 🔴 Mars | 2 km | 1.8-2.2 km | 50 |
| 🪐 Jowisz | 5 km | 4.5-5.5 km | 100 |
| 🌐 Saturn | 10 km | 9-11 km | 200 |

## 📊 Fizyka gry

- **Prędkość**: Każdy silnik dodaje ~600 km/h + bonus
- **Wysokość**: Rośnie proporcjonalnie do prędkości
- **Opór powietrza**: Poniżej 10km prędkość powoli spada
- **Paliwo**: Spada o 30% za każdy aktywowany silnik

## 🏗️ Struktura projektu

```
app/src/main/
├── kotlin/com/spacefrontier/game/
│   ├── models/
│   │   ├── Rocket.kt          # Stan rakiety
│   │   ├── Planet.kt          # Definicje planet
│   │   └── GameState.kt       # Stan całej gry
│   ├── logic/
│   │   └── GameEngine.kt      # Silnik gry (fizyka, logika)
│   └── ui/
│       └── MainActivity.kt    # UI i obsługa tapnięć
└── res/
    ├── layout/
    │   └── activity_main.xml  # Layout gry
    └── values/
        └── strings.xml        # Zasoby tekstowe
```

## 🔧 Technologia

- **Język**: Kotlin
- **Min SDK**: 24
- **Target SDK**: 34
- **Framework**: Android App Compat

## 🎯 Planowane funkcje

- [ ] Animacje 3D rakiety (OpenGL/Vulkan)
- [ ] Efekty dźwiękowe
- [ ] Particle effects (dym, ogień)
- [ ] Leaderboard
- [ ] Zakup ulepszeń (lepsze silniki)
- [ ] Różne tryby gry
- [ ] Multiplayer

## 🚀 Jak uruchomić

1. Sklonuj repozytorium
2. Otwórz w Android Studio
3. Uruchom na emulatorze lub urządzeniu fizycznym
4. Ciesz się grą!

---

**Stworzono przez: [@royall1510s-pixel](https://github.com/royall1510s-pixel)**
