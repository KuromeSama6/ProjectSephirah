package moe.protasis.sephirah.repository;

import moe.protasis.sephirah.data.auth.ProxiedAuthKeyPair;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProxiedAuthRepo extends MongoRepository<ProxiedAuthKeyPair, String> {
}
