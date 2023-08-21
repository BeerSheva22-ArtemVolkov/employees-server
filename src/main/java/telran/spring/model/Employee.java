package telran.spring.model;

import java.io.Serializable;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee implements Serializable {

	private static final long serialVersionUID = 1L;

	Long id;

	@NotEmpty
	public String birthDate;

	@NotEmpty
	public String name;

	@NotEmpty
	public String department;

	@Min(value = 100, message = "less than min value")
	public int salary;

	@NotEmpty
	public String gender;

}
