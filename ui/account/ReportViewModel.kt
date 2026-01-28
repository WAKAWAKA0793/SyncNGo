package com.example.tripshare.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tripshare.data.model.ReportEntity
import com.example.tripshare.data.repo.ReportRepository
import kotlinx.coroutines.launch

class ReportViewModel(private val repo: ReportRepository) : ViewModel() {

    fun submitReport(
        reporterUserId: Long,
        reportedUserId: Long,
        reason: String,
        description: String,
        blockUser: Boolean,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repo.submitReport(
                ReportEntity(
                    reportedUserId = reportedUserId,
                    reporterUserId = reporterUserId,
                    reason = reason,
                    description = description,
                    blockUser = blockUser
                )
            )
            // TODO: if blockUser, persist a block relation (separate table) and apply in UI/queries.
            onDone()
        }
    }
}
