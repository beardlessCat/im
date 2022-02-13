import io.netty.buffer.ByteBufAllocator;

public class Test {

    // static final ByteBufAllocator DEFAULT_ALLOCATOR;
    // static {
    //     // 系统变量中取值，若为安卓平台则使用 unpooled
    //     // String allocType = SystemPropertyUtil.get( "io.netty.allocator.type", PlatformDependent.isAndroid() ? "unpooled" : "pooled");
    //     // allocType = allocType.toLowerCase(Locale.US).trim();
    //     ByteBufAllocator alloc;
    //     if ("unpooled".equals(allocType)) {
    //         alloc = UnpooledByteBufAllocator.DEFAULT;
    //         logger.debug("-Dio.netty.allocator.type: {}", allocType);
    //     } else if ("pooled".equals(allocType)) {
    //         alloc = PooledByteBufAllocator.DEFAULT;
    //         logger.debug("-Dio.netty.allocator.type: {}", allocType);
    //     } else {      // 默认为内存池
    //          alloc = PooledByteBufAllocator.DEFAULT;
    //          logger.debug("-Dio.netty.allocator.type: pooled (unknown: {})", allocType);
    //     }
    //         DEFAULT_ALLOCATOR = alloc;
    //         THREAD_LOCAL_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.threadLocalDirectBufferSize", 0);
    //         logger.debug("-Dio.netty.threadLocalDirectBufferSize: {}", THREAD_LOCAL_BUFFER_SIZE);
    //         MAX_CHAR_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.maxThreadLocalCharBufferSize", 16 * 1024);
    //         logger.debug("-Dio.netty.maxThreadLocalCharBufferSize: {}", MAX_CHAR_BUFFER_SIZE);
    // }
}
