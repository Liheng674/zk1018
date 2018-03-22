package com.lee.config;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import lombok.Getter;
import lombok.Setter;

/**
 * @description 基础配置
 * @author Lee
 * @date 2018年3月21日
 */
public class BaseConfig {
	
	private static final String CONNECTSTRING = "192.168.152.128:2181";
	private static final int SESSION_TIMEOUT = 20 * 1000;
	protected static final String PATH = "/atguigu";
	protected@Setter@Getter ZooKeeper zooKeeper = null;
	
	/**
	* @Title: start
	* @Description: TODO(新建连接)
	* @param @return
	* @param @throws IOException    设定文件
	* @return ZooKeeper    返回类型
	* @throws
	 */
	public ZooKeeper start() throws IOException {
		zooKeeper =  new ZooKeeper(CONNECTSTRING, SESSION_TIMEOUT, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		return zooKeeper;
	}
	
	/**
	* @Title: createZnode
	* @Description: TODO(新建zNode节点)
	* @param @param path
	* @param @param data
	* @param @throws KeeperException
	* @param @throws InterruptedException    设定文件
	* @return void    返回类型
	* @throws
	 */
	public void createZnode(String path, String data) throws KeeperException, InterruptedException {
		zooKeeper.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
	
	/**
	* @Title: getZnode
	* @Description: TODO(获取zNode节点数据)
	* @param @param path
	* @param @return
	* @param @throws KeeperException
	* @param @throws InterruptedException    参数
	* @return String    返回类型
	* @throws
	 */
	public String getZnode(String path) throws KeeperException, InterruptedException {
		String result = "";
		byte[] data = zooKeeper.getData(path, false, new Stat());
		result = new String(data);
		return result;
	}
	
	/**
	* @Title: stop
	* @Description: TODO(关闭连接)
	* @param @param path
	* @param @throws KeeperException
	* @param @throws InterruptedException    参数
	* @return void    返回类型
	* @throws
	 */
	public void stop() throws KeeperException, InterruptedException {
		if (zooKeeper != null) {
			zooKeeper.close();
		}
	}
}
