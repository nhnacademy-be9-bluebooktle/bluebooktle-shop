package shop.bluebooktle.backend.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.common.entity.auth.Address;
import shop.bluebooktle.common.entity.auth.User;

public interface AddressRepository extends JpaRepository<Address, Long> {

	List<Address> findByUser(User user);

	long countByUser(User user);
}