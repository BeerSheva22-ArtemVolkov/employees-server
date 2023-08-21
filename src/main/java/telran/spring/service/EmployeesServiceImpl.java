package telran.spring.service;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static telran.spring.api.EmployeesConfig.*;

import telran.spring.exceptions.NotFoundException;
import telran.spring.model.Employee;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeesServiceImpl implements EmployeesService, EmployeesPersistance {

	final SimpMessagingTemplate notifier;

	ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	ReadLock readLock = lock.readLock();
	WriteLock writeLock = lock.writeLock();
	private Map<Long, Employee> emplsStorage = new HashMap<Long, Employee>();

	@Override
	public List<Employee> getEmployees() {
		readLock.lock();
		try {
			return new ArrayList<Employee>(emplsStorage.values());
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public Employee addEmployee(Employee employeeToAdd) {

		writeLock.lock();

		Long id = employeeToAdd.getId();

		if (id == null) {
			id = generateID();
			employeeToAdd.setId(id);
		}

		try {
			Employee res = emplsStorage.putIfAbsent(id, employeeToAdd);
			if (res != null) {
				throw new RuntimeException("Employee with id " + id + " already exists");
			}
			NotifierAction action = new NotifierAction(ActionType.ADD, employeeToAdd);
			notifier.convertAndSend("/topic/employees", action); // ДОЛЖНО СОВПАДАТЬ С TOPIC ИЗ EmployeesServiceRest
			return employeeToAdd;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void deleteEmployee(Long id) {
		writeLock.lock();
		try {
			Employee empl = emplsStorage.remove(id);
			if (empl == null) {
				throw new NotFoundException("Not found" + id);
			}
			NotifierAction action = new NotifierAction(ActionType.DELETE, id);
			notifier.convertAndSend("/topic/employees", action); 
		} finally {
			writeLock.unlock();
		}

	}

	@Override
	public Employee updateEmployee(Long id, Employee employeeToAdd) {
		writeLock.lock();
		try {
			if (!emplsStorage.containsKey(id)) {
				throw new NotFoundException("Employee wtih id = " + id + " not found");
			}
			Employee res = emplsStorage.put(id, employeeToAdd);
			NotifierAction action = new NotifierAction(ActionType.UPDATE, employeeToAdd);
			notifier.convertAndSend("/topic/employees", action); 
			return res;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public Employee getEmployee(Long id) {
		Employee empl = emplsStorage.get(id);
		if (empl == null) {
			throw new NotFoundException("Not found employee ");
		}
		return null;
	}

	@PreDestroy
	void storeEmployees() {
		store(emplsStorage.values().stream().toList());
	}

	@Override
	public void store(List<Employee> listEmployees) {
		try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
			outputStream.writeObject(listEmployees);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		log.info("Employees are saved to file \"{}\"", fileName);
	}

	@PostConstruct
	private void restoreEmployees() {
		restore().forEach(e -> addEmployee(e));
	}

	@SuppressWarnings("unchecked")
	public List<Employee> restore() {
		List<Employee> res = new ArrayList<Employee>();
		try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName))) {
			var input = inputStream.readObject();
			log.info("Employees are restored from file \"{}\"", fileName);
			res = (List<Employee>) input;

		} catch (FileNotFoundException e) {
			log.warn("No file \"{}\" was found - no advertisment data was restored", fileName);
		} catch (Exception e) {
			log.warn("Service cannot restore advertisments: ", e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
		return res;
	}

	private Long generateID() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		long randomId;
		do {
			randomId = random.nextInt(MIN_ID, MAX_ID);
		} while (emplsStorage.containsKey(randomId));
		return randomId;
	}

}
