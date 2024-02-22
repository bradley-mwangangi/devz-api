package com.devz.api.role;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleDataLoader implements ApplicationRunner {

    private final RoleRepository roleRepository;

    public RoleDataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            Role existingRole = roleRepository.findByRoleName(roleEnum.name());
            if (existingRole == null) {
                Role newRole = new Role();
                newRole.setRoleName(roleEnum.name());

                roleRepository.save(newRole);
            }
        }
    }
}
