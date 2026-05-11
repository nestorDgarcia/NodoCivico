package com.nestorgarcia.nodocivico.data.local

import androidx.room.TypeConverter
import com.nestorgarcia.nodocivico.model.Priority
import com.nestorgarcia.nodocivico.model.ReportStatus

class Converters {

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)

    @TypeConverter
    fun fromReportStatus(status: ReportStatus): String = status.name

    @TypeConverter
    fun toReportStatus(value: String): ReportStatus = ReportStatus.valueOf(value)
}