package com.fosung.framework.common.support.dao.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
@Access(AccessType.FIELD)
@Data
public class AppJpaIdEntity implements Serializable {
	
	private static final long serialVersionUID = 1L ;

	@Id
	@GeneratedValue(generator = "generatedkey")
	@GenericGenerator(name = "generatedkey", strategy = "com.fosung.framework.common.support.dao.entity.id.AppJpaEntityLongIDGenerator")
	@Column
	protected Long id ;

}
