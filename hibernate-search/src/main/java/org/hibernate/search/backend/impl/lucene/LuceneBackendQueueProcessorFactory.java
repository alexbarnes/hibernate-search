/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat, Inc. and/or its affiliates or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat, Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.search.backend.impl.lucene;

import java.util.Collections;
import java.util.Properties;
import java.util.List;
import java.util.concurrent.locks.Lock;

import org.hibernate.search.backend.spi.BackendQueueProcessor;
import org.hibernate.search.indexes.spi.IndexManager;
import org.hibernate.search.spi.WorkerBuildContext;
import org.hibernate.search.backend.BackendFactory;
import org.hibernate.search.backend.LuceneWork;

/**
 * This will actually contain the Workspace and LuceneWork visitor implementation,
 * reused per-DirectoryProvider.
 * Both Workspace(s) and LuceneWorkVisitor(s) lifecycle are linked to the backend
 * lifecycle (reused and shared by all transactions).
 * The LuceneWorkVisitor(s) are stateless, the Workspace(s) are threadsafe.
 * 
 * @author Emmanuel Bernard
 * @author Sanne Grinovero
 */
public class LuceneBackendQueueProcessorFactory implements BackendQueueProcessor {

	private PerDPResources resources;
	private boolean sync;

	public void initialize(Properties props, WorkerBuildContext context, IndexManager indexManager) {
		sync = BackendFactory.isConfiguredAsSync( props );
		resources = new PerDPResources( context, indexManager, props );
	}

	public void close() {
		resources.shutdown();
	}

	@Override
	public void applyWork(List<LuceneWork> workList) {
		LuceneBackendQueueProcessor luceneBackendQueueProcessor = new LuceneBackendQueueProcessor( workList, resources, sync );
		if ( sync ) {
			luceneBackendQueueProcessor.run();
		}
		else {
			resources.getQueueingExecutor().execute( luceneBackendQueueProcessor );
		}
	}

	@Override
	public void applyStreamWork(LuceneWork singleOperation) {
		List<LuceneWork> singletonList = Collections.singletonList( singleOperation );
		applyWork( singletonList );
	}

	@Override
	public Lock getExclusiveWriteLock() {
		return resources.getExclusiveModificationLock();
	}

	public PerDPResources getIndexResources() {
		return resources;
	}

}
