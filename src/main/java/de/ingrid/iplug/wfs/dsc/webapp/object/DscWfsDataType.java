package de.ingrid.iplug.wfs.dsc.webapp.object;

import org.springframework.stereotype.Service;

import de.ingrid.admin.object.AbstractDataType;

@Service
public class DscWfsDataType extends AbstractDataType {

	public DscWfsDataType() {
		super("dsc_wfs");
		this.setForceActive(true);
	}
}