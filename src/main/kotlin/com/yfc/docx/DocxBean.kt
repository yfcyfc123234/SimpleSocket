package com.yfc.com.yfc.docx

import com.google.gson.Gson
import com.yfc.com.yfc.docx.server.ResumeServerBean

data class DocxBean(
    var ac: MutableList<DocxACBean>? = null,
    var group: MutableList<DocxGroupBean>? = null,
) {
    companion object {
        private const val TEST_JSON =
            "{\"age\":20,\"education\":[{\"degree\":\"本科\",\"description_str\":\"1.担任学生会主席，组织多次校园活动\\n2.参与开源项目开发，负责前端模块\\n3.获得校级一等奖学金三次\",\"eid\":\"26\",\"end_time\":\"2022-06-01\",\"major_name\":\"计算机科学与技术\",\"school_name\":\"北京大学\",\"start_time\":\"2018-09-01\"}],\"email\":\"15222222222@qq.com\",\"expected_industry\":\"互联网\",\"expected_salary\":\"1008611\",\"gender\":1,\"honor\":[{\"award_time\":\"2021-09-01\",\"hid\":\"22\",\"honor_name\":\"年度优秀员工\"}],\"id\":\"18\",\"job_experience\":4,\"job_objective\":\"android开发\",\"name\":\"测试姓名\",\"other\":{\"activity\":\"大学期间担任学生会主席，组织过校园文化节、迎新晚会等大型活动；参与社区志愿服务累计200小时\",\"certificate\":\"大学英语六级（CET-6）、计算机二级、驾驶证C1、PMP项目管理认证\",\"hobby\":\"阅读、篮球、旅行、摄影，尤其擅长风光摄影，作品曾在校园比赛中获奖\",\"language\":\"英语（熟练，可作为工作语言）、日语（基础，能进行简单交流）\",\"skill\":\"熟练使用Java、Kotlin进行Android开发；精通Git版本控制；熟悉Jetpack组件及M\"},\"phone_number\":\"15222222222\",\"photo\":\"https://aijianli.cdn.duodianrou.com/upload/20250825/photo_105282_1756104444575.jpg\",\"project\":[{\"description_str\":\"1.负责整体架构设计和技术选型\\n2.开发核心功能模块，包括简历模板、智能填写等\\n3.优化应用性能，提升用户体验\\n4.团队管理，协调前端、后端和测试工作\",\"end_time\":\"2023-09-01\",\"pid\":\"33\",\"project_name\":\"智能简历助手App\",\"role_name\":\"项目负责人\",\"start_time\":\"2022-03-01\"}],\"school\":[{\"department\":\"计算机科学与技术学院\",\"description_str\":\"主修课程：数据结构、计算机网络、软件工程等，成绩优异，获得校级奖学金3次\",\"end_time\":\"2020-06-01\",\"experience_name\":\"北京大学\",\"role_name\":\"本科生\",\"sid\":\"28\",\"start_time\":\"2016-09-01\"}],\"work\":[{\"company_name\":\"测试科技有限公司\",\"department\":\"技术部\",\"description_str\":\"1.负责公司核心产品的Android端开发\\n2.参与需求分析和技术方案设计\\n3.优化应用性能，提升用户体验\",\"end_time\":\"2023-11-01\",\"position_name\":\"Android开发工程师\",\"start_time\":\"2020-01-01\",\"wid\":\"31\"},{\"company_name\":\"测试科技有限公司\",\"department\":\"技术部\",\"description_str\":\"1.负责公司核心产品的Android端开发\\n2.参与需求分析和技术方案设计\\n3.优化应用性能，提升用户体验\",\"end_time\":\"2023-11-01\",\"position_name\":\"Android开发工程师\",\"start_time\":\"2020-01-01\",\"wid\":\"32\"}]}"

        fun createTest(): DocxBean {
            return resumeServerBeanToThis(Gson().fromJson(TEST_JSON, ResumeServerBean::class.java))
        }

        fun resumeServerBeanToThis(b: ResumeServerBean): DocxBean {
            return DocxBean(
                mutableListOf(
                    DocxACBean("name", b.name ?: "", false),
                    DocxACBean("gender", b.genderStr, true),
                    DocxACBean("age", b.ageStr, true),
                    DocxACBean("phoneNumber", b.phone_number ?: "", false),
                    DocxACBean("email", b.email ?: "", false),
                    DocxACBean("jobExperience", b.jobExperienceStr, true),
                    DocxACBean("userAvatar", b.photo ?: "", false, type = DocxACBean.TYPE_IMAGE),
                    DocxACBean("jobObjective", b.job_objective ?: "", false),
                    DocxACBean("expectedIndustry", b.expected_industry ?: "", true),
                    DocxACBean("expectedSalary", b.expected_salary ?: "", true),
                ),
                mutableListOf(
                    DocxGroupBean("workExperience", null, true, b.work.isNullOrEmpty()),
                    DocxGroupBean(
                        "workExperienceItemBeanList",
                        b.work?.map {
                            mutableListOf(
                                DocxReplaceBean("time", it.timePart()),
                                DocxReplaceBean("companyName", it.company_name ?: ""),
                                DocxReplaceBean("department", it.department ?: ""),
                                DocxReplaceBean("positionName", it.position_name ?: ""),
                                DocxReplaceBean("description", it.description_str ?: ""),
                            )
                        }?.toMutableList(),
                        false,
                        b.work.isNullOrEmpty(),
                    ),
                    DocxGroupBean("projectExperience", null, true, b.project.isNullOrEmpty()),
                    DocxGroupBean(
                        "projectExperienceItemBeanList",
                        b.project?.map {
                            mutableListOf(
                                DocxReplaceBean("time", it.timePart()),
                                DocxReplaceBean("projectName", it.project_name ?: ""),
                                DocxReplaceBean("roleName", it.role_name ?: ""),
                                DocxReplaceBean("description", it.description_str ?: ""),
                            )
                        }?.toMutableList(),
                        false,
                        b.project.isNullOrEmpty(),
                    ),
                    DocxGroupBean("educationExperience", null, true, b.education.isNullOrEmpty()),
                    DocxGroupBean(
                        "educationExperienceItemBeanList",
                        b.education?.map {
                            mutableListOf(
                                DocxReplaceBean("time", it.timePart()),
                                DocxReplaceBean("schoolName", it.school_name ?: ""),
                                DocxReplaceBean("majorName", it.major_name ?: ""),
                                DocxReplaceBean("degree", it.degree ?: ""),
                                DocxReplaceBean("description", it.description_str ?: ""),
                            )
                        }?.toMutableList(),
                        false,
                        b.education.isNullOrEmpty(),
                    ),
                    DocxGroupBean("schoolExperience", null, true, b.school.isNullOrEmpty()),
                    DocxGroupBean(
                        "schoolExperienceItemBeanList",
                        b.school?.map {
                            mutableListOf(
                                DocxReplaceBean("time", it.timePart()),
                                DocxReplaceBean("experienceName", it.experience_name ?: ""),
                                DocxReplaceBean("department", it.department ?: ""),
                                DocxReplaceBean("roleName", it.role_name ?: ""),
                                DocxReplaceBean("description", it.description_str ?: ""),
                            )
                        }?.toMutableList(),
                        false,
                        b.school.isNullOrEmpty(),
                    ),
                    DocxGroupBean("honor", null, true, b.honor.isNullOrEmpty()),
                    DocxGroupBean(
                        "honorItemBeanList",
                        b.honor?.map {
                            mutableListOf(
                                DocxReplaceBean("time", it.awardTime()),
                                DocxReplaceBean("honorName", it.honor_name ?: ""),
                            )
                        }?.toMutableList(),
                        false,
                        b.honor.isNullOrEmpty(),
                    ),
                    DocxGroupBean("other", null, true, b.other?.noData() == true),
                    DocxGroupBean(
                        "haveSkill",
                        mutableListOf(mutableListOf(DocxReplaceBean("skillName", b.other?.skillStr ?: ""))),
                        false,
                        b.other?.skillStr.isNullOrEmpty(),
                    ),
                    DocxGroupBean(
                        "haveLanguage",
                        mutableListOf(mutableListOf(DocxReplaceBean("language", b.other?.languageStr ?: ""))),
                        false,
                        b.other?.languageStr.isNullOrEmpty(),
                    ),
                    DocxGroupBean(
                        "haveCertificate",
                        mutableListOf(mutableListOf(DocxReplaceBean("certificate", b.other?.certificate ?: ""))),
                        false,
                        b.other?.certificate.isNullOrEmpty(),
                    ),
                    DocxGroupBean(
                        "haveActivity",
                        mutableListOf(mutableListOf(DocxReplaceBean("activity", b.other?.activity ?: ""))),
                        false,
                        b.other?.activity.isNullOrEmpty(),
                    ),
                    DocxGroupBean(
                        "haveHobby",
                        mutableListOf(mutableListOf(DocxReplaceBean("hobby", b.other?.hobby ?: ""))),
                        false,
                        b.other?.hobby.isNullOrEmpty(),
                    ),
                ),
            )
        }
    }
}

