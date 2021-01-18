/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.iplug.wfs.dsc;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;

import de.ingrid.admin.IConfig;
import de.ingrid.admin.command.PlugdescriptionCommandObject;
import de.ingrid.utils.PlugDescription;

@org.springframework.context.annotation.Configuration
public class Configuration implements IConfig {

    //private static Log log = LogFactory.getLog(Configuration.class);

    @Value("${plugdescription.serviceUrl:}")
    public String serviceUrl;

    @Value("${plugdescription.featurePreviewLimit:0}")
    public String featurePreviewLimit;

    @Override
    public void initialize() {
    }

    @Override
    public void addPlugdescriptionValues( PlugdescriptionCommandObject pdObject ) {
        pdObject.put( "iPlugClass", "de.ingrid.iplug.wfs.dsc.WfsDscSearchPlug");

    	// add necessary fields so iBus actually will query us
        // remove field first to prevent multiple equal entries
        pdObject.removeFromList(PlugDescription.FIELDS, "incl_meta");
        pdObject.addField("incl_meta");
        pdObject.removeFromList(PlugDescription.FIELDS, "t01_object.obj_class");
        pdObject.addField("t01_object.obj_class");
        pdObject.removeFromList(PlugDescription.FIELDS, "metaclass");
        pdObject.addField("metaclass");

        pdObject.put("serviceUrl", serviceUrl);
        pdObject.put("featurePreviewLimit", featurePreviewLimit);
    }

    @Override
    public void setPropertiesFromPlugdescription( Properties props, PlugdescriptionCommandObject pd ) {
        props.setProperty("plugdescription.serviceUrl", serviceUrl);
        props.setProperty("plugdescription.featurePreviewLimit", featurePreviewLimit);
    }
}
