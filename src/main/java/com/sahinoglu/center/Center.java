package com.sahinoglu.center;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "centers")
@Data
public class Center {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;
	private String location;
	private boolean active = true;
}