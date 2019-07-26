# 次元番



应用简介
========

专为二次元爱好者量身打造的番剧聚合平台(伪)，包括但不限于动漫，宅舞，音乐，电视剧，电影，直播等。【分区】包含多达20个的视频播放源。基本涵盖绝大多数你能在其他网站看得到的普通/VIP的视频。可能有些分区视频加载过慢，或者找不到相关视频的时候，可以去其他分区查找。这里不仅包含时下最热门的番剧内容，更有好玩有趣的二次元游戏、应用，供你与二次元的小伙伴交流互动！次元番，欢迎喜欢二次元的你！！

功能简介
========

1.番剧电影电视剧在线观看<br/>
2.央视地方特色TV在线播放<br/>
3.ACG游戏应用预览下载<br/>
4.番剧电视剧电影下载<br/>
5.各式视频播放黑科技<br/>
6.美女热舞在线直播<br/>
7.最新热门短视频播放<br/>
8.New 支持M3U8视频下载缓存 -> [M3u8Download](https://github.com/fanchen001/M3u8Download)<br/>
9.New 支持下载缓存西瓜视频 -> [XiguaP2p](https://github.com/fanchen001/XiguaP2p)<br/>
10.New 支持H264视频播放<br/>
11.New 支持HTTP/HTTPS/FTP/ED2K/THUNDER/MAGNET下载<br/>
12.New 使用新的视频嗅探解析工具,支持解析更多在线视频 -> [Sniffing](https://github.com/fanchen001/Sniffing)

关于新增视频播放源
========

SET.1   在 [RetrofitSource](https://github.com/fanchen001/Bangumi/blob/master/app/src/main/java/com/fanchen/imovie/annotation/RetrofitSource.java) 和 [JsoupSource](https://github.com/fanchen001/Bangumi/blob/master/app/src/main/java/com/fanchen/imovie/annotation/JsoupSource.java) 增加相应的视频源枚举类型,例如：JsoupSource.BILIBILI,RetrofitSource.BILI_API。

SET.2   然后在 [RetrofitManager](https://github.com/fanchen001/Bangumi/blob/master/app/src/main/java/com/fanchen/imovie/retrofit/RetrofitManager.java)   的static静态方法中给 retrofitMap.put 一个视频源地址。

SET.3   编写一个parser类XxxImpl 实现 [IVideoParser](https://github.com/fanchen001/Bangumi/blob/master/app/src/main/java/com/fanchen/imovie/jsoup/IVideoParser.java)或者 [IVideoMoreParser](https://github.com/fanchen001/Bangumi/blob/master/app/src/main/java/com/fanchen/imovie/jsoup/IVideoMoreParser.java) ,这个类是核心。主要用来解析网页。提取程序关心的数据

SET.4   编写一个serivce 接口 XxxService 具体可以参考 [KankanService](https://github.com/fanchen001/Bangumi/blob/master/app/src/main/java/com/fanchen/imovie/retrofit/service/KankanService.java) 

SET.5  在 [JsoupVideoResponseCoverter](https://github.com/fanchen001/Bangumi/blob/master/app/src/main/java/com/fanchen/imovie/retrofit/coverter/JsoupVideoResponseCoverter.java) 的静态方法注册 map.put(JsoupSource.XXX, new XxxImpl());

SET.6   编写一个pagerAdapter XxxPagerAdapter 参考 [KankanPagerAdapter](https://github.com/fanchen001/Bangumi/blob/master/app/src/main/java/com/fanchen/imovie/adapter/pager/KankanPagerAdapter.java)

SET.7   在 [VideoTabActivity](https://github.com/fanchen001/Bangumi/blob/master/app/src/main/java/com/fanchen/imovie/activity/VideoTabActivity.java)  getAdapter 方法里增加if分支，返回XxxPagerAdapter

SET.8   在 [category.json](https://github.com/fanchen001/Bangumi/blob/master/app/src/main/assets/category.json) 增加视频源。大功告成

声明
=======

软件风格高仿bilibili。该项目为个人学习项目，软件视频等资源均来自互联网，与作者本人无关。本程序也不负责存储相应视频，仅提供浏览服务。如果有内容侵权，请联系作者。作者将及时移除相关功能及内容。任何组织或个人不可将本项目做商业用途，在不违反其他视频相关利益的情况下，可随意使用本程序的代码。

软件截图
=======

 ![image](https://github.com/fanchen001/Bangumi/blob/master/app/screenshot/screenshot_001.png)
 ![image](https://github.com/fanchen001/Bangumi/blob/master/app/screenshot/screenshot_002.png)
 ![image](https://github.com/fanchen001/Bangumi/blob/master/app/screenshot/screenshot_003.png)
 ![image](https://github.com/fanchen001/Bangumi/blob/master/app/screenshot/screenshot_004.png)
 ![image](https://github.com/fanchen001/Bangumi/blob/master/app/screenshot/screenshot_005.png)
 ![image](https://github.com/fanchen001/Bangumi/blob/master/app/screenshot/screenshot_006.png)
 ![image](https://github.com/fanchen001/Bangumi/blob/master/app/screenshot/screenshot_007.png)
 ![image](https://github.com/fanchen001/Bangumi/blob/master/app/screenshot/screenshot_008.png)
 ![image](https://github.com/fanchen001/Bangumi/blob/master/app/screenshot/screenshot_009.png)
 ![image](https://github.com/fanchen001/Bangumi/blob/master/app/screenshot/screenshot_010.png)
 ![image](https://github.com/fanchen001/Bangumi/blob/master/app/screenshot/screenshot_011.png)
 ![image](https://github.com/fanchen001/Bangumi/blob/master/app/screenshot/screenshot_012.png)
 ![image](https://github.com/fanchen001/Bangumi/blob/master/app/screenshot/screenshot_013.png)