data class DocxGroupBean(
    var keyword: String,
    var replaceList: MutableList<MutableList<DocxReplaceBean>>? = null,
    var containHave: Boolean = true,
    var hideChild: Boolean = false,
) {
    @Transient
    var haveStart: String = ""

    @Transient
    var haveEnd: String = ""

    init {
        if (keyword.isNotEmpty()) {
            if (containHave) {
                haveStart = "\${have${keyword[0].uppercase()}${keyword.substring(1, keyword.length)}}"
                haveEnd = "\${/have${keyword[0].uppercase()}${keyword.substring(1, keyword.length)}}"
            } else {
                haveStart = "\${${keyword}}"
                haveEnd = "\${/${keyword}}"
            }
        }
    }
}

data class DocxReplaceBean(
    var keyword: String,
    var data: String
) {
    @Transient
    var replace: String = ""

    init {
        if (keyword.isNotEmpty()) {
            replace = "\${${keyword}}"
        }
    }
}

data class DocxACBean(
    var keyword: String,
    var data: String,
    var checkHave: Boolean = true,
    var type: Int = TYPE_TEXT,
) {
    companion object {
        const val TYPE_TEXT = 0
        const val TYPE_IMAGE = 1
    }

    @Transient
    var replace: String = ""

    @Transient
    var haveStart: String = ""

    @Transient
    var haveEnd: String = ""

    init {
        if (keyword.isNotEmpty()) {
            replace = "\${${keyword}}"
            if (checkHave) {
                haveStart = "\${have${keyword[0].uppercase()}${keyword.substring(1, keyword.length)}}"
                haveEnd = "\${/have${keyword[0].uppercase()}${keyword.substring(1, keyword.length)}}"
            }
        }
    }
}