package com.sahinoglu.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.sahinoglu.branch.Branch;
import com.sahinoglu.branch.BranchRepository;
import com.sahinoglu.center.Center;
import com.sahinoglu.center.CenterRepository;
import com.sahinoglu.employee.Employee;
import com.sahinoglu.employee.EmployeeRepository;
import com.sahinoglu.employee.EmployeeRequest;
import com.sahinoglu.employee.EmployeeResponse;
import com.sahinoglu.employee.EmployeeService;
import com.sahinoglu.employee.Role;
import com.sahinoglu.exception.BusinessException;

/**
 * Mockito ile AAA şablonuna göre bir unit test. İlk unit testim. 1 happy path,
 * 2 exception path
 * 
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

	@Mock
	private EmployeeRepository repository;

	@Mock
	private BranchRepository branchRepository;

	@Mock
	private CenterRepository centerRepository;

	@InjectMocks
	private EmployeeService employeeService;

	@Test
	void shouldCreateBranchOperatorWhenRequestIsValid() {
		// Arrange kısmı
		Long centerId = 1L;
		Long branchId = 10L;
		EmployeeRequest request = new EmployeeRequest();
		request.setUsername("branch.operator");
		request.setPassword("123456");
		request.setFirstName("Ali");
		request.setLastName("Yilmaz");
		request.setRole(Role.BRANCH_OPERATOR);
		Center center = new Center();
		center.setId(centerId);
		Branch branch = new Branch();
		branch.setId(branchId);
		branch.setCenter(center);
		request.setCenterId(centerId);
		request.setBranchId(branchId);

		// ---

		Employee savedEmployee = new Employee();
		savedEmployee.setId(100L);
		savedEmployee.setUsername(request.getUsername());
		savedEmployee.setPassword(request.getPassword());
		savedEmployee.setFirstName(request.getFirstName());
		savedEmployee.setLastName(request.getLastName());
		savedEmployee.setRole(request.getRole());
		savedEmployee.setCenter(center);
		savedEmployee.setBranch(branch);
		// unique username
		when(repository.existsByUsername(request.getUsername())).thenReturn(false);
		// branch exists
		when(branchRepository.findById(branchId)).thenReturn(Optional.of(branch));
//center exists
		when(centerRepository.findById(centerId)).thenReturn(Optional.of(center));
//returns employee when saved
		when(repository.save(any(Employee.class))).thenReturn(savedEmployee);

		// Act kısmı
		EmployeeResponse response = employeeService.create(request);

		// Assert kısmı
		assertEquals(100L, response.getId());
		assertEquals("branch.operator", response.getUsername());
		assertEquals("Ali", response.getFirstName());
		assertEquals("Yilmaz", response.getLastName());
		assertEquals(Role.BRANCH_OPERATOR, response.getRole());
		assertEquals(centerId, response.getCenterId());
		assertEquals(branchId, response.getBranchId());

		verify(repository).save(any(Employee.class));

	}

	@Test
	void shouldThrowBusinessExceptionWhenUsernameAlreadyExists() {
		// Arrange
		EmployeeRequest request = new EmployeeRequest();
		request.setUsername("existing.user");
		request.setPassword("123456");
		request.setFirstName("Ali");
		request.setLastName("Yilmaz");
		request.setRole(Role.CENTER_OPERATOR);
		request.setCenterId(1L);
		request.setBranchId(null);
		//username not unique
		when(repository.existsByUsername(request.getUsername())).thenReturn(true);

		// Act, ve Assert
		assertThrows(BusinessException.class, () -> {
			employeeService.create(request);
		});

//		verify(repository, never()).save(any(Employee.class));
	}

	@Test
	void shouldThrowBusinessExceptionWhenBranchDoesNotBelongToGivenCenter() {
		// Arrange
		Long requestedCenterId = 1L;
		Long actualBranchCenterId = 2L;
		Long branchId = 10L;

		EmployeeRequest request = new EmployeeRequest();
		request.setUsername("branch.operator");
		request.setPassword("123456");
		request.setFirstName("Ali");
		request.setLastName("Yilmaz");
		request.setRole(Role.BRANCH_OPERATOR);
		request.setCenterId(requestedCenterId);
		request.setBranchId(branchId);

		Center actualBranchCenter = new Center();
		actualBranchCenter.setId(actualBranchCenterId);

		Branch branch = new Branch();
		branch.setId(branchId);
		branch.setCenter(actualBranchCenter);

		when(branchRepository.findById(branchId)).thenReturn(Optional.of(branch));

		// Act + Assert
		assertThrows(BusinessException.class, () -> {
			employeeService.create(request);
		});

		verify(repository, never()).save(any(Employee.class));
	}
}