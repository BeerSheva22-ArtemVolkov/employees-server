package telran.spring.service;

import java.util.List;

import telran.spring.model.Employee;

public interface EmployeesService {

	List<Employee> getEmployees();
	
	Employee getEmployee(Long id);
	
	Employee addEmployee(Employee employeeToAdd);
	
	void deleteEmployee(Long id);
	
	Employee updateEmployee(Long id, Employee employeeToAdd);
}
