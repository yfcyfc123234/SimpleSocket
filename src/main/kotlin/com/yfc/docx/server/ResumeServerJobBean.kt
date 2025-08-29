package com.yfc.docx.server

data class ResumeServerJobBean(
    var expected_industry: String? = "",
    var expected_salary: String? = "",
    var job_objective: String? = "",
) {
    fun insertToBean(bean: ResumeServerBean) {
        bean.apply {
            job_objective = this@ResumeServerJobBean.job_objective
            expected_industry = this@ResumeServerJobBean.expected_industry
            expected_salary = this@ResumeServerJobBean.expected_salary
        }
    }
}