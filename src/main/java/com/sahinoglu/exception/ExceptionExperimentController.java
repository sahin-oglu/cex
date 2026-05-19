package com.sahinoglu.exception;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ExceptionExperimentController {
	@GetMapping("/bizException")
	public void throwBusinessException() {
		throw new BusinessException("bum");
	}

	@GetMapping("/nfException")
	public void throwNotFoundException() {
		throw new NotFoundException("bam");
	}

	@GetMapping("/problematicCheckedException")
	public void throwCheckedException() throws InstantiationException {
		throw new InstantiationException("BOOM, DEADLY TO THE TOUCH");
	}
	
	@GetMapping("/throwable")
	public void throwThrowable() throws Throwable  {
		throw new Throwable("A MESSAGE FROM LA-LI-LU-LE-LO!");
	}

}
