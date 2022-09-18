package com.agileboot.domain.system.role;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateStatusCommand {

    public UpdateStatusCommand(Long roleId, Integer status) {
        this.roleId = roleId;
        this.status = status;
    }

    @NotNull
    @Positive
    private Long roleId;

    private Integer status;

}
