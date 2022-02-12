package com.kerneldc.hangariot.security.domain.group;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.kerneldc.hangariot.security.domain.AbstractPersistableEntity;
import com.kerneldc.hangariot.security.domain.permission.Permission;

import lombok.Getter;
import lombok.Setter;

//@Entity
//@SequenceGenerator(name = "default_seq_gen", sequenceName = "group_seq", allocationSize = 1)
@Getter @Setter
public class Group extends AbstractPersistableEntity {

	private static final long serialVersionUID = 1L;

	public static final String PROPERTY_USER_SET = "userSet";
	public static final String PROPERTY_PERMISSION_SET = "permissionSet";

	public static final Function<Group, Object> idExtractor = Group::getId;

	//@Column(unique = true)
	//@Setter(AccessLevel.NONE)
    private String name;
    private String description; 
    
    
    //@ManyToMany(mappedBy = "groupSet")
    //private Set<User> userSet = new HashSet<>(); 
 
    //@ManyToMany
//    @JoinTable(name = "group_permission", 
//        joinColumns = @JoinColumn(name = "group_id"), 
//        inverseJoinColumns = @JoinColumn( name="permission_id")) 
    private Set<Permission> permissionSet = new HashSet<>(); 

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
