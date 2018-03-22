package com.lee.test;

import org.apache.log4j.Logger;
import org.apache.zookeeper.ZooKeeper;

import com.lee.config.BaseConfig;

/**
 * @description eclipse此处为Client端，CentOS为ZooKeeper的Server端
 * 
 * 1	通过java程序，新建链接zk，类似jdbc的connection，open.session
 * 2	新建一个znode节点/atguigu并设置为hello1018	等同于create /atguigu hello1018
 * 3	获得当前节点/atguigu的最新值			get /atguigu
 * 4	关闭链接
 * @author Lee
 * @date 2018年3月21日
 */
public class HelloZK extends BaseConfig {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(HelloZK.class);
	
	public static void main(String[] args) throws Exception {
		HelloZK hello = new HelloZK();
		ZooKeeper zooKeeper = hello.start();
		
		if(zooKeeper.exists(PATH, false) == null) {
			hello.createZnode(PATH, "hello1018");
			
			String znode = hello.getZnode(PATH);
			if (logger.isInfoEnabled()) {
				logger.info("main(String[]) - String znode=" + znode); //$NON-NLS-1$
			}
		}else {
			logger.info("**************this node has existed");
		}
		
		hello.stop();
	}
}
