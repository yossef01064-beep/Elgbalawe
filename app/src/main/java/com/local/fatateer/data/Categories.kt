package com.local.fatateer.data

object Categories {
    val spareParts = listOf(
        "IC الصوت",
        "IC TV",
        "المكثفات",
        "الدوائر الكاملة",
        "IC فرتكال",
        "قطع سماعات"
    )

    val sales = listOf(
        "ريموتات",
        "رسيفرات",
        "تلفزيونات",
        "عدسات دش",
        "عدسات رقمية",
        "كابلات",
        "أدابتر 12V",
        "لفات سلاك دش",
        "أطباق دش",
        "فلانشات طبق",
        "إكسسوار دش",
        "بطاريات قلم 1.5V"
    )

    val all = spareParts + sales

    val remoteGroups = listOf("HD", "SD", "تلفزيون")

    val remoteHdBrands = listOf("TIMER", "L/M", "GOTE", "EPG")
    val remoteSdTypes = listOf(
        "صيني صف واحد",
        "صيني بدون صف",
        "صيني 2 صف",
        "تورمان",
        "STRONG",
        "USTRA"
    )
    val remoteTvBrands = listOf("توشيبا", "باناسونيك", "جولدي", "صيني")

    val brandsTv = listOf(
        "توشيبا",
        "صيني",
        "باناسونيك",
        "LG",
        "Gold",
        "Goldstar",
        "أخرى"
    )

    val dishLensTypes = listOf("1 مخرج", "2 مخرج", "4 مخرج")
    val digitalLensBrands = listOf("TIMER", "L/M")
}
