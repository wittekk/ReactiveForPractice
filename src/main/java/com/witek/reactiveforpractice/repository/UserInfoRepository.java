package com.witek.reactiveforpractice.repository;

import com.witek.reactiveforpractice.model.Users;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends ReactiveMongoRepository<Users, String> {

}
