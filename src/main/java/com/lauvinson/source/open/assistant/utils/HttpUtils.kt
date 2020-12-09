package com.lauvinson.source.open.assistant.utils


import org.apache.commons.httpclient.*
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.StringRequestEntity
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

object HttpUtils {

    //因为请求链接里需要一些特殊字符来拼接参数，这里将它们定义成变量，方便以后修改
    private const val URL_PARAM_CONNECT_FLAG = "&"
    private const val EMPTY = ""

    //声明一个多线程安全连接管理类变量，关于该类的简单介绍  https://blog.csdn.net/fairytall/article/details/7938692
    //使用这个对象简单来说就是为了不去考虑多线程带来安全的问题
    private var connectionManager: MultiThreadedHttpConnectionManager? = null
    //将参数提取成变量，方便以后修改
    private const val connectionTimeOut = 5000

    private const val socketTimeOut = 5000

    private const val maxConnectionPerHost = 20

    private const val maxTotalConnections = 20
    //声明client变量，用于执行请求的
    private var client: HttpClient? = null

    init {
        try {
            connectionManager = MultiThreadedHttpConnectionManager()
        } catch (e: Exception) {
            println(e)
        }
        connectionManager!!.params.connectionTimeout = connectionTimeOut
        connectionManager!!.params.soTimeout = socketTimeOut
        connectionManager!!.params.defaultMaxConnectionsPerHost = maxConnectionPerHost
        connectionManager!!.params.maxTotalConnections = maxTotalConnections
        client = HttpClient(connectionManager!!)
    }

    /**
     * POST方式提交数据
     *
     * @param url    待请求的URL
     * @param params 要提交的数据
     * @param enc    编码
     * @return 响应结果
     * @throws IOException IO异常
     */
    fun URLPost(url: String, params: Map<String, String>?, enc: String): Map<String, Any> {
        //创建一个Map集合，用于存储返回结果
        val result = HashMap<String, Any>()
        var response: String
        //创建一个请求方式对象
        var postMethod: PostMethod? = null
        try {
            postMethod = PostMethod(url)
            postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=$enc")
            //将请求参数放入postMethod中
            if (params != null) {
                val keySet = params.keys
                for (key in keySet) {
                    val value = params[key]
                    postMethod.addParameter(key, value)
                }
            }
            //执行postMethod
            val statusCode = client!!.executeMethod(postMethod)
            if (statusCode == HttpStatus.SC_OK) {                //HttpStatus.SC_OK就是200，服务器正确处理了请求
                response = postMethod.responseBodyAsString //返回字符串格式的结果
                result["status"] = 200                        //封装状态信息和结果
                result["data"] = response
            } else {
                result["status"] = postMethod.statusCode
            }
        } catch (e: ConnectTimeoutException) {
            result["status"] = "连接超时"
            e.printStackTrace()
        } catch (e: HttpException) {
            result["status"] = "发生致命的异常，可能是协议不对或者返回的内容有问题"
            e.printStackTrace()
        } catch (e: IOException) {
            result["status"] = "发生网络异常"
            e.printStackTrace()
        } catch (e: Exception) {
            result["status"] = "未知错误"
            e.printStackTrace()
        } finally {
            postMethod?.releaseConnection()
        }
        return result
    }

    //这个方法与上面相同，只不过请求参数是json格式的
    fun URLPost(url: String, json: String, enc: String): Map<String, Any> {

        val result = HashMap<String, Any>()
        val response: String
        var postMethod: PostMethod? = null
        try {
            postMethod = PostMethod(url)
            val se = StringRequestEntity(json, "application/json", "UTF-8")
            postMethod.requestEntity = se
            //执行postMethod
            val statusCode = client!!.executeMethod(postMethod)
            if (statusCode == HttpStatus.SC_OK) {
                response = postMethod.responseBodyAsString
                result["status"] = 200
                result["data"] = response
            } else {
                result["status"] = postMethod.statusCode
            }
        } catch (e: ConnectTimeoutException) {
            result["status"] = "连接超时"
            e.printStackTrace()
        } catch (e: HttpException) {
            result["status"] = "发生致命的异常，可能是协议不对或者返回的内容有问题"
            e.printStackTrace()
        } catch (e: IOException) {
            result["status"] = "发生网络异常"
            e.printStackTrace()
        } catch (e: Exception) {
            result["status"] = "未知错误"
            e.printStackTrace()
        } finally {
            postMethod?.releaseConnection()
        }
        return result
    }

    fun URLGet(url: String, params: Map<String, String>, enc: String): String? {

        var response: String?
        var getMethod: GetMethod? = null
        val strtTotalURL = StringBuffer(EMPTY)

        if (strtTotalURL.indexOf("?") == -1) {
            strtTotalURL.append(url).append("?").append(getUrl(params, enc))
        } else {
            strtTotalURL.append(url).append("&").append(getUrl(params, enc))
        }

        try {
            getMethod = GetMethod(strtTotalURL.toString())
            getMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=$enc")
            getMethod.setRequestHeader("Host", url)
            getMethod.setRequestHeader("Refresh", url)
            //执行getMethod
            val statusCode = client!!.executeMethod(getMethod)
            if (statusCode == HttpStatus.SC_OK) {
                response = getMethod.responseBodyAsString
            } else {
                response = null
            }
        } catch (e: ConnectTimeoutException) {
            response = "连接超时"
            e.printStackTrace()
        } catch (e: HttpException) {
            response = "发生致命的异常，可能是协议不对或者返回的内容有问题"
            e.printStackTrace()
        } catch (e: IOException) {
            response = "发生网络异常"
            e.printStackTrace()
        } catch (e: Exception) {
            response = "未知错误"
            e.printStackTrace()
        } finally {
            getMethod?.releaseConnection()
        }

        return response
    }

    /**
     * 据Map生成URL字符串
     *
     * @param map      Map
     * @param valueEnc URL编码
     * @return URL
     */
    private fun getUrl(map: Map<String, String>?, valueEnc: String): String {

        if (null == map || map.keys.isEmpty()) {
            return EMPTY
        }
        val url = StringBuilder()
        val keys = map.keys
        for (key in keys) {
            if (map.containsKey(key)) {
                val `val` = map[key]
                var str = `val` ?: EMPTY
                try {
                    str = URLEncoder.encode(str, valueEnc)
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }

                url.append(key).append("=").append(str).append(URL_PARAM_CONNECT_FLAG)
            }
        }
        var strURL: String
        strURL = url.toString()
        //这是为了保证  “&”符号后面一定有参数，如果“&”后面没有东西就把“&”去掉
        if (URL_PARAM_CONNECT_FLAG == EMPTY + strURL[strURL.length - 1]) {
            strURL = strURL.substring(0, strURL.length - 1)
        }

        return strURL
    }
}
