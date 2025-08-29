package com.yfc.test.okhttp

object DownloadLinks {
    val downloadLinksMap: Map<String, List<DownloadInfo>> by lazy {
        mapOf(
            // 全球通用（Cloudflare Anycast）
            "全球通用（Cloudflare Anycast）" to listOf(
                DownloadInfo(
                    url = "https://speed.cloudflare.com/__down?during=download&bytes=104857600",
                    sizeInBytes = 104857600,
                    originalSize = "100MB"
                ),
                DownloadInfo(
                    url = "https://speed.cloudflare.com/__down?during=download&bytes=1073741824",
                    sizeInBytes = 1073741824,
                    originalSize = "1GB"
                ),
                DownloadInfo(
                    url = "https://speed.cloudflare.com/__down?during=download&bytes=10737418240",
                    sizeInBytes = 10737418240,
                    originalSize = "10GB"
                )
            ),

            // 中国大陆推荐
            "中国大陆推荐" to listOf(
                DownloadInfo(
                    url = "https://dldir1.qq.com/qqfile/qq/PCQQ9.7.17/QQ9.7.17.29225.exe",
                    sizeInBytes = 209715200,
                    originalSize = "200MB",
                    remark = "QQ CDN加速"
                ),
                DownloadInfo(
                    url = "https://wirelesscdn-download.xuexi.cn/publish/xuexi_android/latest/xuexi_android_10002068.apk",
                    sizeInBytes = 314572800,
                    originalSize = "300MB",
                    remark = "阿里CDN"
                ),
                DownloadInfo(
                    url = "http://speedtest.zju.edu.cn/1000M",
                    sizeInBytes = 1073741824,
                    originalSize = "1000MB（1GB）",
                    remark = "教育网"
                ),
                DownloadInfo(
                    url = "http://vipspeedtest8.wuhan.net.cn:8080/download?size=1073741824",
                    sizeInBytes = 1073741824,
                    originalSize = "1GB",
                    remark = "电信"
                ),
                DownloadInfo(
                    url = "http://vipspeedtest8.wuhan.net.cn:8080/download?size=10737418240",
                    sizeInBytes = 10737418240,
                    originalSize = "10GB",
                    remark = "电信"
                ),
                DownloadInfo(
                    url = "https://dlied4.myapp.com/myapp/1104466820/cos.release-40109/10040714_com.tencent.tmgp.sgame_a2480356_8.2.1.9_F0BvnI.apk",
                    sizeInBytes = 2147483648,
                    originalSize = "2GB",
                    remark = "自适应CDN"
                )
            ),

            // 中国香港（Datapacket/CDN77）
            "中国香港（Datapacket/CDN77）" to listOf(
                DownloadInfo(
                    url = "http://hkg.download.datapacket.com/100mb.bin",
                    sizeInBytes = 104857600,
                    originalSize = "100MB"
                ),
                DownloadInfo(
                    url = "http://hkg.download.datapacket.com/1000mb.bin",
                    sizeInBytes = 1073741824,
                    originalSize = "1000MB（1GB）"
                ),
                DownloadInfo(
                    url = "http://hkg.download.datapacket.com/10000mb.bin",
                    sizeInBytes = 10737418240,
                    originalSize = "10000MB（10GB）"
                )
            ),

            // 新加坡
            "新加坡" to listOf(
                DownloadInfo(
                    url = "http://sgp.download.datapacket.com/100mb.bin",
                    sizeInBytes = 104857600,
                    originalSize = "100MB",
                    remark = "Datapacket/CDN77"
                ),
                DownloadInfo(
                    url = "http://sgp.download.datapacket.com/1000mb.bin",
                    sizeInBytes = 1073741824,
                    originalSize = "1000MB（1GB）",
                    remark = "Datapacket/CDN77"
                ),
                DownloadInfo(
                    url = "http://sgp.download.datapacket.com/10000mb.bin",
                    sizeInBytes = 10737418240,
                    originalSize = "10000MB（10GB）",
                    remark = "Datapacket/CDN77"
                ),
                DownloadInfo(
                    url = "https://sgp.proof.ovh.net/files/100Mb.dat",
                    sizeInBytes = 104857600,
                    originalSize = "100MB",
                    remark = "OVH"
                ),
                DownloadInfo(
                    url = "https://sgp-ping.vultr.com/vultr.com.100MB.bin",
                    sizeInBytes = 104857600,
                    originalSize = "100MB",
                    remark = "Vultr"
                )
            ),

            // 日本东京
            "日本东京" to listOf(
                DownloadInfo(
                    url = "http://tyo.download.datapacket.com/100mb.bin",
                    sizeInBytes = 104857600,
                    originalSize = "100MB",
                    remark = "Datapacket/CDN77"
                ),
                DownloadInfo(
                    url = "http://tyo.download.datapacket.com/1000mb.bin",
                    sizeInBytes = 1073741824,
                    originalSize = "1000MB（1GB）",
                    remark = "Datapacket/CDN77"
                ),
                DownloadInfo(
                    url = "http://tyo.download.datapacket.com/10000mb.bin",
                    sizeInBytes = 10737418240,
                    originalSize = "10000MB（10GB）",
                    remark = "Datapacket/CDN77"
                ),
                DownloadInfo(
                    url = "https://hnd-jp-ping.vultr.com/vultr.com.100MB.bin",
                    sizeInBytes = 104857600,
                    originalSize = "100MB",
                    remark = "Vultr"
                ),
                DownloadInfo(
                    url = "https://hnd-jp-ping.vultr.com/vultr.com.1000MB.bin",
                    sizeInBytes = 1073741824,
                    originalSize = "1GB",
                    remark = "Vultr"
                )
            ),

            // 德国
            "德国" to listOf(
                DownloadInfo(
                    url = "https://nbg1-speed.hetzner.com/100MB.bin",
                    sizeInBytes = 104857600,
                    originalSize = "100MB",
                    remark = "Hetzner（纽伦堡）"
                ),
                DownloadInfo(
                    url = "https://fra-de-ping.vultr.com/vultr.com.100MB.bin",
                    sizeInBytes = 104857600,
                    originalSize = "100MB",
                    remark = "Vultr（法兰克福）"
                ),
                DownloadInfo(
                    url = "https://nbg1-speed.hetzner.com/1GB.bin",
                    sizeInBytes = 1073741824,
                    originalSize = "1GB",
                    remark = "Hetzner（纽伦堡）"
                ),
                DownloadInfo(
                    url = "https://fra-de-ping.vultr.com/vultr.com.1000MB.bin",
                    sizeInBytes = 1073741824,
                    originalSize = "1GB",
                    remark = "Vultr（法兰克福）"
                ),
                DownloadInfo(
                    url = "https://nbg1-speed.hetzner.com/10GB.bin",
                    sizeInBytes = 10737418240,
                    originalSize = "10GB",
                    remark = "Hetzner（纽伦堡）"
                )
            ),

            // 法国（巴黎）
            "法国（巴黎）" to listOf(
                DownloadInfo(
                    url = "http://par.download.datapacket.com/100mb.bin",
                    sizeInBytes = 104857600,
                    originalSize = "100MB",
                    remark = "Datapacket/CDN77"
                ),
                DownloadInfo(
                    url = "http://par.download.datapacket.com/1000mb.bin",
                    sizeInBytes = 1073741824,
                    originalSize = "1000MB（1GB）",
                    remark = "Datapacket/CDN77"
                ),
                DownloadInfo(
                    url = "http://par.download.datapacket.com/10000mb.bin",
                    sizeInBytes = 10737418240,
                    originalSize = "10000MB（10GB）",
                    remark = "Datapacket/CDN77"
                ),
                DownloadInfo(
                    url = "https://gra.proof.ovh.net/files/100Mb.dat",
                    sizeInBytes = 104857600,
                    originalSize = "100MB",
                    remark = "OVH"
                ),
                DownloadInfo(
                    url = "https://gra.proof.ovh.net/files/1Gb.dat",
                    sizeInBytes = 1073741824,
                    originalSize = "1GB",
                    remark = "OVH"
                )
            ),

            // 美西（洛杉矶/俄勒冈）
            "美西（洛杉矶/俄勒冈）" to listOf(
                DownloadInfo(
                    url = "http://lax.download.datapacket.com/100mb.bin",
                    sizeInBytes = 104857600,
                    originalSize = "100MB",
                    remark = "Datapacket/CDN77（洛杉矶）"
                ),
                DownloadInfo(
                    url = "http://lax.download.datapacket.com/1000mb.bin",
                    sizeInBytes = 1073741824,
                    originalSize = "1000MB（1GB）",
                    remark = "Datapacket/CDN77（洛杉矶）"
                ),
                DownloadInfo(
                    url = "http://lax.download.datapacket.com/10000mb.bin",
                    sizeInBytes = 10737418240,
                    originalSize = "10000MB（10GB）",
                    remark = "Datapacket/CDN77（洛杉矶）"
                ),
                DownloadInfo(
                    url = "https://hil.proof.ovh.us/files/100Mb.dat",
                    sizeInBytes = 104857600,
                    originalSize = "100MB",
                    remark = "OVH（俄勒冈）"
                ),
                DownloadInfo(
                    url = "https://lax-ca-us-ping.vultr.com/vultr.com.100MB.bin",
                    sizeInBytes = 104857600,
                    originalSize = "100MB",
                    remark = "Vultr（洛杉矶）"
                )
            ),

            // 美东（阿什本/弗吉尼亚/新泽西）
            "美东（阿什本/弗吉尼亚/新泽西）" to listOf(
                DownloadInfo(
                    url = "http://ash.download.datapacket.com/100mb.bin",
                    sizeInBytes = 104857600,
                    originalSize = "100MB",
                    remark = "Datapacket/CDN77（阿什本）"
                ),
                DownloadInfo(
                    url = "http://ash.download.datapacket.com/1000mb.bin",
                    sizeInBytes = 1073741824,
                    originalSize = "1000MB（1GB）",
                    remark = "Datapacket/CDN77（阿什本）"
                ),
                DownloadInfo(
                    url = "http://ash.download.datapacket.com/10000mb.bin",
                    sizeInBytes = 10737418240,
                    originalSize = "10000MB（10GB）",
                    remark = "Datapacket/CDN77（阿什本）"
                ),
                DownloadInfo(
                    url = "https://vin.proof.ovh.us/files/100Mb.dat",
                    sizeInBytes = 104857600,
                    originalSize = "100MB",
                    remark = "OVH（弗吉尼亚）"
                ),
                DownloadInfo(
                    url = "https://nj-us-ping.vultr.com/vultr.com.100MB.bin",
                    sizeInBytes = 104857600,
                    originalSize = "100MB",
                    remark = "Vultr（新泽西）"
                )
            )
        )
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // 遍历所有地区信息
        downloadLinksMap.forEach { (region, infoList) ->
            println("===== $region =====")
            infoList.take(2).forEach { info ->  // 每个地区只打印前2条示例
                println("链接:  ${info.url}")  // 截断长链接便于展示
                println("大小: ${info.sizeInBytes} Byte (${info.originalSize})")
                if (info.remark.isNotEmpty()) println("备注: ${info.remark}")
                println("---")
            }
            if (infoList.size > 2) println("... 还有 ${infoList.size - 2} 条链接省略展示 ...\n")
        }
    }

    // 数据类：存储单个下载链接的详细信息
    data class DownloadInfo(
        val url: String,
        val sizeInBytes: Long,
        val originalSize: String,
        val remark: String = ""
    )
}
    