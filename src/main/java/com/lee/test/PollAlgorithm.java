package com.lee.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.yetus.audience.InterfaceAudience.Public;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import lombok.Getter;
import lombok.Setter;

/**
* @ClassName: PollAlgorithm
* @Description: TODO(这里用一句话描述这个类的作用)
* @author A18ccms a18ccms_gmail_com
* @date 2018年4月14日 下午10:40:46
* 测试zookeeper的负载均衡轮询算法 
* 在zookeeper的服务端有bank节点.他的子节点有五个 分别为 sub1 sub2 sub3 sub4 sub5 
* 效果：模仿一个银行,有五个窗口,来了15个人来办理业务.15个人轮询去访问五个窗口. 
* 当一个窗口宕机时,他会自动去访问下一个窗口。没有任何变化
*
*/
public class PollAlgorithm {

	private static final String CONNECTSTRING = "192.168.152.128:2181";
	private static final int SESSION_TIMEOUT = 20 * 1000;
	
	// 轮询测试节点
	protected static final String PATH = "/poll";
	protected@Setter@Getter ZooKeeper zooKeeper = null;
	
	// poll节点下的子节点集合
	private List<String> list = new ArrayList<>();
	
	// 子节点前缀
	private static final String PRIFIX = "k";
	private static int total = 5; 
	private int currentIndex;
	
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
				// 获取zookeeper的poll节点下所有子节点
				try {
					list = zooKeeper.getChildren(PATH, true);
					System.out.println(list);
				} catch (KeeperException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		return zooKeeper;
	}
	
	public String poll() throws KeeperException, InterruptedException {
		// 当前位置移动到1
		currentIndex = currentIndex + 1;
		// 遍历子节点集合, 返回当前节点值
		if (currentIndex > 5) {
			currentIndex = currentIndex % total;
		}
		for (int i = currentIndex; i <= total; i++) {
			// 是否包含此节点path
			if (list.contains(PRIFIX + currentIndex)) {
				//获取节点值
				byte[] data = zooKeeper.getData(PATH + "/" +PRIFIX + currentIndex, new Watcher() {
					
					@Override
					public void process(WatchedEvent event) {
						// TODO Auto-generated method stub
						
					}
				}, new Stat());
				return new String(data);
			}else {
				// 一个节点挂了, 走下一个节点
				currentIndex = currentIndex + 1;
			}
		}
		
		// for循环做判断
		/*for (int i = 1; i <= total; i++) {
			if (list.contains(PRIFIX + i)) {
				// 重要, 第二次循环时重头再来
				currentIndex = i;
				//获取节点值
				byte[] data = zooKeeper.getData(PATH + "/" +PRIFIX + currentIndex, new Watcher() {
					
					@Override
					public void process(WatchedEvent event) {
						// TODO Auto-generated method stub
						
					}
				}, new Stat());
				return new String(data);
			}
		}*/
		return "------------------no this node-----------------";
	}
	
	public static void main(String[] args) throws Exception {
		PollAlgorithm pa = new PollAlgorithm();
		pa.start();
		System.out.println("***********start work***********");
		String result;
		for (int i = 1; i <= 15; i++) {
			Thread.sleep(2000);
			result = pa.poll();
			System.out.println(i + " 号用户访问了 " + pa.currentIndex + " 号柜台, 结果"+result);
		}
	}
}
