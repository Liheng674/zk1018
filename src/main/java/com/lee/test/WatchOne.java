package com.lee.test;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.lee.config.BaseConfig;

/**
 * @description 
 * 1	初始化ZK的多个操作
 * 		1.1	建立ZK的链接
 * 		1.2	创建/atguigu节点并赋值
 * 		1.3	获得该节点的值
 * 
 * 2	watch
 * 		2.1	获得值之后(getZnode方法被调用后)设置一个观察者watcher，如果/atguigu该节点的值发生了变化，(A-->B)
 * 			要求通知Client端eclipse，一次性通知
 * @author Lee
 * @date 2018年3月21日
 */
public class WatchOne extends BaseConfig {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(WatchOne.class);

	@Override
	public String getZnode(String path) throws KeeperException, InterruptedException {
		byte[] data = zooKeeper.getData(path, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				try {
					// 触发了/atguigu的数据变更后, 立即通知并获取最新值
					// 提出业务逻辑操作, 封装成方法
					trigger(path);
					
				} catch (KeeperException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, new Stat());
		return new String(data);
	}
	
	/**
	* @Title: trigger
	* @Description: TODO(一次性通知)
	* @param @param path
	* @param @return
	* @param @throws KeeperException
	* @param @throws InterruptedException    参数
	* @return String    返回类型
	* @throws
	 */
	public void trigger(String path) throws KeeperException, InterruptedException {
		String result = "";
		byte[] data = zooKeeper.getData(path, null, new Stat());
		result = new String(data);
		logger.info("------------zNode has changed, zNode = " + result);
	}
	
	public static void main(String[] args) throws Exception {
		WatchOne wo = new WatchOne();
		ZooKeeper zk = wo.start();
		
		if(zk.exists(PATH, false) == null) {
			wo.createZnode(PATH, "hello1018");
			
			String znode = wo.getZnode(PATH);
			if (logger.isInfoEnabled()) {
				logger.info("main(String[]) - String znode=" + znode); //$NON-NLS-1$
			}
		}else{
			logger.info("**************this node has existed");
		}
		
		Thread.sleep(Long.MAX_VALUE);
	}
}
