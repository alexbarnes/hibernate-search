//$Id$
package org.hibernate.search;

/**
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 */
public final class Environment {
	/**
	 * Enable listeners auto registration in Hibernate Annotations and EntityManager. Default to true.
	 */
	public static final String AUTOREGISTER_LISTENERS = "hibernate.search.autoregister_listeners";

	/**
	 * Lucene analyser
	 */
	public static final String ANALYZER_CLASS = "hibernate.search.analyzer";

	public static final String WORKER_PREFIX = "hibernate.search.worker.";
	public static final String WORKER_SCOPE = WORKER_PREFIX + "scope";
	public static final String WORKER_BACKEND = WORKER_PREFIX + "backend";
	public static final String WORKER_EXECUTION = WORKER_PREFIX + "execution";

	/**
	 * Defines the maximum number of indexing operation batched per transaction
	 */
	public static final String WORKER_BATCHSIZE = WORKER_PREFIX + "batch_size";

	/**
	 * only used then execution is async
	 * Thread pool size
	 * default 1
	 */
	public static final String WORKER_THREADPOOL_SIZE = Environment.WORKER_PREFIX + "thread_pool.size";
	
	/**
	 * Size of the buffer queue (besides the thread pool size)
	 * <ul>
	 * 	<li>only used then execution is async</li>
	 * 	<li>default infinite</li>
	 * </ul>
	 */
	public static final String WORKER_WORKQUEUE_SIZE = Environment.WORKER_PREFIX + "buffer_queue.max";

	/**
	 * define the reader prefix
	 */
	public static final String READER_PREFIX = "hibernate.search.reader.";
	
	/**
	 * define the reader strategy used
	 */
	public static final String READER_STRATEGY = READER_PREFIX + "strategy";
	/**
	 * filter caching strategy class (must have a no-arg constructor and implements FilterCachingStrategy)
	 */
	public static final String FILTER_CACHING_STRATEGY = "hibernate.search.filter.cache_strategy";

}
