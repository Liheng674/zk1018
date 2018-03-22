package com.lee.test;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.lee.config.BaseConfig;

import lombok.Getter;
import lombok.Setter;

/**
 * @description 
 * 1	初始化ZK的多个操作
 * 		1.1	建立ZK的链接
 * 		1.2	创建/atguigu节点并赋值
 * 		1.3	获得该节点的值
 * 
 * 2	watchmore
 * 		2.1	获得值之后设置一个观察者watcher，如果/atguigu该节点的值发生了变化，要求通知Client端，一次性通知
 * 
 * 3	watchMore
 * 		3.1	获得值之后设置一个观察者watcher，如果/atguigu该节点的值发生了变化，要求通知Client端,继续观察
 * 		3.2	又再次获得新的值的同时再新设置一个观察者，继续观察并获得值
 * 		3.3	又再次获得新的值的同时再新设置一个观察者，继续观察并获得值.。。。。。重复上述过程
 * @author Lee
 * @date 2018年3月21日
 */
public class WatchMore extends BaseConfig {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(WatchMore.class);
	private @Setter@Getter String oldValue;
	private @Setter@Getter String newValue;
	
	public String getZnode(String path) throws KeeperException, InterruptedException {
		String result = "";
		byte[] data = zooKeeper.getData(path, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				try {
					trigger(path);
				} catch (KeeperException | InterruptedException e) {
					e.printStackTrace();
				}
			}

		}, new Stat());
		result = new String(data);
		setOldValue(result);
		return result;
	}

	/**
	* @Title: trigger
	* @Description: TODO(持续通知)
	* @param @param path
	* @param @throws KeeperException
	* @param @throws InterruptedException    参数
	* @return void    返回类型
	* @throws
	 */
	public void trigger(String path) throws KeeperException, InterruptedException {
		String result = "";
		byte[] data = zooKeeper.getData(path, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				try {
					trigger(path);
				} catch (KeeperException | InterruptedException e) {
					e.printStackTrace();
				}
			}

		}, new Stat());
		result = new String(data);
		setNewValue(result);
		if (oldValue.equals(newValue)) {
			logger.info("--------------the value has not changes yet");
		}else {
			logger.info("------------zNode has changed,OLD_VALUE = "+oldValue+"\tNEW_VALUE = " + newValue);
			setOldValue(newValue);
		}
	}
	
	public static void main(String[] args) throws Exception {
		WatchMore wm = new WatchMore();
		ZooKeeper zk = wm.start();
		
		if(zk.exists(PATH, false) == null) {
			wm.createZnode(PATH, "hello1018");
			
			String znode = wm.getZnode(PATH);
			if (logger.isInfoEnabled()) {
				logger.info("main(String[]) - String znode=" + znode); //$NON-NLS-1$
			}
		}else{
			logger.info("**************this node has existed");
		}
		
		Thread.sleep(Long.MAX_VALUE);
	}
}
