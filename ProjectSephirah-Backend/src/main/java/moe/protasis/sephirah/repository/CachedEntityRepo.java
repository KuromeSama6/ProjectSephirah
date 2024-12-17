package moe.protasis.sephirah.repository;

import moe.protasis.sephirah.data.cache.CachedEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CachedEntityRepo extends MongoRepository<CachedEntity<?>, String> {
    @Query("{ 'path': ?0}")
    CachedEntity<?> findByPathAndType(String path);
}
