package com.sahinoglu.employee;

import com.sahinoglu.center.Center;

import jakarta.persistence.*;
import lombok.*;

@Data
@Table(name = "employees")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@ManyToOne
	@JoinColumn(name = "center_id", nullable = false)
	private Center center;
}
