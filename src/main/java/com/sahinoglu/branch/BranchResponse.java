package com.sahinoglu.branch;

public record BranchResponse(Long id, String name, String location, long centerId, boolean active) {
}
