package com.ies.repositories;


import com.ies.entities.AppEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppRepo extends JpaRepository<AppEntity, Long> {

	@Query(value = "from AppEntity")
	public List<AppEntity> fetchUserApps();

//	@Query(value = "from AppEntity where userId =:userId")
//	public List<AppEntity> fetchCwApps(Integer userId);

	@Query(value = "from AppEntity where userId = :userId")
	public List<AppEntity> fetchCwApps(@Param("userId") Integer userId);

}
