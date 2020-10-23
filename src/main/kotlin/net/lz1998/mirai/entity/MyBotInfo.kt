package net.lz1998.mirai.entity

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File


@Serializable
open class MyBotInfo() {
    @Required
    @SerialName("botId")
    var botId: Long = 0

    @Required
    @SerialName("encryptPwd")
    var encryptPwd: String = ""

    constructor(botId: Long, encryptPwd: String) : this() {
        this.botId = botId
        this.encryptPwd = encryptPwd
    }
}

fun saveBotInFile(botId: Long,password:String){
    //TODO 密码加密

}

/**
 * 保存账号信息
 */
fun File.saveMyBotInfoAsStr(json: Json, myBotInfo: MyBotInfo) {
    if (!this.exists() || this.length() == 0L) {
        if (!this.parentFile.exists()) {
            this.parentFile.mkdirs()
        }
    }
    this.writeText(json.encodeToString(MyBotInfo.serializer(), myBotInfo))
}

/**
 * 加载一个账号信息
 */
fun File.loadStrAsMyBotInfo(json: Json): MyBotInfo {
    if (this.exists() && this.length() != 0L) {
        return json.decodeFromString(MyBotInfo.serializer(), this.readText())
    }
    return MyBotInfo()
}
