package telran.spring.service;

import java.util.List;

import telran.spring.model.Employee;

public interface EmployeesPersistance {

	void store(List<Employee> listEmployees);
	List<Employee> restore();
}
