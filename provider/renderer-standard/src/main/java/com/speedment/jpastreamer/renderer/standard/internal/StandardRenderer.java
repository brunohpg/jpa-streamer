package com.speedment.jpastreamer.renderer.standard.internal;

import static java.util.Objects.requireNonNull;

import com.speedment.jpastreamer.criteria.Criteria;
import com.speedment.jpastreamer.criteria.CriteriaFactory;
import com.speedment.jpastreamer.interopoptimizer.IntermediateOperationOptimizerFactory;
import com.speedment.jpastreamer.merger.CriteriaMerger;
import com.speedment.jpastreamer.merger.MergerFactory;
import com.speedment.jpastreamer.merger.QueryMerger;
import com.speedment.jpastreamer.pipeline.Pipeline;
import com.speedment.jpastreamer.pipeline.intermediate.IntermediateOperation;
import com.speedment.jpastreamer.renderer.RenderResult;
import com.speedment.jpastreamer.renderer.Renderer;
import com.speedment.jpastreamer.rootfactory.RootFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ServiceLoader;
import java.util.stream.Stream;

public final class StandardRenderer implements Renderer {

    private final EntityManager entityManager;
    private final CriteriaFactory criteriaFactory;

    private final IntermediateOperationOptimizerFactory intermediateOperationOptimizerFactory;

    private final MergerFactory mergerFactory;

    StandardRenderer(final EntityManagerFactory entityManagerFactory) {
        this.entityManager = requireNonNull(entityManagerFactory).createEntityManager();
        this.criteriaFactory = RootFactory.getOrThrow(CriteriaFactory.class, ServiceLoader::load);
        this.intermediateOperationOptimizerFactory = RootFactory.getOrThrow(IntermediateOperationOptimizerFactory.class, ServiceLoader::load);
        this.mergerFactory = RootFactory.getOrThrow(MergerFactory.class, ServiceLoader::load);
    }

    @Override
    public <T> RenderResult<T> render(final Pipeline<T> pipeline) {
        optimizePipeline(pipeline);

        final Class<T> entityClass = pipeline.root();

        final CriteriaMerger criteriaMerger = mergerFactory.createCriteriaMerger();
        final QueryMerger queryMerger = mergerFactory.createQueryMerger();

        final Stream<T> baseStream = baseStream(pipeline, entityManager, entityClass, criteriaMerger, queryMerger);
        final Stream<T> replayed = replay(baseStream, pipeline);

        return new StandardRenderResult<>(
            replayed,
            pipeline.terminatingOperation()
        );
    }

     private <T> Stream<T> baseStream(
         final Pipeline<T> pipeline,
         final EntityManager entityManager,
         final Class<T> entityClass,
         final CriteriaMerger criteriaMerger,
         final QueryMerger queryMerger
     ) {
         final Criteria<T> criteria = criteriaFactory.createCriteria(entityManager, entityClass);
         criteria.getQuery().select(criteria.getRoot());

         criteriaMerger.merge(pipeline, criteria);

         final TypedQuery<T> typedQuery = entityManager.createQuery(criteria.getQuery());

         queryMerger.merge(pipeline, typedQuery);

         return typedQuery.getResultStream();
     }

     @SuppressWarnings({"rawtypes", "unchecked"})
    private <T> Stream<T> replay(final Stream<T> stream, final Pipeline<T> pipeline) {
        Stream<T> decorated = stream;

        for (IntermediateOperation intermediateOperation : pipeline.intermediateOperations()) {
            decorated = (Stream<T>) intermediateOperation.function().apply(decorated);
        }

        return decorated;
    }

    private <T> void optimizePipeline(final Pipeline<T> pipeline) {
        intermediateOperationOptimizerFactory.stream().forEach(intermediateOperationOptimizer -> intermediateOperationOptimizer.optimize(pipeline));
    }

    @Override
    public void close() {
        entityManager.close();
    }
}