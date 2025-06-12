package shop.bluebooktle.backend.user.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import shop.bluebooktle.backend.user.service.AddressService;
import shop.bluebooktle.common.exception.auth.InvalidTokenException;

public class AddressControllerUnitTest {

	private final AddressController addressController = new AddressController(mock(AddressService.class));

	@Test
	void checkPrincipal_is_null_throws() {
		assertThrows(InvalidTokenException.class, () ->
			ReflectionTestUtils.invokeMethod(addressController, "checkPrincipal", (Object) null)
		);
	}

}
