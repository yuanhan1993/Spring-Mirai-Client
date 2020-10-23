package net.lz1998.mirai.service

import dto.HttpDto
import kotlinx.serialization.json.Json
import net.lz1998.mirai.entity.MyBotInfo
import net.lz1998.mirai.entity.RemoteBot
import net.lz1998.mirai.entity.WebsocketBotClient
import net.lz1998.mirai.entity.loadStrAsMyBotInfo
import net.lz1998.mirai.properties.ClientProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class BotService {
    val botMap = mutableMapOf<Long, RemoteBot>()

    @Autowired
    lateinit var clientProperties: ClientProperties

    @Synchronized
    suspend fun createBot(botId: Long, password: String) {
        var bot = botMap[botId]
        if (bot == null) {
            bot = WebsocketBotClient(botId, password, wsUrl = clientProperties.wsUrl)
            botMap[botId] = bot
            bot.initBot()
        }else{
            bot = WebsocketBotClient(botId, password, wsUrl = clientProperties.wsUrl)
            botMap.replace(botId,bot)
        }
    }

    fun listBot(): Collection<HttpDto.Bot> {
        return botMap.values.map { remoteBot ->
            HttpDto.Bot.newBuilder().setBotId(remoteBot.botId).setIsOnline(
                    try {
                        remoteBot.bot.isOnline
                    } catch (e: Exception) {
                        false
                    }
            ).build()
        }
    }

    suspend fun botLogin(botId: Long) {
        val bot = botMap[botId]
        bot?.bot?.login()
    }

    suspend fun initBots(){
        val systemDir = File("""bots""")
        val fileTree: FileTreeWalk = systemDir.walk()
        fileTree.maxDepth(1)
                .filter { it.isFile }
                .filter { it.extension == "json" }
                .forEach {
                    loadBotFromFile(it)
                }
    }

    suspend fun loadBotFromFile(file: File){
        var botInfo = file.loadStrAsMyBotInfo(Json)
        if(botInfo.botId != 0L){
            createBot(botInfo.botId,botInfo.encryptPwd)
        }
    }
}

