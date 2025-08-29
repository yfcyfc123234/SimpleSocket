package com.yfc.com.yfc.docx.server

data class ResumeServerBean(
    var id: String? = null,

    var name: String? = null,
    var gender: Int = 0,
    var age: Int = 0,
    var photo: String? = null,
    var phone_number: String? = null,
    var email: String? = null,
    var job_experience: Int = 0,

    var job_objective: String? = null,
    var expected_industry: String? = null,
    var expected_salary: String? = null,

    var work: MutableList<ResumeServerWorkBean>? = null,
    var project: MutableList<ResumeServerProjectBean>? = null,
    var education: MutableList<ResumeServerEducationBean>? = null,
    var school: MutableList<ResumeServerSchoolBean>? = null,
    var honor: MutableList<ResumeServerHonorBean>? = null,
    var other: ResumeServerOtherBean? = null,
) {
    companion object {
        fun createDefault() = ResumeServerBean().apply {
            work = mutableListOf()
            project = mutableListOf()
            education = mutableListOf()
            school = mutableListOf()
            honor = mutableListOf()
            other = ResumeServerOtherBean()
        }
    }

    val genderStr get() = if (gender == 1) "男" else "女"
    val ageStr get() = "${age}岁"
    val jobExperienceStr get() = "$job_experience"

    fun createPersonalInfoBean() = ResumeServerPersonalInfoBean().apply {
        name = this@ResumeServerBean.name
        gender = this@ResumeServerBean.gender
        age = this@ResumeServerBean.age
        photo = this@ResumeServerBean.photo
        phone_number = this@ResumeServerBean.phone_number
        email = this@ResumeServerBean.email
        job_experience = this@ResumeServerBean.job_experience
    }

    fun createJobBean() = ResumeServerJobBean().apply {
        job_objective = this@ResumeServerBean.job_objective
        expected_industry = this@ResumeServerBean.expected_industry
        expected_salary = this@ResumeServerBean.expected_salary
    }
}

