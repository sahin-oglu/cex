package com.sahinoglu.branch;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sahinoglu.center.Center;
import com.sahinoglu.employee.Employee;
import com.sahinoglu.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
public class BranchServiceTest {

	@Mock
	private BranchRepository repository;

	@InjectMocks
	private BranchService service;

	@Test
	void ShouldThrowBusinessExceptionWhenBranchAlreadyInactive() {
		// arrange
		Long branchId = 100l;
		Long centerId = 1l;
		String name = "branch";
		String location = "fatih";
		Boolean active = false;

		Center center = new Center();
		center.setId(centerId);

		Branch branch = new Branch();

		branch.setName(name);
		branch.setId(branchId);
		branch.setLocation(location);
		branch.setActive(active);
		branch.setCenter(center);

		// branch zaten aktif? ne koymam lazim?
		when(repository.findById(branchId)).thenReturn(Optional.of(branch));

		// act

		// assert
		assertThrows(BusinessException.class, () -> {
			service.deactivate(branchId);
		});
		verify(repository).findById(branchId);
		verify(repository, never()).save(any());

	}
}
