## Redis 实现分布式锁

1. 实现过程参考：[redis实现分布式锁](https://blog.csdn.net/ai_xao/article/details/106282538)；

2. 已测试常见用法，执行结果均正常，具体参考 `test` 包下：

   - `JediBlockInterruptilyLockTest`：测试可中断的阻塞锁获取方法；
   - `JedisBlockLockTest`：测试阻塞的锁获取方法；
   - `JedisUnBlockLockTest`：测试非阻塞的锁获取方法；
   - `JedisUnBlockUntilLockTest`：测试在一定时间内尝试获取锁的方法；

3. 推荐用法：参考 `DistributeLock` 类或者参考测试用例；需要注意的是 `unlock` 方法可能会抛出 `IllegalMonitorStateException`，它可能是由于锁过期被销毁导致的。我有理由认为，其它线程可能已经持有了该锁（存在并发风险）。所以，你在调用的时候，应注意到这个现象，并考虑回滚；

4. 主要类简介：

   - `JedisLock`：实现了 `Lock` 接口，负责主要的请求锁、释放锁操作；

     > 如果理解 `juc` 下的  `AbstractQueuedSynchronizer`，理解这个类的代码也很容易，并且这个类的代码结构是参照 `AbstractQueuedSynchronizer` 类的。

   - `JedisUtils`：封装了需要使用到的 `Jedis` 操作；(`redis` 连接也在该类中)

   - `ListKeyConstruct`: 定义可自定义队列名称功能的接口，用于 `JedisLock` 中构造队列名称；

   - `RedisConsts`：一些常量；

   - `DeadLockCheckTask`：参考 [redis实现分布式锁](https://blog.csdn.net/ai_xao/article/details/106282538) 一文中提到的死锁现象，这个任务就是为了解决这个问题；

   - `TopicSubscribeTask`: 订阅指定 topic 的任务；

   - `TaskManager`：分配线程，可用于执行上面的两个任务；

   - `ThreadControl`：持有阻塞的线程资源，并负责 `park` 和 `unpark` 操作；

5. 总结：

   由于并发环境的考虑，所以，在理解代码的同时，多去想想并发环境下，这段代码会不会有问题。

   以上仅适用于 `redis` 单机环境，欢迎讨论！