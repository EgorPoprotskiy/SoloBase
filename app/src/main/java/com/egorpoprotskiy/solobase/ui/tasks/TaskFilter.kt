package com.egorpoprotskiy.solobase.ui.tasks

import androidx.annotation.StringRes
import com.egorpoprotskiy.solobase.R

enum class TaskFilter(@param:StringRes val labelRes: Int) {
    ALL(R.string.task_filter_all),
    ACTIVE(R.string.task_filter_active),
    COMPLETED(R.string.task_filter_completed),
    URGENT(R.string.task_filter_urgent),
    IMPORTANT(R.string.task_filter_important)
}
