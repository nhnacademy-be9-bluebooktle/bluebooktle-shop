package shop.bluebooktle.backend.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import shop.bluebooktle.common.entity.auth.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

	List<Address> findByUser_Id(Long userId);

	long countByUser_Id(Long userId);

	Optional<Address> findByIdAndUser_Id(Long addressId, Long userId);
}