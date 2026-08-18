package com.sky.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理端 WebSocket 服务连接地址为 /ws/{sid}，其中 sid 为客户端生成的标识
 */
@Component
@ServerEndpoint("/ws/{sid}")
@Slf4j
public class WebSocketServer {

    //WebSocket会话集合，保存当前在线管理端连接
    private static final Map<String, Session> SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * 建立WebSocket连接并登记管理端会话
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        SESSION_MAP.put(sid, session);
        log.info("WebSocket 客户端已连接：{}，当前连接数：{}", sid, SESSION_MAP.size());
    }

    /**
     * 接收并记录WebSocket客户端消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        log.debug("收到 WebSocket 客户端 {} 的消息：{}", sid, message);
    }

    /**
     * 关闭WebSocket连接并移除管理端会话
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        SESSION_MAP.remove(sid);
        log.info("WebSocket 客户端已断开：{}，当前连接数：{}", sid, SESSION_MAP.size());
    }

    /**
     * 向全部在线管理端广播文本消息
     * @param message 消息内容
     */
    public void sendToAllClient(String message) {
        SESSION_MAP.forEach((sid, session) -> {
            if (!session.isOpen()) {
                SESSION_MAP.remove(sid, session);
                return;
            }
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                SESSION_MAP.remove(sid, session);
                log.warn("向 WebSocket 客户端 {} 推送消息失败", sid, e);
            }
        });
    }
}
