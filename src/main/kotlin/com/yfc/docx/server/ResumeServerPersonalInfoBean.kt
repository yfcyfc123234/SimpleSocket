package com.yfc.docx.server

data class ResumeServerPersonalInfoBean(
    var name: String? = "",
    var gender: Int = 0,
    var age: Int = 0,
    var photo: String? = "",
    var phone_number: String? = "",
    var email: String? = "",
    var job_experience: Int = 0,
) {
    val genderStr get() = if (gender == 1) "男" else "女"
    val ageStr get() = "${age}岁"
    val jobExperienceStr get() = "${job_experience}年"

    fun insertToBean(bean: ResumeServerBean) {
        bean.apply {
            name = this@ResumeServerPersonalInfoBean.name
            gender = this@ResumeServerPersonalInfoBean.gender
            age = this@ResumeServerPersonalInfoBean.age
            photo = this@ResumeServerPersonalInfoBean.photo
            phone_number = this@ResumeServerPersonalInfoBean.phone_number
            email = this@ResumeServerPersonalInfoBean.email
            job_experience = this@ResumeServerPersonalInfoBean.job_experience
        }
    }
}