package com.yfc.com.yfc.test

object NumberExtractor {
    @JvmStatic
    fun main(args: Array<String>) {
        val testCases = listOf(
            "1K-2K",
            "1023kasd----3450ksdfj",
            "abc123def456ghi789",
            "只有一个数字123",
            "没有数字的字符串"
        )

        testCases.forEach { testCase ->
            val result = extractTwoNumbers(testCase)
            println("输入: \"$testCase\" -> 提取结果: $result")
        }
    }

    /**
     * 从字符串中提取前两个数字
     * @param input 输入字符串
     * @return 包含前两个数字的列表，若不足两个则返回实际数量，无数字则返回空列表
     */
    fun extractTwoNumbers(input: String): List<Int> {
        // 正则表达式匹配所有数字序列
        val pattern = "\\d+".toRegex()
        // 查找所有匹配项
        val numbers = pattern.findAll(input)
            .map { it.value.toInt() } // 转换为整数
            .take(2) // 只取前两个
            .toList()

        return numbers
    }
}
