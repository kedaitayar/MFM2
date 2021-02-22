package io.github.kedaitayar.mfm.data.podata

import java.time.OffsetDateTime

data class SelectedDate(
    var month: Int = OffsetDateTime.now().month.value,
    var year: Int = OffsetDateTime.now().year
)
