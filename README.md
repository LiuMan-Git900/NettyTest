![git](https://github.com/user-attachments/assets/f80938c3-331b-407d-bc70-879a00b59889)
123123

// 动静分离--netty只负责传输数据，编解码逻辑放在我们自己的模块中，这么做的目的是为了将不动的东西动静分离
// 把消息解码和消息编码放在HumanObject这里，是因为变化的是协议和协议处理，不变的是协议变成byte序列后的传输，所以netty只负责传输byte协议
// 而编码解码放在我们自己的逻辑对象上，我们自己可编写部分上，动静分离
// 所以我们消息处理和消息发生都是自己的逻辑，真正走到netty上的其实是已经序列化后的byte序列
// netty在设计中不涉及到编解码技术，支付中传输，编解码技术机制放在我们自己这对象

设计思路：逻辑线程的出现
/**
* 现在我们是netty的线程方法来驱动humanObject对象进行编解码，但我们同时需要服务器逻辑线程，因为心跳
* 我们要有心跳，需要提供心跳服务,心跳服务就是将humanObject挂载在服务器线程上。引出逻辑线程的必要性
* 这个时候就需要考虑线程负载均衡了，将不同的humanObject挂载到线程上去。
* 同时设置一个connection对象作为中间对象：netty线程将byte数据存到connection对象中
* 逻辑线程在humanObject心脏中处理connection中的数据。linkeBolckingQueue<byte[]>。
* 1.netty线程不直接操作humanobject逻辑对象，只做数据传承;
* 2.逻辑线程操作humanobject对象的心跳，进行消息的编解码
* 3.connection就是横跨两个线程的中间对象。
* 编解码完成后就进行数据的分发，开始走到humanobject身上的功能模块了，也就是我们程序的价值了
* 问题：文件系统和DB系统在什么时候初始化。
* 文件系统需要服务器启动时进行初始化，DB系统需要登录时进行初始化，所以在创建humanObject对象前需要有一个门对象

DB系统的的一些思考：
有了connection对象，netty线程和逻辑线程职责分离，然后进一步有了门GateObject对象，然后开始预先加载一些DB数据，将一些DB数据推送到客户端。
然后登录完成了，开始创建humanObject对象，这个时候加载DB对象，DB加载完成后将HumanObject对象挂载到逻辑线程中
然后逻辑线程中开始处理消息，DB加载完成了，逻辑线程处理msg，不会出现丢失问题


Connection对象的创建。服务器逻辑线程模型的创建，DB服务的提供（DB系统的接入），Gate对象的创建，断线，重登逻辑的处理。文件系统的接入。DB功能的搭建实战
好处：netty的链接对象-中间层：netty将数据放入到这，humanObject去取去处理。分离netty和我门商业服务器。
    * 有很多好处    1：组件分离：netty代码不持有humanObject对象，netty组件可以打包，netty只做数据传输
    *             2：线程分离：netty线程不持有我们humanObject对象 逻辑线程和netty线程不同时控制这个大额对象。
    *             3：门对象：可以创建门对象，对登录进行过滤，登录和具体服务功能分流*/

2024/9/20/ 21：14
// 工作线程模型搭建的基础思想：netty搭建成功，创建humanobject对象，我们有多个humanObject对象，且每一个对象都需要心跳，所以需要将humanobject对象放到逻辑线程Work中去。
// Work线程管理多个humanobject对象，且提供心跳功能。为了线程负载均衡，我们需要多个work线程，所以多个work线程也需要也该Node进行管理。
// Work是线程提供心跳，Node不是线程，只是负责管理work线程。
// node-worker-humanObject.我们需要用并发容器去存储 ConcurrentHashMap。connction创建时，从node中获取work如何。worker又是node再主线程创造添加的，所以存在多线程问题，需要用并发容器存储

// 门逻辑-黑客攻击无限登录攻击你如何处理
// connetion 的创建-门对象（少许资源加载）-humanObject完整数据加载-进门以后加载所有服
// 废物connetion处理，排队和无效的connetion处理



//需要补充的逻辑
// Netty源码分析
// 线程模型的搭建：基础知识：并发容器，锁，线程，线程周期状态，线程中断逻辑。
// DB服务器框架的搭建
// 文件系统的搭建
// 安全系统的搭建，第三方平台的搭建

// gameStartUp启动器
// 
// GameNode的服务分布
// gameWork-GateService-gateObject，nettywork-netyy服务-netty启动.mulHumanWork-HumanSever


// 哲学思维：解决问题方法
//我们是从底部一个问题一个问题的延续将框架搭建出来的，从下到上通过问题一个一个的搭建出来了。
// 搭建完成后，又有一点从上倒下搭建框架的感觉了，重构的感觉。
// 所以我们先做，先解决问题，然后在解决问题后再来重构，从上倒下解决布局
// 先从下到上解决问题，解决完问题后，再从上到下进行重构优化


