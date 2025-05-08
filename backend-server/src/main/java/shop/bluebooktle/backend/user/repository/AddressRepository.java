package shop.bluebooktle.backend.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.backend.user.entity.Address;
import shop.bluebooktle.backend.user.entity.User;

public interface AddressRepository extends JpaRepository<Address, Long> {

	List<Address> findByUser(User user);

	long countByUser(User user);
}