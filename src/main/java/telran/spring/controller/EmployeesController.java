package telran.spring.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.spring.model.Employee;
import telran.spring.service.EmployeesService;

@RestController
@RequestMapping("employees")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class EmployeesController {

	final EmployeesService employeesService;

	@PostMapping
	public Employee addEmployee(@RequestBody @Valid Employee employeeToAdd) {
		Long id = employeeToAdd.getId();
		if (id != null) {
			log.warn("Exists id {} from client", id);
			employeeToAdd.setId(null);
		}
		Employee res = employeesService.addEmployee(employeeToAdd);
		log.debug("Employee with id {} was added", id);
		return res;
	}

	@PutMapping("/{id}")
	public Employee updateEmployee(@PathVariable long id, @RequestBody @Valid Employee employeeToUpdate) {
		if (employeeToUpdate.getId() != id) {
			throw new IllegalArgumentException("id doesn't exist");
		}
		Employee emplUpdated = employeesService.updateEmployee(id, employeeToUpdate);
		log.debug("Employee with id {} was updated", id);
		return emplUpdated;
	}

	@DeleteMapping("/{id}")
	public void deleteEmployee(@PathVariable Long id) {
		employeesService.deleteEmployee(id);
		log.debug("Employee with id {} has been removed", id);	
	}

	@GetMapping
	public List<Employee> getEmployees() {
		List<Employee> res = employeesService.getEmployees();
		log.trace("All employees are received {}", res);
		return res;
	}

	@GetMapping("/{id}")
	public Employee getEmployee(@PathVariable Long id) {
		return employeesService.getEmployee(id);
	}

}
