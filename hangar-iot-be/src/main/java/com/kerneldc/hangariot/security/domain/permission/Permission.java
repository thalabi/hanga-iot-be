package com.kerneldc.hangariot.security.domain.permission;

import java.time.LocalDateTime;

import com.kerneldc.hangariot.security.domain.AbstractPersistableEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

//@Entity
//@SequenceGenerator(name = "default_seq_gen", sequenceName = "permission_seq", allocationSize = 1)
@Getter @Setter
public class Permission extends AbstractPersistableEntity {

	private static final long serialVersionUID = 1L;

	//@Column(unique = true)
	@Setter(AccessLevel.NONE)
    private String name;
    private String description; 
    
//    @ManyToMany(mappedBy = "permissionSet")
    //@Transient
    //private Set<Group> groupSet = new HashSet<>(); 


	private LocalDateTime created = LocalDateTime.now();
	private LocalDateTime modified = LocalDateTime.now();
	

	public void setName(String name) {
		this.name = name;
		setLogicalKeyHolder();
	}
	
	@Override
	protected void setLogicalKeyHolder() {
		getLogicalKeyHolder().setLogicalKey(name);
	}

}
