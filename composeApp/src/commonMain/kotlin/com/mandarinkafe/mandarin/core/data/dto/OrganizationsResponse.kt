package com.mandarinkafe.mandarin.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrganizationsResponse(
    val organizations: List<Organization>
) : Response()

@Serializable
data class Organization(
    val id: String,
    val name: String,
)
