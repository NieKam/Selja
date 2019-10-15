package io.selja.seljabackend.model

import javax.validation.constraints.*

data class NewAdItem(
        @get:NotEmpty(message = "Please provide a deviceId")
        val deviceId: String,

        @get:NotEmpty(message = "Please provide a name")
        @get:Size(max = 55, message = "Name cannot be longer than 55 characters")
        val name: String,

        @get:Size(min = 0, message = "Description cannot be empty")
        val description: String,

        @get:Size(min = 4, max = 25, message = "Phone number cannot be shorter than 3 digits and longer than 25")
        val phone: String,

        @get:DecimalMin("0.00", message = "Price must be greater than or equal to 0.00")
        val price: Double,

        @get:NotNull(message = "Please provide a validation")
        @get:DecimalMin("1.00", message = "validFor must be greater than zero")
        @get:DecimalMax("604800000", message = "validFor cannot be greater than 7 days") // 7days in ms
        val validFor: Long,

        @get:NotNull(message = "Please provide valid location")
        val lat: Double,

        @get:NotNull(message = "Please provide valid location")
        val long: Double
)

fun NewAdItem.toAdItem() = AdItem(
        deviceId = this.deviceId,
        name = this.name,
        description = this.description,
        price = this.price,
        phone = this.phone,
        phoneObfuscated = this.phone.obfuscate(),
        location = Location(lat = this.lat, long = this.long),
        validUntil = System.currentTimeMillis() + this.validFor
)

fun String.obfuscate(): String {
    val mask = "****"
    if (this.length < 4) {
        return mask
    }

    val phoneNonSpace = this.replace("\\s+", "")
    return phoneNonSpace.replaceRange(3, phoneNonSpace.length - 1, mask)
}