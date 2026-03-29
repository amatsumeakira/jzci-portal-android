package com.jzci.portal.data.model

data class LoginResponse(
    val sessionId: String,
    val username: String
)

data class StudentInfo(
    val baseInfo: BaseInfo? = null,
    val batchInfo: BatchInfo? = null,
    val dorm: DormInfo? = null,
    val manager: ManagerInfo? = null,
    val family: List<FamilyMember>? = null,
    val error: String? = null
)

data class BaseInfo(
    val XSBH: String = "",
    val XM: String = "",
    val XBDM_DISPLAY: String = "",
    val SFZJH: String = "",
    val DWDM_DISPLAY: String = "",
    val ZYDM_DISPLAY: String = "",
    val BJDM_DISPLAY: String = "",
    val MZDM_DISPLAY: String = "",
    val ZZMMDM_DISPLAY: String = "",
    val XJZTDM_DISPLAY: String = "",
    val XSLXDM_DISPLAY: String = "",
    val SSL_DISPLAY: String = "",
    val ZSDZ: String = ""
)

data class BatchInfo(
    val XZNJ: String = "",
    val DWDM_DISPLAY: String = "",
    val ZYDM_DISPLAY: String = "",
    val BJDM_DISPLAY: String = "",
    val XSLBDM_DISPLAY: String = "",
    val XN: String = "",
    val WHKSRQ: String = "",
    val WHJSRQ: String = ""
)

data class DormInfo(
    val XQDM_DISPLAY: String = "",
    val SSDM_DISPLAY: String = "",
    val FJH: String = "",
    val CWH: String = "",
    val SFBZ: String = ""
)

data class ManagerInfo(
    val XM: String = "",
    val ZGH: String = "",
    val LXDH: String = "",
    val RZLBDM_DISPLAY: String = "",
    val ZRZT: String = ""
)

data class FamilyMember(
    val CYXM: String = "",
    val GXDM_DISPLAY: String = "",
    val LXDH: String = "",
    val GZDW: String = ""
)
