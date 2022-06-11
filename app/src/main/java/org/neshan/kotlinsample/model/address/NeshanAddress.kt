package org.neshan.kotlinsample.model.address

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NeshanAddress : Serializable {
    // when address is found
    @SerializedName("neighbourhood")
    var neighbourhood: String? = null

    @SerializedName("formatted_address")
    var address: String? = null

    @SerializedName("municipality_zone")
    var municipality_zone: String? = null

    @SerializedName("in_traffic_zone")
    var in_traffic_zone: Boolean? = null

    @SerializedName("in_odd_even_zone")
    var in_odd_even_zone: Boolean? = null

    @SerializedName("city")
    var city: String? = null

    @SerializedName("state")
    var state: String? = null

    // when address is not found
    @SerializedName("status")
    var status: String? = null

    @SerializedName("code")
    var code: Int? = null

    @SerializedName("message")
    var message: String? = null

    constructor(
        neighbourhood: String?,
        address: String?,
        municipality_zone: String?,
        in_traffic_zone: Boolean?,
        in_odd_even_zone: Boolean?,
        city: String?,
        state: String?
    ) {
        this.neighbourhood = neighbourhood
        this.address = address
        this.municipality_zone = municipality_zone
        this.in_traffic_zone = in_traffic_zone
        this.in_odd_even_zone = in_odd_even_zone
        this.city = city
        this.state = state
    }

    constructor(status: String?, code: Int?, message: String?) {
        this.status = status
        this.code = code
        this.message = message
    }
}