package com.focused.projectf.multiplayer.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

import com.focused.projectf.ErrorManager;
import com.focused.projectf.Map;
import com.focused.projectf.MapGenerator;
import com.focused.projectf.TileConstants.MapSize;
import com.focused.projectf.TileConstants.MapType;
import com.focused.projectf.global.ResearchManager;
import com.focused.projectf.resources.Content;

public class Server {
	private final int port;
	private Map map;
	
	public Server(int port) {
		this.port = port;
		ErrorManager.init("server_log.log");
		Content.initialize(false);
		ResearchManager.initialize();
		map = MapGenerator.generateMap(MapType.Land, MapSize.Huge);
		
	}
	
	public void run() {
		// Configure the server.
		ServerBootstrap bootstrap = new ServerBootstrap(
			new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(
						new ObjectEncoder(),
						new ObjectDecoder(
								ClassResolvers.cacheDisabled(getClass().getClassLoader())),
						new ActionHandler(map));
			}
		});
		
		bootstrap.bind(new InetSocketAddress(port));
		System.out.println("Server now started");
	}
	
	public static void main(String[] args) {
		new Server(15635).run();
	}
}