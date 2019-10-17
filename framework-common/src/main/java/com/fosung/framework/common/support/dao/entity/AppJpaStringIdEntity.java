package com.fosung.framework.common.support.dao.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
@Access(AccessType.FIELD)
@Data
public class AppJpaStringIdEntity implements Serializable {
	
	private static final long serialVersionUID = 1L ;

	@Id
	@GeneratedValue(generator = "generatedkey")
	@GenericGenerator(name = "generatedkey", strategy = "com.fosung.framework.common.support.dao.entity.id.AppJpaEntityStringIDGenerator")
	@Column(length=32)
	protected String id ;
	
	/**
	 * 设置id，如果id为空字符，则id=null
	 * @param id
	 */
	public void setId(String id){
		this.id = StringUtils.isNotBlank(id) ? id : null ;
	}
	
}
