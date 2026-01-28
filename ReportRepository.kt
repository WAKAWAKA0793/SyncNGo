package com.example.tripshare.data.repo

import com.example.tripshare.data.db.ReportDao
import com.example.tripshare.data.model.ReportEntity


class ReportRepository(private val dao: ReportDao) {
    suspend fun submitReport(report: ReportEntity): Long = dao.insert(report)
    fun reportsFiledBy(userId: Long) = dao.reportsFiledBy(userId)
    fun reportsAgainst(userId: Long) = dao.reportsAgainst(userId)
}
