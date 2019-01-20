package com.crossoverjie.cim.client.service.impl;

import com.crossoverjie.cim.client.client.CIMClient;
import com.crossoverjie.cim.client.config.AppConfiguration;
import com.crossoverjie.cim.client.service.RouteRequest;
import com.crossoverjie.cim.common.kit.HeartBeatHandler;
import com.crossoverjie.cim.common.util.NettyAttrUtil;
import io.netty.channel.ChannelHandlerContext;
import okhttp3.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-20 17:16
 * @since JDK 1.8
 */
@Service
public class ClientHeartBeatHandlerImpl implements HeartBeatHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClientHeartBeatHandlerImpl.class);
    private final MediaType mediaType = MediaType.parse("application/json");

    @Autowired
    private AppConfiguration appConfiguration ;

    @Autowired
    private CIMClient cimClient ;

    @Autowired
    private RouteRequest routeRequest;

    @Override
    public void process(ChannelHandlerContext ctx) throws Exception {

        long heartBeatTime = appConfiguration.getHeartBeatTime() * 1000;

        Long lastReadTime = NettyAttrUtil.getReaderTime(ctx.channel());
        long now = System.currentTimeMillis();
        if (lastReadTime != null && now - lastReadTime > heartBeatTime){
            LOGGER.warn("服务端心跳超时[{}]ms，[{}]需要关闭重新连接!",now - lastReadTime,appConfiguration.getUserName());

            //首先清除路由信息，下线
            routeRequest.offLine();

            //重连
            cimClient.reconnect();

        }
    }


}