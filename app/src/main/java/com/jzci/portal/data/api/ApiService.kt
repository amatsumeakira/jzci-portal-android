package com.jzci.portal.data.api

import com.google.gson.Gson
import com.jzci.portal.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Base64
import java.net.HttpURLConnection
import java.net.URL

class ApiService(private val baseUrl: String) {

    private val gson = Gson()
    private var sessionId: String? = null

    suspend fun login(username: String, password: String): Result<LoginResponse> = withContext(Dispatchers.IO) {
        try {
            val encodedPassword = Base64.getEncoder().encodeToString(password.toByteArray(Charsets.UTF_8))
            val json = gson.toJson(mapOf("username" to username, "password" to encodedPassword))

            val connection = URL("$baseUrl/api/login").openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.outputStream.write(json.toByteArray())

            val response = connection.inputStream.bufferedReader().readText()
            val data = gson.fromJson(response, Map::class.java)

            if (connection.responseCode == 200) {
                sessionId = data["sessionId"] as String
                Result.success(LoginResponse(sessionId!!, data["username"] as String))
            } else {
                Result.failure(Exception((data["error"] as? String) ?: "登录失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStudentInfo(): Result<StudentInfo> = withContext(Dispatchers.IO) {
        try {
            val sid = sessionId ?: return@withContext Result.failure(Exception("未登录"))

            val connection = URL("$baseUrl/api/student").openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("X-Session-Id", sid)

            val response = connection.inputStream.bufferedReader().readText()

            when (connection.responseCode) {
                200 -> {
                    @Suppress("UNCHECKED_CAST")
                    val data = gson.fromJson(response, Map::class.java) as Map<String, Any>
                    val studentInfo = parseStudentInfo(data)
                    Result.success(studentInfo)
                }
                401 -> Result.failure(Exception("会话已过期"))
                else -> Result.failure(Exception("获取信息失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val sid = sessionId ?: return@withContext Result.success(Unit)
            sessionId = null

            val connection = URL("$baseUrl/api/logout").openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("X-Session-Id", sid)
            connection.inputStream.bufferedReader().readText()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    suspend fun checkSession(): Boolean = withContext(Dispatchers.IO) {
        try {
            val sid = sessionId ?: return@withContext false
            val connection = URL("$baseUrl/api/me").openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("X-Session-Id", sid)
            connection.responseCode == 200
        } catch (e: Exception) {
            false
        }
    }

    fun setSession(id: String) {
        sessionId = id
    }

    fun getSession(): String? = sessionId

    @Suppress("UNCHECKED_CAST")
    private fun parseStudentInfo(data: Map<String, Any>): StudentInfo {
        fun <T> Map<String, Any>.toClass(clazz: Class<T>): T? {
            return try {
                gson.fromJson(gson.toJson(this), clazz)
            } catch (e: Exception) {
                null
            }
        }

        val baseInfo = (data["baseInfo"] as? Map<String, Any>)?.toClass(BaseInfo::class.java)
        val batchInfo = (data["batchInfo"] as? Map<String, Any>)?.toClass(BatchInfo::class.java)
        val dorm = (data["dorm"] as? Map<String, Any>)?.toClass(DormInfo::class.java)
        val managerList = data["manager"] as? List<Map<String, Any>>
        val manager = managerList?.firstOrNull()?.toClass(ManagerInfo::class.java)
        val familyList = data["family"] as? List<Map<String, Any>>
        val family = familyList?.mapNotNull { it.toClass(FamilyMember::class.java) }

        return StudentInfo(
            baseInfo = baseInfo,
            batchInfo = batchInfo,
            dorm = dorm,
            manager = manager,
            family = family
        )
    }
}
