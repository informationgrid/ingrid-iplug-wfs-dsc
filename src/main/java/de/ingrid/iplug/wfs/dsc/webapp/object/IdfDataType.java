package de.ingrid.iplug.wfs.dsc.webapp.object;

import org.springframework.stereotype.Service;

import de.ingrid.admin.object.AbstractDataType;

@Service
public class IdfDataType extends AbstractDataType {

	public IdfDataType() {
		super("IDF_1.0");
		this.setForceActive(true);
	}
}