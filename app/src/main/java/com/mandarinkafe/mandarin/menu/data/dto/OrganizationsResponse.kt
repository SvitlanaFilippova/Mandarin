package com.mandarinkafe.mandarin.menu.data.dto

data class OrganizationsResponse(
    val organizations: List<Organization>
): Response()

data class Organization(
    val id: String,
    val name: String,

)