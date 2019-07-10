package com.sportologic.ruianclient.model;

import java.util.Comparator;

public class RuianRegion {
	
	public static final Comparator<RuianRegion> REGION_COMP =
			new Comparator<RuianRegion>() {
				public int compare(RuianRegion reg1, RuianRegion reg2) {
					String reg1Name = reg1.getRegionName();
					String reg2Name = reg2.getRegionName();
		            return reg1Name.compareTo(reg2Name);
				}
		};
		
	private String regionId;
	private String regionName;
	
	public String getRegionId() {
		return regionId;
	}
	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	
	@Override
	public String toString() {
		return "RuianRegion [regionId=" + regionId + ", regionName=" + regionName + "]";
	}
}
