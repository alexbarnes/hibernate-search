// $Id$
package org.hibernate.search.filter;

import java.io.IOException;
import java.util.BitSet;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Filter;
import org.slf4j.Logger;

import org.hibernate.search.util.LoggerFactory;
import org.hibernate.util.SoftLimitMRUCache;

/**
 * A slightly different version of Lucene's original <code>CachingWrapperFilter</code> which
 * uses <code>SoftReferences</code> instead of <code>WeakReferences</code> in order to cache 
 * the filter <code>BitSet</code>.
 * 
 * @author Hardy Ferentschik
 * @see org.apache.lucene.search.CachingWrapperFilter
 * @see <a href="http://opensource.atlassian.com/projects/hibernate/browse/HSEARCH-174">HSEARCH-174</a>
 */
@SuppressWarnings("serial")
public class CachingWrapperFilter extends Filter {
	
	private final Logger log = LoggerFactory.make();
	
	public static final int DEFAULT_SIZE = 5;
	
	private final int size;
	
	/**
	 * The cache using soft references in order to store the filter bit sets.
	 */
	private transient SoftLimitMRUCache cache;
	
	private final Filter filter;

	/**
	 * @param filter
	 *            Filter to cache results of
	 */
	public CachingWrapperFilter(Filter filter) {
		this(filter, DEFAULT_SIZE);
	}
	
	/**
	 * @param filter
	 *            Filter to cache results of
	 */
	public CachingWrapperFilter(Filter filter, int size) {
		this.filter = filter;
		this.size = size;
	}	

	public BitSet bits(IndexReader reader) throws IOException {
		if (cache == null) {
			log.debug("Initialising SoftLimitMRUCache with hard ref size of {}", size);
			cache = new SoftLimitMRUCache(size);
		}

		//memory barrier ensure cache == null will not always stay true on concurrent threads
		synchronized (cache) { // check cache
			BitSet cached = (BitSet) cache.get(reader);
			if (cached != null) {
				return cached;
			}
		}

		final BitSet bits = filter.bits(reader);

		synchronized (cache) { // update cache
			cache.put(reader, bits);
		}

		return bits;
	}

	public String toString() {
		return this.getClass().getName() + "(" + filter + ")";
	}

	public boolean equals(Object o) {
		if (!(o instanceof CachingWrapperFilter))
			return false;
		return this.filter.equals(((CachingWrapperFilter) o).filter);
	}

	public int hashCode() {
		return filter.hashCode() ^ 0x1117BF25;
	}
}
