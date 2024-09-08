package cooktop;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CooktopModel {
    
    private final List<CookZoneModel> cookZones = new ArrayList<>(); 
    
    public CooktopModel() {
        var leftTop = new CookZoneModel(0,300,40f, 1f);
        var rightTop = new CookZoneModel(1,150,40f, 1f);
        var leftBottom = new CookZoneModel(2,150,40f, 1f);
        var rightBottom = new CookZoneModel(3,300,40f, 1f);
        
        cookZones.add(leftTop);
        cookZones.add(rightTop);
        cookZones.add(leftBottom);
        cookZones.add(rightBottom);
    }
}
