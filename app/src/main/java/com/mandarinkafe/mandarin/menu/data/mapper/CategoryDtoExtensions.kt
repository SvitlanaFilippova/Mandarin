package com.mandarinkafe.mandarin.menu.data.mapper

import com.mandarinkafe.mandarin.menu.data.dto.CategoryDto

fun CategoryDto.hasParent(): Boolean =
    name.contains("/")

fun CategoryDto.parentName(): String =
    name.substringBefore("/")

fun CategoryDto.subName(): String =
    name.substringAfter("/")