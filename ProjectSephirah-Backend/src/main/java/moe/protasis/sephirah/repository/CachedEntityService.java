package moe.protasis.sephirah.repository;

import lombok.extern.slf4j.Slf4j;
import moe.protasis.sephirah.data.cache.CachedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CachedEntityService {
    @Autowired
    private CachedEntityRepo repo;

    public <T> CachedEntity<T> GetEntity(String path, Class<T> clazz) {
        var ret = repo.findByPathAndType(path);
        if (ret == null) {
            return null;
        }
        return (CachedEntity<T>) ret;
    }

}
