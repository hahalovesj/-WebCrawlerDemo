package action;
/**	http://www.zifangsky.cn/239.html
 *  前言：写这篇文章之前，主要是我看了几篇类似的爬虫写法，有的是用的队列来写，感觉不是很直观，
 *  还有的只有一个请求然后进行页面解析，根本就没有自动爬起来这也叫爬虫？
 *  因此我结合自己的思路写了一下简单的爬虫，测试用例就是自动抓取我的博客网站（http://www.zifangsky.cn）的所有链接。
 *  算法简介
 *  程序在思路上采用了广度优先算法，对未遍历过的链接逐次发起GET请求，然后对返回来的页面用正则表达式进行解析，
 *  取出其中未被发现的新链接，加入集合中，待下一次循环时遍历。
 *  具体实现上使用了Map<String, Boolean>，键值对分别是链接和是否被遍历标志。
 *  程序中使用了两个Map集合，分别是：oldMap和newMap，初始的链接在oldMap中，
 *  然后对oldMap里面的标志为false的链接发起请求，解析页面，用正则取出<a>标签下的链接，
 *  如果这个链接未在oldMap和newMap中，则说明这是一条新的链接，
 *  同时要是这条链接是我们需要获取的目标网站的链接的话，我们就将这条链接放入newMap中，
 *  一直解析下去，等这个页面解析完成，把oldMap中当前页面的那条链接的值设为true，
 *  表示已经遍历过了。最后是当整个oldMap未遍历过的链接都遍历结束后，
 *  如果发现newMap不为空，则说明这一次循环有新的链接产生，
 *  因此将这些新的链接加入oldMap中，继续递归遍历，
 *  反之则说明这次循环没有产生新的链接，继续循环下去已经不能产生新链接了，因为任务结束，返回链接集合oldMap
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawlerDemo {

	public static void main(String[] args) {
		WebCrawlerDemo webCrawlerDemo = new WebCrawlerDemo();
		webCrawlerDemo.myPrint("http://www.aoyou.com");
	}

	public void myPrint(String baseUrl) {
		Map<String, Boolean> oldMap = new LinkedHashMap<String, Boolean>(); // 存储链接-是否被遍历
																			// 键值对
		String oldLinkHost = "";  //host

		Pattern p = Pattern.compile("(https?://)?[^/\\s]*"); //比如：http://www.zifangsky.cn
		Matcher m = p.matcher(baseUrl);
		if (m.find()) {
			oldLinkHost = m.group();
		}

		oldMap.put(baseUrl, false);
		oldMap = crawlLinks(oldLinkHost, oldMap);
		for (Map.Entry<String, Boolean> mapping : oldMap.entrySet()) {
			System.out.println("链接：" + mapping.getKey());

		}

	}

	/**
	 * 抓取一个网站所有可以抓取的网页链接，在思路上使用了广度优先算法
	 * 对未遍历过的新链接不断发起GET请求，一直到遍历完整个集合都没能发现新的链接
	 * 则表示不能发现新的链接了，任务结束
	 * 
	 * @param oldLinkHost  域名，如：http://www.zifangsky.cn
	 * @param oldMap  待遍历的链接集合
	 * 
	 * @return 返回所有抓取到的链接集合
	 * */
	private Map<String, Boolean> crawlLinks(String oldLinkHost,
			Map<String, Boolean> oldMap) {
		Map<String, Boolean> newMap = new LinkedHashMap<String, Boolean>();
		String oldLink = "";

		for (Map.Entry<String, Boolean> mapping : oldMap.entrySet()) {
			System.out.println("link:" + mapping.getKey() + "--------check:"
					+ mapping.getValue());
			// 如果没有被遍历过
			if (!mapping.getValue()) {
				oldLink = mapping.getKey();
				// 发起GET请求
				try {
					URL url = new URL(oldLink);
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(2000);
					connection.setReadTimeout(2000);

					if (connection.getResponseCode() == 200) {
						InputStream inputStream = connection.getInputStream();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(inputStream, "UTF-8"));
						String line = "";
						Pattern pattern = Pattern
								.compile("<a.*?href=[\"']?((https?://)?/?[^\"']+)[\"']?.*?>(.+)</a>");
						Matcher matcher = null;
						while ((line = reader.readLine()) != null) {
							matcher = pattern.matcher(line);
							if (matcher.find()) {
								String newLink = matcher.group(1).trim(); // 链接
								// String title = matcher.group(3).trim(); //标题
								// 判断获取到的链接是否以http开头
								if (!newLink.startsWith("http")) {
									if (newLink.startsWith("/"))
										newLink = oldLinkHost + newLink;
									else
										newLink = oldLinkHost + "/" + newLink;
								}
								//去除链接末尾的 /
								if(newLink.endsWith("/"))
									newLink = newLink.substring(0, newLink.length() - 1);
								//去重，并且丢弃其他网站的链接
								if (!oldMap.containsKey(newLink)
										&& !newMap.containsKey(newLink)
										&& newLink.startsWith(oldLinkHost)) {
									// System.out.println("temp2: " + newLink);
									newMap.put(newLink, false);
								}
							}
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				oldMap.replace(oldLink, false, true);
			}
		}
		//有新链接，继续遍历
		if (!newMap.isEmpty()) {
			oldMap.putAll(newMap);
			oldMap.putAll(crawlLinks(oldLinkHost, oldMap));  //由于Map的特性，不会导致出现重复的键值对
		}
		return oldMap;
	}

}

