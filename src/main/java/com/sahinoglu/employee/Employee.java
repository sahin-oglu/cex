package com.sahinoglu.employee;

import com.sahinoglu.branch.Branch;
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
	private Long id;

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
	// optional true redundant ama dursun
	@ManyToOne(optional = true)
	@JoinColumn(name = "center_id")
	private Center center;
	@ManyToOne(optional = true)
	@JoinColumn(name = "branch_id")
	private Branch branch;
}
