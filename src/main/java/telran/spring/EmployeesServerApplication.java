package telran.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"telran", "telran.spring"})
public class EmployeesServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeesServerApplication.class, args);
	}

}
