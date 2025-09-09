package com.yfc.com.yfc.docx.poi

data class DocxEditBean(
    var replaces: MutableList<DocxReplaceBean>,
    var shows: MutableList<DocxShowBean>,
) {
    companion object {
        fun createTest(): DocxEditBean {
            return DocxEditBean(
                mutableListOf(
                    "schoolName" to "aaaaaaaaaaaaaaaaaaaaaaa",
                    "majorName" to "出去玩啊实打实阿萨德阿萨德",
                    "time" to "1991-06-22",
                    "time" to "1991-06-23",
                    "time" to "1991-06-24",
                    "time" to "1991-06-25",
                ).map { DocxReplaceBean(it.first, it.second) }.toMutableList(),
                mutableListOf(
                    "haveWorkExperience" to true,
                    "haveProjectExperience" to true,
                    "haveEducationExperience" to false,
                    "haveSchoolExperience" to false,
                    "haveHonor" to false,
                ).map { DocxShowBean(it.first, it.second) }.toMutableList(),
            )
        }
    }

    fun copyNewValid(): DocxEditBean {
        return this.copy().apply {
            replaces = replaces.filter { it.valid }.toMutableList()
            shows = shows.filter { it.valid }.toMutableList()
        }
    }
}

data class DocxShowBean(private var keyword: String, var show: Boolean) {
    private fun String.addShowBeginTemplate() = "\${${this}}"
    private fun String.addShowEndTemplate() = "\${/${this}}"

    val valid get() = keyword.isNotEmpty()
    val showBegin get() = keyword.addShowBeginTemplate()
    val showEnd get() = keyword.addShowEndTemplate()
}

data class DocxReplaceBean(private var raw: String, var text: String) {
    private fun String.addReplaceTemplate() = "\${${this}}"

    val valid get() = raw.isNotEmpty() && text.isNotEmpty()
    val template get() = raw.addReplaceTemplate()
}