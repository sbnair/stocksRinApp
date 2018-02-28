package org.stocksrin.fiidii;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/fiiDiiService")
public class FIIDIIRestService {

	//http://localhost:8080/rest/fiiDiiService/fiiData
	@GET
	@Path("/fiiData")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Map<String, FIIDIIDataModle> getExpiryList() {
		return FIIDIIdataModelMap.getfIIDIIDataModleData();
	}
}